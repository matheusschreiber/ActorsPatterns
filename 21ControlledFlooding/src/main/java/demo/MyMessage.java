package demo;

import akka.actor.ActorRef;

public class MyMessage {
    public final String string;
    public final ActorRef reference;
    public final int sequenceNumber;

    public MyMessage(String string, int sequenceNumber) {
        this.string = string;
        this.reference = null;
        this.sequenceNumber = sequenceNumber;
    }

    public MyMessage(ActorRef reference) {
        this.string = null;
        this.reference = reference;
        this.sequenceNumber = -1;
    }
}