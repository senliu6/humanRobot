package com.shciri.rosapp.ui.taskexe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.control.ManageTaskDB;
import com.shciri.rosapp.ui.myview.MapView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import src.com.jilk.ros.message.PoseStamped;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TaskExeMapViewFragment extends Fragment {

    private MapView mapView;
//    private RosTopic rosTopic = new RosTopic();
//    private ReceiveHandler receiveHandler = new ReceiveHandler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        PublishEvent.readyPublish = true;
        return inflater.inflate(R.layout.fragment_task_exe_map_view, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.task_detail_map_view);
        if(RosData.rosBitmap != null) {
            mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        }else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.map_example);
            mapView.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        if(!mapView.isShowCoveragePath && RosData.coveragePath != null) {
            for(PoseStamped point : RosData.coveragePath.poses) {
                plotPath((int)(point.pose.position.x/0.05f), (int)(point.pose.position.y/0.05f));
            }
        }

        TextView taskName = view.findViewById(R.id.task_exe_name);
        taskName.setText(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).taskName);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PublishEvent.readyPublish = false;
    }


    /***
    ThreadMode.MAIN：表示无论事件是在哪个线程发布出来的，该事件订阅方法onEvent都会在UI线程中执行，这个在Android中是非常有用的，因为在Android中只能在UI线程中更新UI，所有在此模式下的方法是不能执行耗时操作的。

    ThreadMode.POSTING：表示事件在哪个线程中发布出来的，事件订阅函数onEvent就会在这个线程中运行，也就是说发布事件和接收事件在同一个线程。使用这个方法时，在onEvent方法中不能执行耗时操作，如果执行耗时操作容易导致事件分发延迟。

    ThreadMode.BACKGROUND：表示如果事件在UI线程中发布出来的，那么订阅函数onEvent就会在子线程中运行，如果事件本来就是在子线程中发布出来的，那么订阅函数直接在该子线程中执行。

    ThreadMode.AYSNC：使用这个模式的订阅函数，那么无论事件在哪个线程发布，都会创建新的子线程来执行订阅函数。
    ***/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if("/tf".equals(event.getMessage())) {
            plotRoute(RosData.BaseLink.x,RosData.BaseLink.y);

        }
        if("/coverage_path".equals(event.getMessage())) {
            for(PoseStamped point : RosData.coveragePath.poses) {
                plotPath((int)(point.pose.position.x/0.05f), (int)(point.pose.position.y/0.05f));
            }
        }
    };

    private void plotPath(int x, int y) {
        int tX = RosData.MapData.poseX + x;
        int tY = RosData.MapData.poseY + y;

        mapView.setCoveragePath(tX, RosData.map.info.height - tY, true);
    }

    private void plotRoute(int x, int y) {
        int tX = RosData.MapData.poseX + x;
        int tY = RosData.MapData.poseY + y;

        mapView.setRobotPosition(tX, RosData.map.info.height-tY, (RosData.BaseLink.yaw * 100)-120, true);
    }
}