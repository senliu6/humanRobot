package com.shciri.rosapp.ui.control;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.mydata.MoreTaskAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoreTaskFragment extends Fragment {

    private ListView listView;
    private MoreTaskAdapter moreTaskAdapter;
    private List<MoreTaskAdapter.MoreTaskList> moreTaskList;
    private int currentPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_more_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        listView = view.findViewById(R.id.more_task_lv);
        moreTaskList = new ArrayList<>();
        for (ManageTaskDB.TaskList x :
                ManageTaskDB.taskLists) {
            moreTaskList.add(new MoreTaskAdapter.MoreTaskList(x.ID, x.taskName, 1, getString(R.string.mode_none)));
        }
        moreTaskAdapter = new MoreTaskAdapter(getContext(), moreTaskList);

        listView.setAdapter(moreTaskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (ImageView x : moreTaskAdapter.yesIvList) {
                    if(x.getVisibility() == View.VISIBLE)
                        x.setVisibility(View.INVISIBLE);
                }
                moreTaskAdapter.yesIvList.get(position).setVisibility(View.VISIBLE);
                currentPosition = position;
                ManageTaskDB.currentTaskIndex = currentPosition;
            }
        });

        view.findViewById(R.id.delete_task_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition < moreTaskList.size())
                    deleteTask();
            }
        });

        view.findViewById(R.id.detail_task_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskDetailFragment);
            }
        });

        view.findViewById(R.id.start_task_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskExeFragment);
            }
        });
    }

    private void deleteTask() {
        RCApplication.db.delete("task","id=?", new String[]{Integer.toString(moreTaskList.get(currentPosition).ID)});
        moreTaskList.remove(currentPosition);
        moreTaskAdapter.notifyDataSetChanged();
    }
}
