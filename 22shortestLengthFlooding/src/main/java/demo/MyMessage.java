package demo;

import akka.actor.ActorRef;

public class MyMessage {
    public final String string;
    public final ActorRef reference;
    public final int sequenceNumber;
    public final int length;

    public MyMessage(String string, int sequenceNumber, int length) {
        this.string = string;
        this.reference = null;
        this.sequenceNumber = sequenceNumber;
        this.length = length;
    }

    public MyMessage(ActorRef reference) {
        this.string = null;
        this.reference = reference;
        this.sequenceNumber = -1;
        this.length = -1;
    }
}