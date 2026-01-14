package demo;

import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;

public class MyActorB extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	public MyActorB() {}

	// Static function creating actor
	public static Props createActor() {
		return Props.create(MyActorB.class, () -> {
			return new MyActorB();
		});
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(MyMessage.class, this::receiveFunction)
			.build();
	}

	public void receiveFunction(MyMessage m){
		ActorRef sender = getSender();
		log.info(getSelf().path().name() + " received 'hello' from " + sender.path().name());

		MyMessage message = new MyMessage("hi");
		log.info(getSelf().path().name() + " sending 'hi' back to " + sender.path().name());
		
		sender.tell(message, getSelf());
	}


}
