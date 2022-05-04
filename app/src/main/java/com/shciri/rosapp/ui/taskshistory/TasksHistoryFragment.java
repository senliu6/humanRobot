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
        List<ShowData> datas = new ArrayList<ShowData>();
        ShowData data1 = new ShowData("2022-04-21  １9:03:00", "execute_task_2", "组合任务", "admin");
        ShowData data2 = new ShowData("2022-04-21  １9:03:00", "execute_task_2", "组合任务", "admin");
        ShowData data3 = new ShowData("2022-04-21  １9:03:00", "execute_task_2", "组合任务", "admin");
        ShowData data4 = new ShowData("2022-04-21  １9:03:00", "execute_task_2", "组合任务", "admin");
        ShowData data5 = new ShowData("2022-04-21  １9:03:00", "execute_task_2", "组合任务", "admin");
        ShowData data6 = new ShowData("2022-04-21  １9:03:00", "execute_task_2", "组合任务", "admin");
        datas.add(data1);
        datas.add(data2);
        datas.add(data3);
        datas.add(data4);
        datas.add(data5);
        datas.add(data6);

        lv = (ListView) view.findViewById(R.id.task_report_lv);
        lv.setAdapter(new MyAdapter(getActivity(), datas));
    }

    public class ShowData {
        public ShowData(String createTime, String taskName, String taskType, String username){
             mCreateTime = createTime;
           mTaskName = taskName;
            mTaskType=taskType;
            mUsername=username;
        }
        public String mCreateTime;
        public String mTaskName;
        public String mTaskType;
        public String mUsername;
    }

    public class MyAdapter extends BaseAdapter {
        private List<ShowData> mDatas = null;
        private Context mContext = null;

        public MyAdapter(Context context, List<ShowData> datas){
            mDatas = datas;
            mContext = context;
        }
        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int i) {
            return mDatas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;
            if (view == null){
                vh = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_task_report_list, null);
                vh.mCreateTime = (TextView)view.findViewById(R.id.create_time_tv);
                vh.mTaskName = (TextView)view.findViewById(R.id.task_name_tv);
                vh.mTaskType = (TextView)view.findViewById(R.id.task_type_tv);
                vh.mUsername = (TextView)view.findViewById(R.id.username_tv);
                vh.mOperadetail = (TextView)view.findViewById(R.id.operator_detail_tv);
                vh.mOperaSend = (TextView)view.findViewById(R.id.operator_send_tv);
                view.setTag(vh);
            } else {
                vh = (ViewHolder)view.getTag();
            }
            vh.mCreateTime.setText(mDatas.get(i).mCreateTime);
            vh.mTaskName.setText(mDatas.get(i).mTaskName);
            vh.mTaskType.setText(mDatas.get(i).mTaskType);
            vh.mUsername.setText(mDatas.get(i).mUsername);
            vh.mOperadetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"Operator detail:" + getItemId(i),Toast.LENGTH_SHORT).show();
                }
            });
            vh.mOperaSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"Operator send:" + getItemId(i),Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }

        public final class ViewHolder
        {
            public TextView mCreateTime;
            public TextView mTaskName;
            public TextView mTaskType;
            public TextView mUsername;
            public TextView mOperadetail;
            public TextView mOperaSend;
        }
    }
}