package demo;

import java.util.ArrayList;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * @author Matheus SCHREIBER
 * @description
 */
public class MessageWithTransmitter {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		final LoggingAdapter log = Logging.getLogger(system, "main");

		final ActorRef a = system.actorOf(MyActorA.createActor(), "A");
        final ActorRef b = system.actorOf(MyActorB.createActor(), "B");

        final ActorRef transmitter = system.actorOf(Transmitter.createActor(), "transmitter");

		log.info("Main sending transmitter and destination references to ActorA");
		a.tell(new MyMessage(transmitter, b), ActorRef.noSender());

		log.info("Main sending 'start' to ActorA");
		a.tell(new MyMessage("start"), ActorRef.noSender());
		
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
