package com.shciri.rosapp.ui.taskshistory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.mydata.TaskHistoryAdapter;
import com.shciri.rosapp.ui.datamanagement.DataManagePathInfoFragment;

import java.util.ArrayList;
import java.util.List;

public class TasksHistoryFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    ListView lv;
    private List<TaskHistoryAdapter.TaskHistoryList> taskHistoryLists;

    public static TasksHistoryFragment newInstance(int index) {
        TasksHistoryFragment fragment = new TasksHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_history_report_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        lv = (ListView) view.findViewById(R.id.task_report_lv);
        taskHistoryLists = new ArrayList<TaskHistoryAdapter.TaskHistoryList>();
        queryTaskHistory();
        lv.setAdapter(new TaskHistoryAdapter(getActivity(), taskHistoryLists));
    }

    //id integer primary key autoincrement,date_created varchar(20),task_name varchar(20),task_type varchar(20),operator varchar(20), mode varchar(20), percentage integer
    private void queryTaskHistory() {
        //查询全部数据
        Cursor cursor = RCApplication.db.query("task_history",null, null, null, null, null, null);
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") String date_created = cursor.getString(cursor.getColumnIndex("date_created"));
                @SuppressLint("Range") String task_name = cursor.getString(cursor.getColumnIndex("task_name"));
                @SuppressLint("Range") String task_type = cursor.getString(cursor.getColumnIndex("task_type"));
                @SuppressLint("Range") String operator = cursor.getString(cursor.getColumnIndex("operator"));
                @SuppressLint("Range") String mode = cursor.getString(cursor.getColumnIndex("mode"));
                @SuppressLint("Range") int percentage = cursor.getInt(cursor.getColumnIndex("percentage"));
                taskHistoryLists.add(new TaskHistoryAdapter.TaskHistoryList(date_created, task_name, task_type, operator, mode, percentage));
            }
        }
        cursor.close();
    }
}