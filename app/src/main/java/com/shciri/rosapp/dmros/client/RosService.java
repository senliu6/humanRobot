package com.shciri.rosapp.dmros.client;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.Service;
import src.com.jilk.ros.message.RobotControlRequest;
import src.com.jilk.ros.message.RobotControlResponse;
import src.com.jilk.ros.rosapi.message.Empty;
import src.com.jilk.ros.rosapi.message.GetTime;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosService {

    public static Service<RobotControlRequest, RobotControlResponse> coverageMapService;
    public final int ENABLE_ROBOT = 1;
    public final int EXECUTE_ROBOT = 2;
    public final int PAUSE_ROBOT = 3;
    public final int RESET_ROBOT = 4;

    public final String[] ServiceName = {"/robot_control"};

    public void initCoverageMapService(ROSBridgeClient client) {
        try {
            coverageMapService = new Service<RobotControlRequest, RobotControlResponse>("/robot_control", RobotControlRequest.class, RobotControlResponse.class, client);
            coverageMapService.verify();
//            coverageMapService.callWithHandler(new Empty(), messageHandler);
        }catch (InterruptedException ex) {
            System.out.println("Process was interrupted.");
        }

    }
}
