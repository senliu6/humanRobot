package com.shciri.rosapp.rosdata;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shciri.rosapp.R;

import java.util.ArrayList;
import java.util.List;

public class MoreTaskAdapter extends BaseAdapter {

    private List<MoreTaskList> moreTaskLists;
    LayoutInflater inflater;
    public int yesPosition;
    public List<ImageView> yesIvList = new ArrayList<>();

    public MoreTaskAdapter(Context context, List<MoreTaskList> list) {
        inflater = LayoutInflater.from(context);
        moreTaskLists = list;
    }

    @Override
    public int getCount() {
        return moreTaskLists.size();
    }

    @Override
    public MoreTaskList getItem(int position) {
        return moreTaskLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_more_task, parent, false);
            vh = new ViewHolder();
            vh.nameTv = convertView.findViewById(R.id.more_task_name);
            vh.cycleIndexTv = convertView.findViewById(R.id.more_task_cycle_index);
            vh.modeTv = convertView.findViewById(R.id.more_task_mode);
            yesIvList.add(convertView.findViewById(R.id.yes_iv));
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        int colorPos=position%2;
        if(colorPos==1)
            convertView.setBackgroundColor(Color.argb(30, 255, 255, 255)); //颜色设置
        else
            convertView.setBackgroundColor(Color.argb(30, 100, 100, 100));//颜色设置

        MoreTaskList item = getItem(position);
        vh.nameTv.setText(item.taskName);
        vh.cycleIndexTv.setText(Integer.toString(item.cycleIndex));
        vh.modeTv.setText(item.mode);
        return convertView;
    }

    static class ViewHolder {
        TextView nameTv;
        TextView cycleIndexTv;
        TextView modeTv;
    }

    public static class MoreTaskList {
        public int ID;
        public String taskName;
        public int cycleIndex;
        public String mode;

        public MoreTaskList(int id, String name, int cycleIndex, String mode){
            this.ID = id;
            taskName = name;
            this.cycleIndex = cycleIndex;
            this.mode = mode;
        }
    }
}
