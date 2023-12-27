package com.shciri.rosapp.ui.addtask;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;

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
        view.findViewById(R.id.new_time_task_bt).setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_addTaskFragment_to_addNewTimeTaskFragment));

        view.findViewById(R.id.return_ll).setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());

        timeTaskItemList = new ArrayList<TimeTaskAdapter.TimeTaskItemList>();
        queryTimeTaskList();
        TimeTaskAdapter timeTaskAdapter = new TimeTaskAdapter(timeTaskItemList);
        RecyclerView recyclerView = view.findViewById(R.id.qlv_task);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        //设置Adapter
        recyclerView.setAdapter(timeTaskAdapter);
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        // 设置Item添加和移除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void queryTimeTaskList() {
        try {
            //查询全部数据
            Cursor cursor = RCApplication.db.query("time_task", null, "map_id=?", new String[]{Integer.toString(RosData.currentMapID)}, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") String origin_task_name = cursor.getString(cursor.getColumnIndex("origin_task_name"));
                    @SuppressLint("Range") int origin_task_id = cursor.getInt(cursor.getColumnIndex("origin_task_id"));
                    @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                    @SuppressLint("Range") int loop = cursor.getInt(cursor.getColumnIndex("loop"));
                    @SuppressLint("Range") String mode = cursor.getString(cursor.getColumnIndex("mode"));
                    Log.d("CeshiTAG", "task" + origin_task_id + "name" + origin_task_name + "id" + id);
                    timeTaskItemList.add(new TimeTaskAdapter.TimeTaskItemList(id, origin_task_name, origin_task_id, time, date, loop, mode));
                }
            }
            cursor.close();
        } catch (Exception e) {
            Toaster.showShort(e.getMessage());
        }
    }
}