package demo;

import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;

public class MyActorA extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorRef transmitter;
	private ActorRef destination;

	public MyActorA() {}

	// Static function creating actor
	public static Props createActor() {
		return Props.create(MyActorA.class, () -> {
			return new MyActorA();
		});
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(MyMessage.class, this::receiveFunction)
			.build();
	}

	public void receiveFunction(MyMessage m){
		if (m.transmitter != null) {
			transmitter = (ActorRef) m.transmitter;
			log.info(getSelf().path().name() + " received transmitter reference");
		}

		if (m.destination != null) {
			destination = (ActorRef) m.destination;
			log.info(getSelf().path().name() + " received destination reference");
		}

		String receivedString = m.string;
		if (receivedString != null) {
			if ("start".equals(receivedString) && transmitter != null && destination != null) {
				log.info(getSelf().path().name() + " received 'start' message, sending 'hello' to " + destination.path().name() + " via Transmitter");
				MyMessage message = new MyMessage("hello", destination);
				transmitter.tell(message, getSelf());
			} else {
				log.info(getSelf().path().name() + " received message: " + receivedString);
			}
		}

	}

}
