package demo;

import java.util.ArrayList;
import java.util.List;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.actor.ActorRef;

public class MyActor extends AbstractActor {

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private List<ActorRef> references = new ArrayList<>();
	private int electionId = 0;
	private boolean isParticipating = false;
	private int leaderId = -1;
	private boolean isElected = false;
	private boolean electionIsDone = false;

	public MyActor(int electionId) {
		this.electionId = electionId;
	}

	// Static function creating actor
	public static Props createActor(int electionId) {
		return Props.create(MyActor.class, () -> {
			return new MyActor(electionId);
		});
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(MyMessage.class, this::receiveFunction)
				.build();
	}

	public void handleStart(MyMessage m) {
		if (isParticipating) return;

		isParticipating = true;
		log.info("I (" + getSelf().path().name() + ") am starting an election.");
		for (ActorRef ref : references) {
			ref.tell(new MyMessage("election", electionId), getSelf());
		}
	}

	public void handleElection(MyMessage m) {
		if (m.electionId > electionId) {
			isParticipating = true;
			log.info("I (" + getSelf().path().name() + ") received a higher election ID: " + m.electionId
					+ ". Forwarding election message.");
			for (ActorRef ref : references) {
				ref.tell(new MyMessage("election", m.electionId), getSelf());
			}
		} else if (m.electionId < electionId && !isParticipating) {
			isParticipating = true;
			log.info("I (" + getSelf().path().name() + ") received a lower election ID: " + m.electionId
					+ ". Starting my own election.");
			for (ActorRef ref : references) {
				ref.tell(new MyMessage("election", electionId), getSelf());
			}
		} else if (m.electionId == electionId) {
			for (ActorRef ref : references) {
				ref.tell(new MyMessage("elected", m.electionId), getSelf());
			}
			isElected = true;
		}
	}

	public void handleElected(MyMessage m) {
		if (electionIsDone) return;

		leaderId = m.electionId;
		electionIsDone = true;
		if (m.electionId != electionId) {
			isElected = false;
			log.info("I (" + getSelf().path().name() + ") acknowledge the elected leader with ID: " + m.electionId);
			for (ActorRef ref : references) {
				ref.tell(new MyMessage("elected", m.electionId), getSelf());
			}
		} else {
			log.info("I (" + getSelf().path().name() + ") am the elected leader!");
		}
	}

	public void receiveFunction(MyMessage m) {
		if (m.string != null) {
			if (m.string.equals("start")) {
				handleStart(m);
			} else if (m.string.equals("election")) {
				handleElection(m);
			} else if (m.string.equals("elected")) {
				handleElected(m);
			}

		} else if (m.reference != null) {
			this.references.add(m.reference);
			log.info("I (" + getSelf().path().name() + ") received a reference: " + m.reference.path().name());
		}
	}

}
