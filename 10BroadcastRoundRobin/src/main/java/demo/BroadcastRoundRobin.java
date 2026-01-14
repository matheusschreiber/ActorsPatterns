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
public class BroadcastRoundRobin {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		final LoggingAdapter log = Logging.getLogger(system, "main");

		final ActorRef a = system.actorOf(MyActor.createActor(), "A");
        final ActorRef b = system.actorOf(MyActor.createActor(), "B");
		final ActorRef c = system.actorOf(MyActor.createActor(), "C");

        final ActorRef broadcaster = system.actorOf(Broadcaster.createActor(), "broadcaster");

		a.tell(new MyMessage(broadcaster), ActorRef.noSender());
		b.tell(new MyMessage(broadcaster), ActorRef.noSender());
		c.tell(new MyMessage(broadcaster), ActorRef.noSender());
		
		a.tell(new MyMessage("trigger"), ActorRef.noSender());

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
