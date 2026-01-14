package demo;

import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Broadcaster extends AbstractActor{

	// Logger attached to actor
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	private ActorRef[] actors;

	public Broadcaster() {}

    // Static function creating actor
	public static Props createActor() {
		return Props.create(Broadcaster.class, () -> {
			return new Broadcaster();
		});
	}

    @Override
	public Receive createReceive() {
		return receiveBuilder()
			.match(MyMessage.class, this::receiveFunction)
			.build();
	  }

	public void receiveFunction(MyMessage m){
		if ("join".equals(m.string)){
			log.info("Broadcaster received message: " + m.string + " from actor: " + getSender().path().name());

			if (actors == null) {
				actors = new ActorRef[1];
				actors[0] = getSender();
			} else {
				ActorRef[] newActors = new ActorRef[actors.length + 1];
				System.arraycopy(actors, 0, newActors, 0, actors.length);
				newActors[actors.length] = getSender();
				actors = newActors;
			}
			log.info("Actor " + getSender().path().name() + " joined broadcaster");
		} else {
			if (actors == null || actors.length == 0) {
				log.info("No actors to broadcast to.");
				return;
			}

			for (ActorRef a : actors) {
				if (a == getSender()) continue;

				log.info("Broadcaster sending message to actor: " + a.path().name());
				MyMessage msg = new MyMessage(m.string);
				a.tell(msg, getSelf());
			}
		}
	}
}
