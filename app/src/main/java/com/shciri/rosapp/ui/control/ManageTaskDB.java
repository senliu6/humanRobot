package com.shciri.rosapp.ui.control;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.google.gson.Gson;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.ui.datamanagement.DataManagePathInfoFragment;

import java.util.ArrayList;

public class ManageTaskDB {

    public static ArrayList<TaskList> taskLists;
    public static int currentTaskIndex;

    private final Gson gson = new Gson();

    public void queryTask() {
        Cursor cursor = RCApplication.db.query("task",null, "map_id=?", new String[]{Integer.toString(RosData.currentMapID)}, null, null, null);
        taskLists = new ArrayList<>();
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String pathId = cursor.getString(cursor.getColumnIndex("path_id"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date_created"));
                taskLists.add(new TaskList(id, name, gson.fromJson(pathId, int.class), date));
            }
        }
        cursor.close();
    }

    public static class TaskList {
        public int ID;
        public String taskName;
        public int pathID;
        public String dateCreated;
        public String mode;

        public TaskList(int id, String name, int pathID, String dateCreated){
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