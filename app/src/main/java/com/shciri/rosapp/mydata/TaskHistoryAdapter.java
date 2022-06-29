package com.shciri.rosapp.mydata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shciri.rosapp.R;
import java.util.List;

public class TaskHistoryAdapter extends BaseAdapter {
    private List<ShowData> dataList = null;
    private Context mContext = null;

    public TaskHistoryAdapter(Context context, List<ShowData> data){
        dataList = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null){
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_task_report_list, null);
            vh.mCreateTime = (TextView)convertView.findViewById(R.id.create_time_tv);
            vh.mTaskName = (TextView)convertView.findViewById(R.id.task_name_tv);
            vh.mTaskType = (TextView)convertView.findViewById(R.id.task_type_tv);
            vh.mUsername = (TextView)convertView.findViewById(R.id.username_tv);
            vh.mOperadetail = (TextView)convertView.findViewById(R.id.operator_detail_tv);
            vh.mOperaSend = (TextView)convertView.findViewById(R.id.operator_send_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }
        vh.mCreateTime.setText(dataList.get(position).mCreateTime);
        vh.mTaskName.setText(dataList.get(position).mTaskName);
        vh.mTaskType.setText(dataList.get(position).mTaskType);
        vh.mUsername.setText(dataList.get(position).mUsername);
        vh.mOperadetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        vh.mOperaSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    public static class ShowData {
        public String mCreateTime;
        public String mTaskName;
        public String mTaskType;
        public String mUsername;

        public ShowData(String createTime, String taskName, String taskType, String username){
            mCreateTime = createTime;
            mTaskName = taskName;
            mTaskType=taskType;
            mUsername=username;
        }
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
