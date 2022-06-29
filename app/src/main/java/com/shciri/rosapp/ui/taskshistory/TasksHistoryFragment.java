package com.shciri.rosapp.ui.taskshistory;

import android.content.Context;
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
import com.shciri.rosapp.mydata.TaskHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class TasksHistoryFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    ListView lv;

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
        List<TaskHistoryAdapter.ShowData> datas = new ArrayList<TaskHistoryAdapter.ShowData>();
        TaskHistoryAdapter.ShowData data1 = new TaskHistoryAdapter.ShowData("2022-04-21  19:03:00", "execute_task_2", "组合任务", "admin");
        TaskHistoryAdapter.ShowData data2 = new TaskHistoryAdapter.ShowData("2022-04-21  19:03:00", "execute_task_2", "组合任务", "admin");
        TaskHistoryAdapter.ShowData data3 = new TaskHistoryAdapter.ShowData("2022-04-21  19:03:00", "execute_task_2", "组合任务", "admin");
        TaskHistoryAdapter.ShowData data4 = new TaskHistoryAdapter.ShowData("2022-04-21  19:03:00", "execute_task_2", "组合任务", "admin");
        TaskHistoryAdapter.ShowData data5 = new TaskHistoryAdapter.ShowData("2022-04-21  19:03:00", "execute_task_2", "组合任务", "admin");
        TaskHistoryAdapter.ShowData data6 = new TaskHistoryAdapter.ShowData("2022-04-21  19:03:00", "execute_task_2", "组合任务", "admin");
        datas.add(data1);
        datas.add(data2);
        datas.add(data3);
        datas.add(data4);
        datas.add(data5);
        datas.add(data6);

        lv = (ListView) view.findViewById(R.id.task_report_lv);
        lv.setAdapter(new TaskHistoryAdapter(getActivity(), datas));
    }
}