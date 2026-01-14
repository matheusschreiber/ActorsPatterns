package demo;

import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Topic extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorRef[] actors;

	public Topic() {}

    // Static function creating actor
	public static Props createActor() {
		return Props.create(Topic.class, () -> {
			return new Topic();
		});
	}

    @Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(MyMessage.class, this::receiveFunction)
			.build();
	  }

	public void receiveFunction(MyMessage m){
		if ("subscribe".equals(m.string)){
			if (actors == null) {
				actors = new ActorRef[1];
				actors[0] = getSender();
			} else {
				ActorRef[] newActors = new ActorRef[actors.length + 1];
				System.arraycopy(actors, 0, newActors, 0, actors.length);
				newActors[actors.length] = getSender();
				actors = newActors;
			}
			log.info("Actor " + getSender().path().name() + " joined topic " + getSelf().path().name());
		} else if ("unsubscribe".equals(m.string)) {
			if (actors == null) {
				return;
			} 

			ActorRef[] newActors = new ActorRef[actors.length - 1];
			for (int i = 0, j = 0; i < actors.length; i++) {
				if (actors[i] != getSender()) {
					newActors[j++] = actors[i];
				}
			}
			actors = newActors;
			log.info("Actor " + getSender().path().name() + " left topic " + getSelf().path().name());
		} else  {
			if (actors == null || actors.length == 0) {
				return;
			}

			for (ActorRef a : actors) {
				MyMessage msg = new MyMessage(m.string);
				a.tell(msg, getSelf());
			}
		}
	}
}
