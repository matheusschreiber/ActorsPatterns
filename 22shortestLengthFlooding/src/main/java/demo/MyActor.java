package demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;

public class MyActor extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private List<ActorRef> references = new ArrayList<>();
	private HashMap<Integer, Integer> sequenceNumbers = new HashMap<>();

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
			if (sequenceNumbers.containsKey(m.sequenceNumber) && sequenceNumbers.get(m.sequenceNumber) <= m.length) {
				log.info("I (" + getSelf().path().name() + ") already processed this message: " + m.string + " with length " + m.length);
				return;
			}
			sequenceNumbers.put(m.sequenceNumber, m.length);
			log.info("I (" + getSelf().path().name() + ") received a message: " + m.string + " with length " + m.length);
			for (ActorRef ref : this.references) {
				log.info("I (" + getSelf().path().name() + ") am sending a message to " + ref.path().name());
				ref.tell(new MyMessage(m.string, m.sequenceNumber, m.length+1), getSelf());
			}
		} else if (m.reference != null) {
			this.references.add(m.reference);
			log.info("I (" + getSelf().path().name() + ") received a reference: " + m.reference.path().name());
		}
	}

}
