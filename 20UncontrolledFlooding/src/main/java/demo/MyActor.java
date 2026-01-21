package demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;

public class MyActor extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private List<ActorRef> references = new ArrayList<>();

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
		if (m.string != null) {
			log.info("I (" + getSelf().path().name() + ") received a message: " + m.string);
			for (ActorRef ref : this.references) {
				log.info("I (" + getSelf().path().name() + ") am sending a message to " + ref.path().name());
				ref.tell(new MyMessage(m.string), getSelf());
			}
		} else if (m.reference != null) {
			this.references.add(m.reference);
			log.info("I (" + getSelf().path().name() + ") received a reference: " + m.reference.path().name());
		}
	}

}
