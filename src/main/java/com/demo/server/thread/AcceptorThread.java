package com.demo.server.thread;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * created by vaibhava on 15/04/18
 **/

public class AcceptorThread implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private Logger logger = Logger.getLogger(AcceptorThread.class.getName());


    public AcceptorThread(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    public void run() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                System.out.println("Connection Accepted by ServerCore" + socketChannel.getRemoteAddress());
                new HandlerThread(selector, socketChannel);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}