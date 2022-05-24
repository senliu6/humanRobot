package com.shciri.rosapp.myfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.data.RosData;

import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Point;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.QuaternionMsg;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.TransformsMsg;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class NetworkFragment extends Fragment implements View.OnClickListener{


    View root;

    public Button connectBtn;

    final public String MOVE = ".com.shciri.rosapp.MOVE";

    public static src.com.jilk.ros.Topic<CmdVel> cmd_velTopic;
    public static src.com.jilk.ros.Topic<MapMsg> mapTopic;
    public static src.com.jilk.ros.Topic<TFTopic> tfTopic;
    public static src.com.jilk.ros.Topic<MoveGoal> goalTopic;
    public static CmdVel cmd_vel;
    public static MapMsg map;
    public static TFTopic tf;
    public static MoveGoal moveGoal;

    static float lastX = 1f;
    static float lastY = 1f;
    private int stopCMDNum = 3;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_network, container, false);

        connectBtn = root.findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(this);

        return root;
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(".com.shciri.rosapp.POINT")){
                float dstX = intent.getFloatExtra("dstX",0f);
                float dstY = intent.getFloatExtra("dstY",0f);
                dstX -= RosData.MapData.poseX;
                dstY -= RosData.MapData.poseY;
                dstX *= 0.05F;
                dstY *= 0.05F;
                System.out.println("dstX =" + dstX + "  dstY =" + dstY);
                moveGoal.header.seq = 3;
                moveGoal.header.frame_id = "map";
                moveGoal.pose.position.x = dstX;
                moveGoal.pose.position.y = dstY;
//                goalTopic.publish(moveGoal);
            }else{
                float x = intent.getFloatExtra("fingerX", 0.0f);
                float y = intent.getFloatExtra("fingerY", 0.0f);

                if(lastX==0f && lastY==0f && x==0f && y==0f){
                    if(stopCMDNum == 1) {
                        return;
                    }
                    stopCMDNum --;
                }else{
                    stopCMDNum = 3;
                }

                cmd_vel.linear.x = y/2.0f;
                cmd_vel.angular.z = x/1.5f;
                lastX = x;
                lastY = y;

                cmd_velTopic.publish(cmd_vel);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connect_btn) {
            new Thread(() -> {
                rosInit();
                tfTopic.subscribe();
                while(true) {
                    try {
//                        Thread.sleep(300);
                        tf = tfTopic.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (TransformsMsg trans :
                            tf.transforms) {
                        if(trans.child_frame_id.equals("base_link")) {
                            RosData.BaseLink.transform = trans.transform;
                            RosData.BaseLink.fastConversion();
//                            System.out.println("child_frame_id =" + trans.child_frame_id);
//                            System.out.println("child_frame_id =" + trans.child_frame_id);
//                            System.out.println("tf.x =" + trans.transform.translation.x);
//                            System.out.println("tf.y =" + trans.transform.translation.y);
//                            System.out.println("tf.z =" + trans.transform.translation.z);
                        }
                    }
                }
            }).start();

            new Thread(() -> {

                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(".com.shciri.rosapp.TF");
                    intent.putExtra("update", 1);
                    LocalBroadcastManager.getInstance(getActivity())
                            .sendBroadcast(intent);
                }
            }).start();
        }
    }

    private void rosInit() {
        ROSBridgeClient client = new ROSBridgeClient("ws://192.168.1.105:9090");
        client.connect();
        testTopic(client);
    }

    public void testTopic(ROSBridgeClient client) {
        cmd_velTopic = new Topic<CmdVel>("/cmd_vel", CmdVel.class, client);
        mapTopic = new Topic<MapMsg>("/map", MapMsg.class, client);
        tfTopic  = new Topic<TFTopic>("/tf", TFTopic.class, client);
        goalTopic  = new Topic<MoveGoal>("/move_base_simple/goal", MoveGoal.class, client);;
        cmd_velTopic.subscribe();
        mapTopic.subscribe();
//        goalTopic.subscribe();
        try {
            cmd_vel = cmd_velTopic.take(); //上面调用的没有handler的subcribe()，因此需要自行调用Topic实例对象的take()来取出预订的来自RosBridge WebSocket Server的消息
            map = mapTopic.take();
            moveGoal = new MoveGoal();
            moveGoal.pose = new Pose();
            moveGoal.pose.position = new Point();
            moveGoal.pose.orientation = new QuaternionMsg();
            moveGoal.header = new Header();
        }
        catch (InterruptedException ex) {}
        cmd_vel.print();
        cmd_velTopic.advertise();
//        goalTopic.advertise();


        Intent intent_map = new Intent(".com.shciri.rosapp.BITMAP");
        intent_map.putExtra("width", map.info.width);
        intent_map.putExtra("height", map.info.height);
        intent_map.putExtra("bitmap", map.data);
        intent_map.putExtra("poseX", (int)map.info.origin.position.x);
        intent_map.putExtra("poseY", (int)map.info.origin.position.y);
        LocalBroadcastManager.getInstance(getActivity())
                .sendBroadcast(intent_map);

        System.out.println("h=" + map.info.height);
        System.out.println("w=" + map.info.width);
        System.out.println("resolution=" + map.info.resolution);
        System.out.println("position.x =" + map.info.origin.position.x);
        System.out.println("position.y =" + map.info.origin.position.y);
        System.out.println("position.z =" + map.info.origin.position.z);
//        System.out.println("tf.x =" + tf.transforms.transform.translation.x);
//        System.out.println("tf.y =" + tf.transforms.transform.translation.y);
//        System.out.println("tf.z =" + tf.transforms.transform.translation.z);


//        Bitmap bitmap = pgm.getPicFromBytes(map.data);
//        imageView.setImageBitmap(bitmap);

//        System.out.println("Not a pgm image!"+" [0]="+map.data[0]+", [1]="+map.data[1]);
//        pgm.bytesToImageFile(map.data);

//        iw = pgm.getWidth();
//        ih = pgm.getHeight();
//        pix = pgm.readData(iw, ih, 5);   //P5-Gray image
//        Bitmap bitmap = Bitmap.createBitmap(iw,ih, Bitmap.Config.ARGB_4444);
//        bitmap.setPixels(pix,0,iw,0,0,iw,ih);

//        imageView.setImageBitmap(bitmap);


//        cmdvelTopic.unsubscribe();
//        cmdvelTopic.unadvertise();
    }
}
