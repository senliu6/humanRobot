package com.shciri.rosapp.ui.manualcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.github.chrisbanes.photoview.PhotoView;
import com.shciri.rosapp.MyPGM;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RosInit;
import com.shciri.rosapp.data.RosData;
import com.shciri.rosapp.ui.myview.DmSwitchView;
import com.shciri.rosapp.ui.myview.MapView;
import com.shciri.rosapp.ui.myview.MyControllerView;
import com.shciri.rosapp.ui.myview.RosMapView;
import com.shciri.rosapp.peripheral.Buzzer;
import com.shciri.rosapp.peripheral.Led;

public class ManuaControlFragment extends Fragment implements View.OnClickListener {

    View root;

    public Button connectBtn;

    public DmSwitchView ledSwitch;

    public MyControllerView controllerView;

    private PhotoView photoView;

    private Bitmap bitmap;

    private RosInit rosInit;

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
                    RosData.cmd_vel.linear.x = dy / 1.5f;
                    RosData.cmd_vel.angular.z = -dx / 2f;
                    RosInit.cmd_velTopic.publish(RosData.cmd_vel);
                }
            }
        };

        controllerView.setMoveListener(moveListener);
        mMapView = root.findViewById(R.id.ros_map);
//        Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.map_example);
//        mMapView.setBitmap(map, 12);
//        mMapView.setRobotPosition(200, 300, (float) 0.3, true);

        connectBtn = root.findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(this);

        ledSwitch = root.findViewById(R.id.led_switch);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RosData.MAP);
        intentFilter.addAction(RosData.TF);
        intentFilter.addAction(RosData.TOAST);
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        rosInit = new RosInit(getContext());

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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.connect_btn) {
            new Thread(() -> {
                rosInit.rosConnect();
                rosInit.getTF();
                rosInit.getMap();
            }).start();
            return;
        }
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
        mMapView.setBitmap(rosBitmap,12);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBackPressedCallback.remove();
        localBroadcastManager.unregisterReceiver(localReceiver);
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
}
