package demo;

import akka.actor.ActorRef;

public class MyMessage {
    public final String string;
    public final ActorRef reference;
    public final int electionId;

    public MyMessage(String string, int electionId) {
        this.string = string;
        this.reference = null;
        this.electionId = electionId;
    }

    public MyMessage(ActorRef reference) {
        this.string = null;
        this.reference = reference;
        this.electionId = -1;
    }
}