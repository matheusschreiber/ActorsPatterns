package demo;

import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Transmitter extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public Transmitter() {}

    // Static function creating actor
	public static Props createActor() {
		return Props.create(Transmitter.class, () -> {
			return new Transmitter();
		});
	}

    @Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(MyMessage.class, this::receiveFunction)
			.build();
	  }

	public void receiveFunction(MyMessage m){
		ActorRef destination = (ActorRef) m.destination;
		log.info("Transmitter forwarding message to " + destination.path().name());
		destination.forward(m, getContext());
	}

}
