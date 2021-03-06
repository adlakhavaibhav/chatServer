package com.demo.chat;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * created by vaibhava on 15/04/18
 **/
class Room {

    private final ReadWriteLock readWriteLock;
    private Logger logger = Logger.getLogger(Room.class.getName());
    private String name;
    private Set<Participant> participants = new LinkedHashSet<>();


    Room(String name, Participant participant) {
        this.name = name;
        this.participants.add(participant);
        this.readWriteLock = new ReentrantReadWriteLock(true);
    }

    void addParticipant(Participant participant) {
        readWriteLock.writeLock().lock();
        try {
            this.participants.add(participant);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    void removeParticipant(Participant participant) {
        readWriteLock.writeLock().lock();
        try {
            this.participants.remove(participant);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    String broadCastMsg(String msg, Participant sender) throws IOException {
        readWriteLock.readLock().lock();
        try {
            if (!this.participants.contains(sender)) {
                return "Please join " + this.name + " before trying to send messages";
            }

            for (Participant participant : participants) {
                logger.log(Level.INFO, "Sending message to " + participant.getIdentifier() + " as " + msg + " in room " + this.name);
                sendMsgAsync(msg, participant);
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return "OK";
    }

    private void sendMsgAsync(String msg, Participant receiver) {
        ExecutorService executor = Executors.newFixedThreadPool(20);

        Runnable worker = new Runnable() {
            @Override
            public void run() {
                try {
                    receiver.sendMessage(msg);
                } catch (IOException ioe) {
                    logger.log(Level.SEVERE, " Error Sending message to " + receiver.getIdentifier() + " as " + msg);
                }
            }
        };
        executor.execute(worker);
    }


}


