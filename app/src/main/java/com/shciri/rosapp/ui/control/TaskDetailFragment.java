package com.shciri.rosapp.ui.control;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.ui.datamanagement.DataManagePathInfoFragment;
import com.shciri.rosapp.ui.myview.MapView;

import java.util.ArrayList;

public class TaskDetailFragment extends Fragment {

    private MapView mapView;
    private TextView taskName;
    private TextView pathName;
    private TextView dateCreatedTv;
    private String dbPathName;
    private java.lang.reflect.Type type = new TypeToken<ArrayList<PointF>>(){}.getType();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.task_detail_map_view);
        if(RosData.rosBitmap != null){
            mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        }else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.map_example);
            mapView.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        queryManualPath();

        taskName = view.findViewById(R.id.task_detail_name);
        taskName.setText(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).taskName);

        pathName = view.findViewById(R.id.task_path_name);
        pathName.setText(String.format(getResources().getString(R.string.task_detail_path),dbPathName));

        dateCreatedTv = view.findViewById(R.id.creation_time_tv);
        dateCreatedTv.setText(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).dateCreated);
    }

    private void queryManualPath(){
        Cursor cursor = RCApplication.db.query("manual_path",null, "id=?", new String[]{Integer.toString(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).pathID)}, null, null, null);
        Gson gson = new Gson();
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") String point = cursor.getString(cursor.getColumnIndex("point"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                dbPathName = name;
                mapView.DBPathPointList = gson.fromJson(point, type);
                mapView.setShowDBPath(true);
            }
        }
        cursor.close();
    }
}
