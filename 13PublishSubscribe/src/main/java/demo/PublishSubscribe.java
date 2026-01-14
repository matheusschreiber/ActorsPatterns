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
public class PublishSubscribe {

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("system");
		final LoggingAdapter log = Logging.getLogger(system, "main");

		final ActorRef a = system.actorOf(MyActor.createActor(), "A");
        final ActorRef b = system.actorOf(MyActor.createActor(), "B");
		final ActorRef c = system.actorOf(MyActor.createActor(), "C");

        final ActorRef topic1 = system.actorOf(Topic.createActor(), "topic1");
		final ActorRef topic2 = system.actorOf(Topic.createActor(), "topic2");

		final ActorRef publisher1 = system.actorOf(Topic.createActor(), "publisher1");
		final ActorRef publisher2 = system.actorOf(Topic.createActor(), "publisher2");

		topic1.tell(new MyMessage("subscribe"), a);
		topic1.tell(new MyMessage("subscribe"), b);
		topic2.tell(new MyMessage("subscribe"), b);
		topic2.tell(new MyMessage("subscribe"), c);

		system.scheduler().scheduleOnce(
			Duration.ofSeconds(1),
			topic1,
			new MyMessage("hello"),
			system.dispatcher(),
			publisher1
		);

		system.scheduler().scheduleOnce(
			Duration.ofSeconds(2),
			topic2,
			new MyMessage("world"),
			system.dispatcher(),
			publisher2
		);

		system.scheduler().scheduleOnce(
			Duration.ofSeconds(3),
			topic1,
			new MyMessage("unsubscribe"),
			system.dispatcher(),
			a
		);

		system.scheduler().scheduleOnce(
			Duration.ofSeconds(4),
			topic1,
			new MyMessage("hello2"),
			system.dispatcher(),
			publisher1
		);
		
		// topic1.tell(new MyMessage("hello"), publisher1);
		// topic2.tell(new MyMessage("world"), publisher2);
		// topic1.tell(new MyMessage("unsubscribe"), a);
		// topic1.tell(new MyMessage("hello2"), publisher1);

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
