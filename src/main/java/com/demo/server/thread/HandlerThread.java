package com.demo.server.thread;

import com.demo.chat.ChatApplicationData;
import com.demo.chat.Participant;
import com.demo.server.operation.Command;
import com.demo.server.operation.JoinRoomCommand;
import com.demo.server.operation.SendMsgCommand;
import com.demo.server.operation.UnSupportedCommand;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * created by vaibhava on 15/04/18
 **/
public class HandlerThread implements Runnable {

    private static final String JOIN = "JOIN";
    private static final String SEND = "SEND";
    private static final int READING = 0, SENDING = 1;
    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    private Logger logger = Logger.getLogger(HandlerThread.class.getName());
    private ByteBuffer input = ByteBuffer.allocate(1024);
    private int state = READING;

    private Command currentCommand;


    HandlerThread(Selector selector, SocketChannel c) throws IOException {
        socketChannel = c;
        c.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }


    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            selectionKey.cancel();
            try {
                logger.log(Level.INFO, "Connection removed by ServerCore" + socketChannel.getRemoteAddress());
                ChatApplicationData.getInstance().participantDropped(new Participant(this, socketChannel.getRemoteAddress().toString()));
                socketChannel.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error in handler thread", e);
            }
        }
    }

    private String readInput() throws IOException {
        StringBuilder sb = new StringBuilder();
        int readCount = socketChannel.read(input);
        if (readCount > 0) {
            input.flip();
            byte[] subStringBytes = new byte[readCount];
            byte[] array = input.array();
            System.arraycopy(array, 0, subStringBytes, 0, readCount);
            sb.append(new String(subStringBytes));
            input.clear();
        }

        return sb.toString().trim();
    }

    private void read() throws IOException {

        String input = readInput();
        try {
            String[] tokenArr = parseInput(input);
            if (null != tokenArr) {
                String token = tokenArr[0];
                Participant participant = new Participant(this, socketChannel.getRemoteAddress().toString());
                if (JOIN.equalsIgnoreCase(token)) {
                    currentCommand = new JoinRoomCommand(tokenArr[1], participant);
                } else if (SEND.equalsIgnoreCase(token)) {
                    currentCommand = new SendMsgCommand(tokenArr[1], tokenArr[2], participant);
                } else {
                    currentCommand = new UnSupportedCommand();
                }
            } else {
                currentCommand = new UnSupportedCommand();
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Error while reading request", e);
            currentCommand = new UnSupportedCommand();
        }

        state = SENDING;
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    public void send(String message) throws IOException {
        ByteBuffer output = ByteBuffer.wrap((message + "\n").getBytes());
        socketChannel.write(output);
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = READING;
    }

    private void send() throws IOException {
        String response = currentCommand.execute();

        ByteBuffer output = ByteBuffer.wrap((response + "\n").getBytes());
        socketChannel.write(output);
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = READING;
    }

    private String[] parseInput(String input) {
        try {
            String[] result = new String[3];

            int idx = input.indexOf(" ");
            String firstToken = input.substring(0, idx);
            firstToken = firstToken.trim();
            result[0] = firstToken;
            if ("JOIN".equalsIgnoreCase(firstToken)) {
                String secondToken = input.substring(input.indexOf("<") +1, input.indexOf(">"));
                result[1] = secondToken;
                return result;
            } else if ("SEND".equalsIgnoreCase(firstToken)) {
                String secondToken = input.substring(input.indexOf("<") +1, input.indexOf(">"));
                String thirdToken = input.substring(input.lastIndexOf("<") + 1, input.lastIndexOf(">"));
                result[1] = secondToken;
                result[2] = thirdToken;
                return result;
            } else {
                System.err.println("Invalid input");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


}