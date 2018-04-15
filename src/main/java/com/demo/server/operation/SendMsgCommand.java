package com.demo.server.operation;

import com.demo.chat.ChatApplicationData;
import com.demo.chat.Participant;
import com.demo.server.thread.HandlerThread;

/**
 * created by vaibhava on 15/04/18
 **/
public class SendMsgCommand implements Command {

    private String roomName;
    private String message;
    private Participant sender;

    public SendMsgCommand(String roomName, String message,Participant sender) {
        this.roomName = roomName;
        this.message = message;
        this.sender = sender;
    }

    public String execute() {
        return ChatApplicationData.getInstance().broadCastMessage(roomName, message,sender);
    }

}
