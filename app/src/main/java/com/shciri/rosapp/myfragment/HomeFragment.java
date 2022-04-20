package com.shciri.rosapp.myfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.shciri.rosapp.ControllerView;
import com.shciri.rosapp.MyPGM;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RosInit;
import com.shciri.rosapp.data.RosData;
import com.shciri.rosapp.peripheral.Buzzer;
import com.shciri.rosapp.peripheral.Led;

import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Point;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.TransformsMsg;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class HomeFragment extends Fragment implements View.OnClickListener{

    View root;

    private Buzzer buzzer = new Buzzer();
    private Led led;

    public Button connectBtn;

    public Switch ledSwitch;

    public ControllerView controllerView;

    private PhotoView photoView;

    private Bitmap bitmap;

    private RosInit rosInit;

    private LocalReceiver localReceiver;

    public static LocalBroadcastManager localBroadcastManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        controllerView = root.findViewById(R.id.controller_view);
        photoView = root.findViewById(R.id.map_view);
        photoView.setOnClickListener(new PhotoView.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.out.println("Click");
            }
        });

        photoView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                System.out.println("x:= " + x + "  y:= " + y);
                x -= RosData.MapData.poseX;
                y -= RosData.MapData.poseY;
                //System.out.println("x:= " + x + "  y:= " + y);
                x *= 0.05F;
                y *= 0.05F;
                System.out.println("dstX =" + x + "  dstY =" + y);
                RosData.moveGoal.header.frame_id = "map";
                RosData.moveGoal.pose.position.x = x;
                RosData.moveGoal.pose.position.y = y;
                RosData.moveGoal.pose.orientation.z = 0.98f;
                RosData.moveGoal.pose.orientation.w = -0.019f;
                RosInit.goalTopic.publish(RosData.moveGoal);
            }
        });
        connectBtn = root.findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(this);
        ledSwitch = root.findViewById(R.id.led_switch);

        led = new Led();
        if(led.LedOpen() == -1)
            Toast.makeText(getContext(), "设备打开失败！ ", Toast.LENGTH_SHORT).show();
        else
            led.LedIoctl(1,1);

        ledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    led.LedIoctl(0,0);
                }else {
                    led.LedIoctl(1,1);
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RosData.MAP);
        intentFilter.addAction(RosData.TF);
        intentFilter.addAction(RosData.TOAST);
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        return root;
    }


    private static float lastX = 1f;
    private static float lastY = 1f;
    private int stopCMDNum = 3;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connect_btn) {
            new Thread(()->{
                rosInit = new RosInit(getContext());
                rosInit.getTF();
                while (true) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    float x = controllerView.getFingerX();
                    float y = controllerView.getFingerY();

                    if(RosInit.isConnect){
                        if (lastX == 0f && lastY == 0f && x == 0f && y == 0f) {
                            if (stopCMDNum != 1) {
                                RosData.cmd_vel.linear.x = y / 2.0f;
                                RosData.cmd_vel.angular.z = x / 1.5f;
                                lastX = x;
                                lastY = y;
                                RosInit.cmd_velTopic.publish(RosData.cmd_vel);
                                stopCMDNum--;
                            }
                            //System.out.println("x:= " + x + "   y:= " + y);
                        } else {
                            stopCMDNum = 3;
                            RosData.cmd_vel.linear.x = y / 2.0f;
                            RosData.cmd_vel.angular.z = x / 1.5f;
                            lastX = x;
                            lastY = y;
                            RosInit.cmd_velTopic.publish(RosData.cmd_vel);
                        }
                    }
                }
            }).start();


        }
    }

    private void plotRoute(int x, int y){
        int tX = RosData.MapData.poseX + x;
        int tY = RosData.MapData.poseY + y;

//        System.out.println("tX =" + tX + "  tY =" + tY);
//        if(tX < 0) tX = -tX;
//        if(tY < 0) tY = -tY;
        bitmap.setPixel(tX-1, tY-1, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX, tY-1, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX-1, tY+1, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX-1, tY, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX, tY, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX+1, tY, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX+1, tY-1, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX, tY+1, Color.argb(100, 0, 150, 0));
        bitmap.setPixel(tX+1, tY+1, Color.argb(100, 0, 150, 0));
        photoView.setImageBitmap(bitmap);
    }

    private void plotMap(){
        System.out.println("look x=" + RosData.MapData.poseX + "  y=" + RosData.MapData.poseY);
        MyPGM pgm = new MyPGM();
        int[] pix;
        pix = pgm.readData(RosData.map.info.width, RosData.map.info.height, 5, RosData.map.data, RosData.MapData.poseX, RosData.MapData.poseY);   //P5-Gray image
        bitmap = Bitmap.createBitmap(RosData.map.info.width, RosData.map.info.height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pix,0,RosData.map.info.width,0,0,RosData.map.info.width,RosData.map.info.height);
        photoView.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    public class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case RosData.TF:
                    plotRoute(RosData.BaseLink.x, RosData.BaseLink.y);
                    break;

                case RosData.MAP:
                    plotMap();
                    break;

                case RosData.TOAST:
                    String hint = intent.getStringExtra("Hint");
                    Toast.makeText(getContext(), hint, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
