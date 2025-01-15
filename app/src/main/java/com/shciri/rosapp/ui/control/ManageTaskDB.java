package com.shciri.rosapp.ui.control;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.ui.datamanagement.DataManagePathInfoFragment;

import java.util.ArrayList;
import java.util.List;

public class ManageTaskDB {

    public static ArrayList<TaskItemList> taskLists;
    public static List<String> taskNameList;
    public static int currentTaskIndex;

    private final Gson gson = new Gson();

    public void queryTask() {
        Cursor cursor = RCApplication.db.query("task",null, "map_id=?", new String[]{RosData.currentMapID}, null, null, null);
        taskLists = new ArrayList<>();
        taskNameList = new ArrayList<>();
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String pathId = cursor.getString(cursor.getColumnIndex("path_id"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date_created"));
                taskLists.add(new TaskItemList(id, name, gson.fromJson(pathId, int.class), date));
                taskNameList.add(name);
            }
        }
        cursor.close();
    }

    public static class TaskItemList {
        public int ID;
        public String taskName;
        public int pathID;
        public String dateCreated;
        public String mode;

        public TaskItemList(int id, String name, int pathID, String dateCreated){
            this.ID = id;
            this.taskName = name;
            this.pathID = pathID;
            this.dateCreated = dateCreated;
        }

        private String getTaskName() {
            return taskName;
        }
    }
}