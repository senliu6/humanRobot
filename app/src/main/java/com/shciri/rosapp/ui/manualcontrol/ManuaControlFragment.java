package com.shciri.rosapp.ui.manualcontrol;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.base.BaseFragment;
import com.shciri.rosapp.databinding.FragmentManuaControlBinding;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.PointCloud2d;
import com.shciri.rosapp.dmros.tool.PointCloudEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.dmros.tool.RobotPoseEvent;
import com.shciri.rosapp.ui.view.MapView;
import com.shciri.rosapp.ui.view.MyControllerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutorService;

public class ManuaControlFragment extends BaseFragment {


    private static boolean send_zero;

    private FragmentManuaControlBinding binding;

    private ExecutorService executorService = RCApplication.getExecutorService();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentManuaControlBinding.inflate(inflater, container, false);


        MyControllerView.MoveListener moveListener = (dx, dy) -> {
            //Log.v("ManuaControlFragment", "move " + dx + ", " + dy);
            RosData.cmd_vel.linear.x = dy / 2.5f;
            RosData.cmd_vel.angular.z = -dx / 2f;
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
        binding.rosMap.isSetGoal = true;

        initView();
        return binding.getRoot();

    }

    private void initView() {
        Log.d("CeshiTAG", "look x=" + RosData.MapData.poseX + "  y=" + RosData.MapData.poseY);
        binding.btnQuit.setOnClickListener(this::back);
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        setMap();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        binding.rosMap.isSetGoal = false;
        mBackPressedCallback.remove();
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if ("/slam_map".equals(event.getMessage())) {
            setMap();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PointCloudEvent event) {
        PointCloud2d cloud2d = event.getPointCloud();
        if (cloud2d.points.length > 0) {
            MapView.pointsArray = cloud2d.points;
            binding.rosMap.postInvalidate();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RobotPoseEvent event) {
//        Pose location = event.getPose();
//        MapView.pose = location;
//        binding.rosMap.postInvalidate();
    }

    private void setMap() {
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
