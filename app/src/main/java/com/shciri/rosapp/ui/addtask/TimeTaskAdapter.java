package com.shciri.rosapp.ui.addtask;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.shciri.rosapp.R;
import com.shciri.rosapp.mydata.DBUtils;
import com.shciri.rosapp.ui.control.ManageTaskDB;
import com.shciri.rosapp.utils.AlarmManagerUtils;

import java.util.List;

import src.com.jilk.ros.message.CoveragePoints;

public class TimeTaskAdapter extends RecyclerView.Adapter<TimeTaskAdapter.ViewHolder> {

    private List<TimeTaskItemList>localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView originTaskNameTv;
        public final TextView originTaskIDTv;
        public final TextView timeTv;
        public final TextView dateTv;
        public final TextView loopTv;
        public final TextView modeTv;
        public final TextView deleteTv;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            originTaskNameTv = (TextView) view.findViewById(R.id.tv_origin_task_name);
            originTaskIDTv = (TextView) view.findViewById(R.id.tv_origin_task_id);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
            dateTv = (TextView) view.findViewById(R.id.tv_date);
            loopTv = (TextView) view.findViewById(R.id.tv_loop);
            modeTv = (TextView) view.findViewById(R.id.tv_mode);
            deleteTv = (TextView) view.findViewById(R.id.tv_delete);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public TimeTaskAdapter(List<TimeTaskItemList> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.time_task_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        viewHolder.originTaskNameTv.setText(localDataSet.get(position).originTaskName);
        viewHolder.originTaskIDTv.setText(localDataSet.get(position).originTaskId.toString());
        viewHolder.timeTv.setText(localDataSet.get(position).time);
        viewHolder.dateTv.setText(localDataSet.get(position).date);
        viewHolder.loopTv.setText(localDataSet.get(position).loop.toString());
        viewHolder.modeTv.setText(localDataSet.get(position).mode);
        viewHolder.deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    removeData(position);
            }
        });
    }

    //  删除数据
    public void removeData(int position) {
        DBUtils.getInstance().deleteTimeTask(localDataSet.get(position).ID);
        AlarmManagerUtils.getInstance(null).cancelClockAlarm(localDataSet.get(position).ID);
        localDataSet.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class TimeTaskItemList {
        public Integer ID;
        public Integer originTaskId;
        public String originTaskName;
        public String time;
        public String date;
        public Integer loop;
        public String mode;

        public TimeTaskItemList(Integer id, String originTaskName, Integer originTaskId, String time, String date, Integer loop, String mode){
            this.ID = id;
            this.originTaskId = originTaskId;
            this.originTaskName = originTaskName;
            this.time = time;
            this.date = date;
            this.loop = loop;
            this.mode = mode;
        }
    }
}
