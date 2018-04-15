package com.demo.server.operation;

import com.demo.chat.ChatApplicationData;
import com.demo.chat.Participant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * created by vaibhava on 15/04/18
 **/
public class JoinRoomCommand implements Command {

    private Logger logger = Logger.getLogger(JoinRoomCommand.class.getName());
    private String roomName;
    private Participant participant;

    public JoinRoomCommand(String roomName, Participant participant) {
        this.roomName = roomName;
        this.participant = participant;
    }

    public String execute() {
        if (null == roomName || roomName.length() == 0) {
            return "Room name cannot be blank";
        }
        logger.log(Level.INFO, participant.getIdentifier() + " joining " + roomName);
        return ChatApplicationData.getInstance().joinRoom(roomName, participant);
    }
}
