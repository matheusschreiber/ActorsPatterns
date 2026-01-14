package demo;

import java.time.Duration;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;

public class MyActor extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorRef broadcaster;

	public MyActor() {}

	// Static function creating actor
	public static Props createActor() {
		return Props.create(MyActor.class, () -> {
			return new MyActor();
		});
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(MyMessage.class, this::receiveFunction)
			.build();
	}

	public void receiveFunction(MyMessage m){
		log.info("Actor " + getSelf().path().name() + " received message: " + m.string);
	}

}
