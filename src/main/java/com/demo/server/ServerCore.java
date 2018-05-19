package com.demo.server;


import com.demo.server.thread.AcceptorThread;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * created by vaibhava on 15/04/18
 **/

public class ServerCore implements Runnable {


    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private Logger logger = Logger.getLogger(ServerCore.class.getName());


    public ServerCore(int port) throws IOException {

        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // intereseted in accepting connections.
        selectionKey.attach(new AcceptorThread(selector, serverSocketChannel));
    }


    public void run() {
        logger.log(Level.INFO, "SimpleChatServer started on: " + serverSocketChannel.socket().getLocalPort());
        try {
            while (!Thread.interrupted()) {
                selector.selectNow();
                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) (it.next());

                    if(!key.isValid()){
                        continue;
                    }
                    if(key.isAcceptable()) {
                        dispatch(key);
                    }
                }
                selectedKeys.clear();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error on event loop", ex);
        }
    }

    private void dispatch(SelectionKey k) {
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            r.run();
        }
    }

}