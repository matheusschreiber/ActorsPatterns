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
public class SimpleLeaderElectionAlgorithm {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		final LoggingAdapter log = Logging.getLogger(system, "main");

		final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		final ActorRef[] actors = new ActorRef[4];
		for (int i = 0; i < actors.length; i++) {
			actors[i] = system.actorOf(MyActor.createActor(i), alphabet.charAt(i) + "");
		}

		final int[][] adjacency_matrix = {
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1},
			{1, 0, 0, 0},
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
			actors[alphabet.indexOf('A')],
			new MyMessage("start", 1),
			system.dispatcher(),
			ActorRef.noSender()
		);

		// [...] I (C) received a reference: D
		// [...] I (A) received a reference: B
		// [...] I (B) received a reference: C
		// [...] I (D) received a reference: A
		// [...] I (A) am starting an election.
		// [...] I (B) received a lower election ID: 0. Starting my own election.
		// [...] I (C) received a lower election ID: 1. Starting my own election.
		// [...] I (D) received a lower election ID: 2. Starting my own election.
		// [...] I (A) received a higher election ID: 3. Forwarding election message.
		// [...] I (B) received a higher election ID: 3. Forwarding election message.
		// [...] I (C) received a higher election ID: 3. Forwarding election message.
		// [...] I (A) acknowledge the elected leader with ID: 3
		// [...] I (B) acknowledge the elected leader with ID: 3
		// [...] I (C) acknowledge the elected leader with ID: 3
		// [...] I (D) am the elected leader!


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
}
