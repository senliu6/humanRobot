package com.shciri.rosapp.ui.taskdetail;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosService;
import com.shciri.rosapp.mydata.DBOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import src.com.jilk.ros.message.RobotControlRequest;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TaskDetailWorkViewFragment extends Fragment {

    ImageView start_pause_btn;
    ImageView stop_btn;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        RosService.coverageMapService.call(new RobotControlRequest(1));
//        DBOpenHelper dbOpenHelper = new DBOpenHelper(getContext(),"users.db",null,1);
//        db = dbOpenHelper.getWritableDatabase();
        return inflater.inflate(R.layout.fragment_task_detail_work_view, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        start_pause_btn = view.findViewById(R.id.task_detail_exe_view);
        stop_btn = view.findViewById(R.id.task_detail_stop_view);

        start_pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start_pause_btn.isActivated()){
                    RosService.coverageMapService.call(new RobotControlRequest(3));
                    start_pause_btn.setActivated(false);
                }else{
                    RosService.coverageMapService.call(new RobotControlRequest(2));
                    start_pause_btn.setActivated(true);
//                    ContentValues values = new ContentValues();
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
//                    Date date = new Date(System.currentTimeMillis());
//                    values.put("createTime",simpleDateFormat.format(date));
//                    values.put("taskName",);
                }
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RosService.coverageMapService.call(new RobotControlRequest(4));

            }
        });
    }
}