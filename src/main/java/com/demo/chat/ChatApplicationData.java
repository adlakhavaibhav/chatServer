package com.demo.chat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * created by vaibhava on 15/04/18
 **/
public class ChatApplicationData {

    private static ChatApplicationData instance = new ChatApplicationData();
    private Logger logger = Logger.getLogger(ChatApplicationData.class.getName());
    private Map<String, Room> nameToRooms = new HashMap<>();

    private ChatApplicationData() {
    }

    public static ChatApplicationData getInstance() {
        return instance;
    }

    public String joinRoom(String roomName, Participant participant) {
        String key = keyForRoom(roomName);
        Room room = nameToRooms.get(key);
        if (null == room) {
            room = new Room(roomName, participant);
            nameToRooms.put(key, room);
        } else {
            room.addParticipant(participant);
        }
        return "OK";
    }


    public String broadCastMessage(String roomName, String message, Participant sender) {
        String key = keyForRoom(roomName);
        Room room = nameToRooms.get(key);
        if (null != room) {
            try {
                return room.broadCastMsg(message, sender);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error broadcasting message", e);
                return "FAIL";
            }
        } else {
            return "Room with specified name does not exist";
        }

    }

    public void participantDropped(Participant participant) {
        for (Room room : nameToRooms.values()) {
            room.removeParticipant(participant);
        }
    }

    private String keyForRoom(String name) {
        String key = name.replaceAll("\\s","-");
        return key.trim().toLowerCase();
    }
}
