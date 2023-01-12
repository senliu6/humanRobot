package com.shciri.rosapp.ui.addtask;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.ui.taskshistory.TaskHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AddTaskFragment extends Fragment {

    private ViewPager viewPager;
    TabLayout tabs;
    private List<TimeTaskAdapter.TimeTaskItemList> timeTaskItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_task, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.new_time_task_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_addNewTimeTaskFragment);
            }
        });

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        view.findViewById(R.id.new_task_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_addNewTaskFragment);
            }
        });

        timeTaskItemList = new ArrayList<TimeTaskAdapter.TimeTaskItemList>();
//        TimeTaskAdapter timeTaskAdapter = new TimeTaskAdapter(data);
//        RecyclerView recyclerView = view.findViewById(R.id.qlv_task);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        //设置布局管理器
//        recyclerView.setLayoutManager(layoutManager);
//        //设置为垂直布局，这也是默认的
//        layoutManager.setOrientation(RecyclerView.VERTICAL);
//        //设置Adapter
//        recyclerView.setAdapter(data);
    }

    private void queryTimeTaskList() {
        //查询全部数据
        Cursor cursor = RCApplication.db.query("time_task",null, null, null, null, null, null);
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") String task_name = cursor.getString(cursor.getColumnIndex("task_name"));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String map_name = cursor.getString(cursor.getColumnIndex("map_name"));
                @SuppressLint("Range") int loop = cursor.getInt(cursor.getColumnIndex("loop"));
                //timeTaskItemList.add(new TimeTaskAdapter.TimeTaskItemList((task_name, time, date, map_name, loop));
            }
        }
        cursor.close();
    }
}