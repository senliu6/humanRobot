package com.shciri.rosapp.dmros.tool;

public class ControlMapEvent {
    public static boolean readyPublish;
    public String message;
    public String mapName;
    public int mapID;

    public ControlMapEvent(String message, String name, int id) {
        this.message = message;
        this.mapName = name;
        this.mapID = id;
    }
}
