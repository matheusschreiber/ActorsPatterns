package demo;

import akka.actor.ActorRef;

public class MyMessage {
    public final String string;
    public final ActorRef reference;

    public MyMessage(String string) {
        this.string = string;
        this.reference = null;
    }

    public MyMessage(ActorRef reference) {
        this.string = null;
        this.reference = reference;
    }
}