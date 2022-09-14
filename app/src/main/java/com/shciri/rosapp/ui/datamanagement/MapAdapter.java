package com.shciri.rosapp.ui.datamanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shciri.rosapp.R;
import com.shciri.rosapp.mydata.MoreTaskAdapter;

import java.util.List;

public class MapAdapter extends BaseAdapter {

    public static List<MapList> mapLists;
    LayoutInflater inflater;

    public MapAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void removeItem(int position) {
        mapLists.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mapLists.size();
    }

    @Override
    public MapList getItem(int position) {
        return mapLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_map_title, parent, false);
            vh = new ViewHolder();
            vh.iv_icon = convertView.findViewById(R.id.iv_icon);
            vh.tv_name = convertView.findViewById(R.id.tv_name);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        MapList item = getItem(position);
        vh.tv_name.setText(item.name);
        return convertView;
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
    }

    public static class MapList {
        public int id;
        public String name;
        public String time;
        public int width;
        public int height;

        public MapList(int id, String name, String time, int width, int height){
            this.id = id;
            this.name = name;
            this.time = time;
            this.width = width;
            this.height = height;
        }
    }
}
