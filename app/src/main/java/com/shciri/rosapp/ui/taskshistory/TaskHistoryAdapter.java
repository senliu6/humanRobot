package com.shciri.rosapp.ui.taskshistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shciri.rosapp.R;
import java.util.List;

public class TaskHistoryAdapter extends BaseAdapter {
    private List<TaskHistoryList> dataList = null;
    private Context mContext = null;

    public TaskHistoryAdapter(Context context, List<TaskHistoryList> data){
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
            vh.dateCreated = (TextView)convertView.findViewById(R.id.create_time_tv);
            vh.taskName = (TextView)convertView.findViewById(R.id.task_name_tv);
            vh.taskType = (TextView)convertView.findViewById(R.id.task_type_tv);
            vh.operator = (TextView)convertView.findViewById(R.id.operator_tv);
            vh.mode = (TextView)convertView.findViewById(R.id.mode_tv);
            vh.percentage = (TextView)convertView.findViewById(R.id.percentage_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder)convertView.getTag();
        }
        vh.dateCreated.setText(dataList.get(position).dateCreated);
        vh.taskName.setText(dataList.get(position).taskName);
        if(dataList.get(position).taskType.equals("independent task"))
            vh.taskType.setText("独立任务");
        else
            vh.taskType.setText("组合任务");
        vh.operator.setText(dataList.get(position).operator);
        vh.mode.setText(dataList.get(position).mode);
        vh.percentage.setText(String.format("%s%%", dataList.get(position).percentage));
        return convertView;
    }

    public final class ViewHolder
    {
        public TextView dateCreated;
        public TextView taskName;
        public TextView taskType;
        public TextView operator;
        public TextView mode;
        public TextView percentage;
    }
    

    public static class TaskHistoryList {
        public String dateCreated;
        public String taskName;
        public String taskType;
        public String operator;
        public String mode;
        public int percentage;

        public TaskHistoryList(String createTime, String taskName, String taskType, String operator, String mode, int percentage){
            this.dateCreated = createTime;
            this.taskName = taskName;
            this.taskType = taskType;
            this.operator = operator;
            this.mode = mode;
            this.percentage = percentage;
        }
    }
}
