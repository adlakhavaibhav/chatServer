package com.demo.server.operation;

/**
 * created by vaibhava on 15/04/18
 **/
public class UnSupportedCommand implements Command {

    public String execute() {
        return "Server does not support the request";
    }
}
