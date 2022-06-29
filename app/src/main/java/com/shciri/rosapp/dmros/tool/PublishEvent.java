package com.shciri.rosapp.dmros.tool;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.rosbridge.operation.Operation;

public class PublishEvent {
    public static boolean readyPublish;
    public String message;
    public int[] path;

    public PublishEvent(String message) {
        this.message = message;
    }

    public PublishEvent(String message, int x, int y) {
        this.message = message;
        this.path[0] = x;
        this.path[1] = y;
    }

    public String getMessage() {
        return message;
    }

    public int[] getPath() {
        return path;
    }
}
