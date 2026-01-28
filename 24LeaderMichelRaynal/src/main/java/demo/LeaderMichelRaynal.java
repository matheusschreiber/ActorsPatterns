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
public class LeaderMichelRaynal {

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

		// Obs.: The idMax is initialized as -1 here because the 
		// first actor election id is already 0. This is changeable.

		// [...] I (D) received a reference: A
		// [...] I (C) received a reference: D
		// [...] I (B) received a reference: C
		// [...] I (A) received a reference: B
		// [...] I (A) am starting an election.
		// [...] I (B) received a higher election ID: 0. Forwarding election message.
		// [...] I (C) received a higher election ID: 0. Forwarding election message.
		// [...] I (D) received a higher election ID: 0. Forwarding election message.
		// [...] I (B) acknowledge the elected leader with ID: 0
		// [...] I (C) acknowledge the elected leader with ID: 0
		// [...] I (D) acknowledge the elected leader with ID: 0
		// [...] I (A) am the elected leader!


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
