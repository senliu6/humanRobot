package com.shciri.rosapp.ui.taskexe;

import android.app.ADWApiManager;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.client.RosService;
import com.shciri.rosapp.server.ConnectServer;
import com.shciri.rosapp.server.ServerInfoTab;
import com.shciri.rosapp.ui.control.ManageTaskDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import src.com.jilk.ros.message.RobotControlRequest;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TaskExeWorkViewFragment extends Fragment {

    ImageView start_pause_btn;
    ImageView stop_btn;
    private int percentage;
    private boolean once = true;
    private static long startMills;
    private static long exeTime;
    private ConnectServer connectServer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if(RosService.coverageMapService != null)
            RosService.coverageMapService.call(new RobotControlRequest(1));
        return inflater.inflate(R.layout.fragment_task_exe_work_view, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        start_pause_btn = view.findViewById(R.id.task_detail_exe_view);
        stop_btn = view.findViewById(R.id.task_detail_stop_view);

        connectServer = new ConnectServer();

        TextView taskName = view.findViewById(R.id.task_exe_name);
        taskName.setText(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).taskName);

        start_pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start_pause_btn.isActivated()){
                    if(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode.equals("原地空气净化")){
                        RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 0);
                        Log.d("adwApiManager", "/sys/class/gpio/gpio39/value = " + 1);
                    }else{
                        if(RosService.coverageMapService != null)
                            RosService.coverageMapService.call(new RobotControlRequest(3));
                    }
                    start_pause_btn.setActivated(false);
                    exeTime += System.currentTimeMillis() - startMills;
                }else{
                    if(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode.equals("原地空气净化")){
                        RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 1);
                    }else {
                        if (RosService.coverageMapService != null)
                            RosService.coverageMapService.call(new RobotControlRequest(2));
                    }
                    start_pause_btn.setActivated(true);
                    if (once) {
                        insertTaskHistory(100);
                        ServerInfoTab.task_exe_number++;

                        connectServer.updateInfo();
                        once = false;
                    }
                    startMills = System.currentTimeMillis();
                }
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode.equals("原地空气净化")){
                    RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 1);
                }else {
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
            }
        });
    }

    private void insertTaskHistory(int percent) {
        ContentValues values = new ContentValues();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        values.put("date_created",simpleDateFormat.format(date));
        values.put("task_name", ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).taskName);
        values.put("task_type", "independent task");
        values.put("operator", RCApplication.Operator);
        values.put("mode", ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).mode);
        values.put("percentage", percent);
        RCApplication.db.insert("task_history",null,values);
    }
}