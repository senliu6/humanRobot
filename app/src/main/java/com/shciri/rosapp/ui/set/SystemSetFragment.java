package com.shciri.rosapp.ui.set;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.mydata.MoreTaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class SystemSetFragment extends Fragment {

    private List<SystemSetAdapter.SystemSetList> systemSetListList;
    private ListView listView;
    private SystemSetAdapter systemSetAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_system_set, container, false);
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

        systemSetListList = new ArrayList<>();
        systemSetListList.add(new SystemSetAdapter.SystemSetList("清除任务历史报告数据"));
        systemSetAdapter = new SystemSetAdapter(getContext(), systemSetListList);
        listView = view.findViewById(R.id.system_set_lv);
        listView.setAdapter(systemSetAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        deleteTaskHistoryAll();
                        break;
                }
            }
        });
    }

    private void deleteTaskHistoryAll() {
        RCApplication.db.execSQL("delete from task_history");
    }
}
