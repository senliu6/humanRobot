package com.shciri.rosapp.ui.taskexe;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.databinding.FragmentTaskExeWorkViewBinding;
import com.shciri.rosapp.dmros.client.RosService;
import com.shciri.rosapp.server.ConnectServer;
import com.shciri.rosapp.server.ServerInfoTab;
import com.shciri.rosapp.ui.control.ManageTaskDB;
import com.shciri.rosapp.utils.protocol.RequestIPC;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import src.com.jilk.ros.message.RobotControlRequest;
import src.com.jilk.ros.message.custom.NavPace;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TaskExeWorkViewFragment extends Fragment {

    private boolean once = true;
    private static long startMills;
    private static long exeTime;
    private ConnectServer connectServer;

    private FragmentTaskExeWorkViewBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskExeWorkViewBinding.inflate(inflater, container, false);

        if (RosService.coverageMapService != null)
            RosService.coverageMapService.call(new RobotControlRequest(1));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        connectServer = new ConnectServer();

        binding.taskExeName.setText(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).taskName);

        binding.taskDetailExeView.setOnClickListener(v -> {
            if (binding.taskDetailExeView.isActivated()) {
                if (ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode.equals("同时开启")) {
                    byte[] data = RequestIPC.fanControlRequest((byte) 0);
                    RCApplication.uartVCP.sendData(data);
                    data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 0);
                    RCApplication.uartVCP.sendData(data);
                } else {
                    if (RosService.coverageMapService != null)
                        RosService.coverageMapService.call(new RobotControlRequest(3));
                }
                binding.taskDetailExeView.setActivated(false);
                exeTime += System.currentTimeMillis() - startMills;
            } else {
                if (ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode.equals("同时开启")) {
                    Log.d("adwApiManager", " start exe task ");
                    byte[] data = RequestIPC.fanControlRequest((byte) 1);
                    RCApplication.uartVCP.sendData(data);
                    data = RequestIPC.disinfectionLedControlRequest((byte) 1, (byte) 1, (byte) 1);
                    RCApplication.uartVCP.sendData(data);
                } else {
                    if (RosService.coverageMapService != null)
                        RosService.coverageMapService.call(new RobotControlRequest(2));
                }
                binding.taskDetailExeView.setActivated(true);
                if (once) {
                    insertTaskHistory(100);
                    ServerInfoTab.task_exe_number++;

                    connectServer.updateInfo();
                    once = false;
                }
                startMills = System.currentTimeMillis();
            }
        });

        binding.taskDetailPercent.setOnClickListener(v -> {
            if (ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode.equals("同时开启")) {
            } else {
                if (RosService.coverageMapService != null)
                    RosService.coverageMapService.call(new RobotControlRequest(4));
            }
            if (!once) {
                exeTime += System.currentTimeMillis() - startMills;
                ServerInfoTab.task_exe_duration += exeTime / 1000 / 60;
                if (exeTime < 60000) {
                    ServerInfoTab.task_exe_duration += 1;
                }
                connectServer.updateInfo();
            }
            Navigation.findNavController(view).navigateUp();
        });
    }

    private void insertTaskHistory(int percent) {
        ContentValues values = new ContentValues();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        values.put("date_created", simpleDateFormat.format(date));
        values.put("task_name", ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).taskName);
        values.put("task_type", "independent task");
        values.put("operator", RCApplication.Operator);
        values.put("mode", ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode);
        values.put("percentage", percent);
        RCApplication.db.insert("task_history", null, values);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private void onEvent(NavPace event) {
        Toaster.showShort("任务进度" + event.std_msgs);
        binding.taskDetailPercent.setText(String.valueOf(event.std_msgs * 100));
    }
}