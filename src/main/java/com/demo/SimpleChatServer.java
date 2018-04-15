package com.demo;

import com.demo.server.ServerCore;
import java.io.IOException;

/**
 * created by vaibhava on 15/04/18
 **/

public class SimpleChatServer {

    public static void main(String[] args) throws IOException {

        ServerCore serverCore = new ServerCore(9095);
        new Thread(serverCore).start();
    }

}
