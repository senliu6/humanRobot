package com.shciri.rosapp.ui.manualcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.github.chrisbanes.photoview.PhotoView;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.tool.MyPGM;
import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.myview.DmSwitchView;
import com.shciri.rosapp.ui.myview.MapView;
import com.shciri.rosapp.ui.myview.MyControllerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import src.com.jilk.ros.message.PoseStamped;

public class ManuaControlFragment extends Fragment {

    View root;

    public DmSwitchView ledSwitch;

    public MyControllerView controllerView;

    private PhotoView photoView;

    private Bitmap bitmap;

    private LocalReceiver localReceiver;

    public static LocalBroadcastManager localBroadcastManager;

    private MapView mMapView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_manua_control, container, false);

        controllerView = root.findViewById(R.id.controller_view);
        MyControllerView.MoveListener moveListener = new MyControllerView.MoveListener() {
            @Override
            public void move(float dx, float dy) {
                Log.v("ManuaControlFragment", "move " + dx + ", " + dy);
//                PointF robot = mMapView.getRobotPosition();
//                float direction = (float) Math.random();
                //mMapView.setRobotPosition(robot.x + dx, robot.y + dy, direction*100, true);
                if (RosInit.isConnect) {
//                    RosData.cmd_vel.linear.x = dy / 1.5f;
//                    RosData.cmd_vel.angular.z = -dx / 2f;
//                    RosTopic.cmd_velTopic.publish(RosData.cmd_vel);
                    RosData.jointSpeeds.joint_speeds[0].value = dy;
                    RosTopic.joint_velocity.publish(RosData.jointSpeeds);
                }
            }
        };

        controllerView.setMoveListener(moveListener);
        mMapView = root.findViewById(R.id.ros_map);
        if(RosData.rosBitmap != null){
            mMapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        }else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
            mMapView.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        mMapView.isSetGoal = true;
        ledSwitch = root.findViewById(R.id.led_switch);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RosData.MAP);
        intentFilter.addAction(RosData.TF);
        intentFilter.addAction(RosData.TOAST);
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        return root;
    }

    OnBackPressedCallback mBackPressedCallback;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).navigateUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), mBackPressedCallback);
    }

    private void plotRoute(int x, int y) {
        int tX = RosData.MapData.poseX + x;
        int tY = RosData.MapData.poseY + y;

//        bitmap.setPixel(tX - 1, tY - 1, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX, tY - 1, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX - 1, tY + 1, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX - 1, tY, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX, tY, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX + 1, tY, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX + 1, tY - 1, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX, tY + 1, Color.argb(100, 0, 150, 0));
//        bitmap.setPixel(tX + 1, tY + 1, Color.argb(100, 0, 150, 0));

        //System.out.println("RosData.BaseLink.yaw = " + RosData.BaseLink.yaw*100);
        mMapView.setRobotPosition(tX, RosData.map.info.height-tY, (RosData.BaseLink.yaw * 100)-120, true);
    }

    private void plotMap() {
        System.out.println("look x=" + RosData.MapData.poseX + "  y=" + RosData.MapData.poseY);
        MyPGM pgm = new MyPGM();
        int[] pix;
        pix = pgm.readData(RosData.map.info.width, RosData.map.info.height, 5, RosData.map.data, RosData.MapData.poseX, RosData.MapData.poseY);   //P5-Gray image
        bitmap = Bitmap.createBitmap(RosData.map.info.width, RosData.map.info.height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pix, 0, RosData.map.info.width, 0, 0, RosData.map.info.width, RosData.map.info.height);
        Matrix invert = new Matrix();
        invert.setScale(1, -1); //镜像翻转以与真实地图对应
        Bitmap rosBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), invert ,true);
        mMapView.setBitmap(rosBitmap,MapView.updateMapID.RUNNING);
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        PublishEvent.readyPublish = true;
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        PublishEvent.readyPublish = false;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mMapView.isSetGoal = false;
        mBackPressedCallback.remove();
        localBroadcastManager.unregisterReceiver(localReceiver);
        super.onDestroy();
    }

    public class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if("/map".equals(event.getMessage())) {
            if(RosData.rosBitmap != null){
                if(MapView.scanning)
                    mMapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.SCANNING);
                else
                    mMapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
            }else {
                Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
                mMapView.setBitmap(map, MapView.updateMapID.RUNNING);
            }
        }
    };
}
