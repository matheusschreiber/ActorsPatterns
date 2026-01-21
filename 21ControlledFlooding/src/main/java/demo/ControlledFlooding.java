package demo;

import java.time.Duration;
import java.util.ArrayList;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Matheus SCHREIBER
 * @description
 */
public class ControlledFlooding {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		final LoggingAdapter log = Logging.getLogger(system, "main");

		final ActorRef[] actors = new ActorRef[5];
		actors[0] = system.actorOf(MyActor.createActor(), "A");
		actors[1] = system.actorOf(MyActor.createActor(), "B");
		actors[2] = system.actorOf(MyActor.createActor(), "C");
		actors[3] = system.actorOf(MyActor.createActor(), "D");
		actors[4] = system.actorOf(MyActor.createActor(), "E");

		final int[][] adjacency_matrix = {
			{0, 1, 1, 0, 0},
			{0, 0, 0, 1, 0},
			{0, 0, 0, 1, 0},
			{0, 0, 0, 0, 1},
			{0, 1, 0, 0, 0} 
		};

		for (int i = 0; i < adjacency_matrix.length; i++) {
			ActorRef sender = actors[i];
			for (int j = 0; j < adjacency_matrix[i].length; j++) {
				if (adjacency_matrix[i][j] == 1) {
					ActorRef receiver = actors[j];
					sender.tell(new MyMessage(receiver), ActorRef.noSender());
				}
			}
		}

		system.scheduler().scheduleOnce(
			Duration.ofSeconds(2),
			actors[0],
			new MyMessage("hi", 1),
			system.dispatcher(),
			ActorRef.noSender()
		);

		system.scheduler().scheduleOnce(
			Duration.ofSeconds(3),
			actors[0],
			new MyMessage("hello", 2),
			system.dispatcher(),
			ActorRef.noSender()
		);

		system.scheduler().scheduleOnce(
			Duration.ofSeconds(4),
			actors[0],
			new MyMessage("oi", 3),
			system.dispatcher(),
			ActorRef.noSender()
		);

        
		// We wait 5 seconds before ending system (by default)
		// But this is not the best solution.
		try {
			waitBeforeTerminate();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			system.terminate();
		}
	}

	public static void waitBeforeTerminate() throws InterruptedException {
		Thread.sleep(5000);
	}

	public static void sleepFor(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
