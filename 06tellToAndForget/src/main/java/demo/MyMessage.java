package demo;

import akka.actor.ActorRef;

public class MyMessage {
    public final String string;
    public final ActorRef transmitter;
    public final ActorRef destination;

    public MyMessage(String string, ActorRef destination) {
        this.string = string;
        this.transmitter = null;
        this.destination = destination;
    }

    public MyMessage(ActorRef transmitter, ActorRef destination) {
        this.string = null;
        this.transmitter = transmitter;
        this.destination = destination;
    }

    public MyMessage(String string) {
        this.string = string;
        this.transmitter = null;
        this.destination = null;
    }
    
}
