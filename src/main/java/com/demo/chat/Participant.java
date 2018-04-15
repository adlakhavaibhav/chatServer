package com.demo.chat;

import com.demo.server.thread.HandlerThread;
import java.io.IOException;
import java.util.Objects;

/**
 * created by vaibhava on 15/04/18
 **/
public class Participant {

    private HandlerThread handlerThread;
    private String identifier;


    public Participant(HandlerThread handlerThread, String identifier) {
        this.handlerThread = handlerThread;
        this.identifier = identifier;
    }

    void sendMessage(String message) throws IOException {
        this.handlerThread.send(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Participant that = (Participant) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public String getIdentifier() {
        return identifier;
    }
}
