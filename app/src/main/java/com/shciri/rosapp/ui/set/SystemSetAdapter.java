package com.shciri.rosapp.ui.set;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shciri.rosapp.R;
import com.shciri.rosapp.mydata.MoreTaskAdapter;

import java.util.List;
import java.util.zip.Inflater;

public class SystemSetAdapter extends BaseAdapter {

    private List<SystemSetList> systemSetLists;
    private LayoutInflater inflater;

    public SystemSetAdapter(Context context, List<SystemSetList> systemSetLists) {
        this.systemSetLists =systemSetLists;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return systemSetLists.size();
    }

    @Override
    public SystemSetList getItem(int position) {
        return systemSetLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_system_set, parent, false);
            vh = new ViewHolder();
            vh.title = convertView.findViewById(R.id.system_set_option);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        int colorPos=position%2;
        if(colorPos==1)
            convertView.setBackgroundColor(Color.argb(30, 255, 255, 255)); //颜色设置
        else
            convertView.setBackgroundColor(Color.argb(30, 100, 100, 100));//颜色设置

        SystemSetList item = getItem(position);
        vh.title.setText(item.option);
        return convertView;
    }

    static class ViewHolder {
        public TextView title;
    }

    public static class SystemSetList {
        public String option;

        public SystemSetList(String option) {
            this.option = option;
        }
    }
}
