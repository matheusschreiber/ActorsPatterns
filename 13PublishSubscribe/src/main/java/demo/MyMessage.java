package demo;

import akka.actor.ActorRef;

public class MyMessage {
    public final String string;
    public final ActorRef broadcaster;

    public MyMessage(String string) {
        this.string = string;
        this.broadcaster = null;
    }

    public MyMessage(ActorRef broadcaster) {
        this.string = null;
        this.broadcaster = broadcaster;
    }
}