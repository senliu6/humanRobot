package com.shciri.rosapp.ui.manualcontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.databinding.FragmentManuaControlBinding;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.MyPGM;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.dmros.tool.RobotPoseEvent;
import com.shciri.rosapp.dmros.tool.StateNotifyHeadEvent;
import com.shciri.rosapp.ui.dialog.InputDialog;
import com.shciri.rosapp.ui.dialog.WaitDialog;
import com.shciri.rosapp.ui.myview.ControlFaceplateView;
import com.shciri.rosapp.ui.myview.MapView;
import com.shciri.rosapp.ui.myview.MyControllerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutorService;

import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.StateMachineRequest;
import src.com.jilk.ros.message.StateNotificationHeartbeat;

public class ManuaControlFragment extends Fragment {

    View root;

//    public DmSwitchView ledSwitch;

    public ControlFaceplateView controlFaceplateView;

    public OperatingArmView operatingArmView;

//    private PhotoView photoView;

    private Bitmap bitmap;

    private LocalReceiver localReceiver;

    public static LocalBroadcastManager localBroadcastManager;

    private static boolean send_zero;

    private FragmentManuaControlBinding binding;

    private boolean handModel = true;

    private StateMachineRequest stateMachineRequest;

    private WaitDialog waitDialog;

    private InputDialog inputDialog;
    private ExecutorService executorService = RCApplication.getExecutorService();

    private Handler handler = new Handler();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentManuaControlBinding.inflate(inflater, container, false);

        stateMachineRequest = new StateMachineRequest();

        MyControllerView.MoveListener moveListener = new MyControllerView.MoveListener() {
            @Override
            public void move(float dx, float dy) {
                //Log.v("ManuaControlFragment", "move " + dx + ", " + dy);
                RosData.cmd_vel.linear.x = dy / 2.5f;
                RosData.cmd_vel.angular.z = -dx / 2f;
            }
        };

        executorService.execute(() -> {
            while (true) {
                if (RosInit.isConnect && RosTopic.cmd_velTopic != null) {

                    if (RosData.cmd_vel.linear.x != 0 || RosData.cmd_vel.angular.z != 0) {
                        RosTopic.cmd_velTopic.publish(RosData.cmd_vel);
                        send_zero = true;
                    } else {
                        if (send_zero) RosTopic.cmd_velTopic.publish(RosData.cmd_vel);

                        send_zero = false;
                    }

                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.controllerView.setMoveListener(moveListener);

        if (RosData.rosBitmap != null) {
            binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        } else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
            binding.rosMap.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        binding.rosMap.isSetGoal = true;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RosData.MAP);
        intentFilter.addAction(RosData.TF);
        intentFilter.addAction(RosData.TOAST);
        intentFilter.addAction(RosData.WATCH);
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        waitDialog = new WaitDialog.Builder(getContext()).setLoadingText(getString(R.string.loading)).setCancelText(getResources().getString(R.string.cancel)).build();
        Bundle bundle = getArguments();
        if (bundle != null) {
            int state = bundle.getInt("state");
            if (state == 1) {
                waitDialog.show();
            }
        }
        initView();
        return binding.getRoot();


    }

    private void initView() {
        Log.d("CeshiTAG", "look x=" + RosData.MapData.poseX + "  y=" + RosData.MapData.poseY);
        setHandModel(handModel);
        binding.linearHandSwitch.setOnClickListener(v -> {
            setHandModel(handModel);
            StateMachineRequest stateMachineRequest = new StateMachineRequest();
            stateMachineRequest.hardware_control = (byte) (handModel ? 6 : 7);
            RosTopic.publishStateMachineRequest(stateMachineRequest);
        });
    }

    //动态切换Fragment
    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getParentFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.frameLayout, fragment);
//        transaction.commit();
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
        binding.rosMap.setRobotPosition(tX, RosData.map.info.height - tY, (RosData.BaseLink.yaw * 100) - 120, true);
    }

    private void plotMap() {
        Log.d("CeshiTAG", "look x=" + RosData.MapData.poseX + "  y=" + RosData.MapData.poseY);
        MyPGM pgm = new MyPGM();
        int[] pix;
        pix = pgm.readData(RosData.map.info.width, RosData.map.info.height, 5, RosData.map.data, RosData.MapData.poseX, RosData.MapData.poseY);   //P5-Gray image
        bitmap = Bitmap.createBitmap(RosData.map.info.width, RosData.map.info.height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pix, 0, RosData.map.info.width, 0, 0, RosData.map.info.width, RosData.map.info.height);
        Matrix invert = new Matrix();
        invert.setScale(1, -1); //镜像翻转以与真实地图对应
        Bitmap rosBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), invert, true);
        binding.rosMap.setBitmap(rosBitmap, MapView.updateMapID.RUNNING);
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
        binding.rosMap.isSetGoal = false;
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
                    Toaster.showShort(hint);
                    break;
                case RosData.WATCH:
                    break;
                default:
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if ("/map".equals(event.getMessage())) {
            if (RosData.rosBitmap != null) {
                if (MapView.scanning) {
                    binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.SCANNING);
                } else {
                    binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
                }
            } else {
                Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
                binding.rosMap.setBitmap(map, MapView.updateMapID.RUNNING);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StateNotifyHeadEvent event) {
        StateNotificationHeartbeat stateNotificationHeartbeat = event.getState();
        switch (stateNotificationHeartbeat.motor_state) {
            //使能
            case 0x01:
                setHandModel(false);
                break;
            //失能
            case 0x02:
                //急停
            case 0x03:
                setHandModel(true);
                break;
            default:

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RobotPoseEvent event) {
        Pose location = event.getPose();
        MapView.pose = location;
        binding.rosMap.postInvalidate();
    }

    private void setHandModel(boolean hand) {
        if (!hand) {
            handModel = true;
            binding.tvHand.setBackground(null);
            binding.tvAuto.setBackgroundResource(R.drawable.button_background_unpressed);
        } else {
            handModel = false;
            binding.tvHand.setBackgroundResource(R.drawable.button_background_unpressed);
            binding.tvAuto.setBackground(null);
        }
    }
}
