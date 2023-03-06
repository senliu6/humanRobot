package com.shciri.rosapp.mydata;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.shciri.rosapp.RCApplication;

public class DBUtils {

    private static DBUtils dbUtils;

    public static DBUtils getInstance() {
        if(dbUtils == null){
            dbUtils = new DBUtils();
            return dbUtils;
        }
        return dbUtils;
    }

    public void DBInsertInfo(int id, String robot_ip) {
        ContentValues values = new ContentValues();
        values.put("my_server_id",id);
        values.put("robot_ip",robot_ip);
        RCApplication.rosIP = robot_ip;
        RCApplication.db.insert("info",null,values);
    }

    public void DBUpdateInfo(int id, String robot_ip) {
        ContentValues values = new ContentValues();
        values.put("robot_ip",robot_ip);
        RCApplication.rosIP = robot_ip;
        RCApplication.db.update("info",values,"my_server_id=?", new String[]{Integer.toString(id)});
    }

    public int DBInsertTimeTask(String originTaskName, Integer originTaskID, String time, String date, Integer mapID, String loop, String mode) {
        ContentValues values = new ContentValues();
        values.put("origin_task_name",originTaskName);
        values.put("origin_task_id",originTaskID);
        values.put("time",time);
        values.put("date",date);
        values.put("map_id",mapID);
        values.put("loop",Integer.valueOf(loop));
        values.put("mode",mode);
        return (int)RCApplication.db.insert("time_task",null,values);
    }

    public int DBQueryInfo() {
        int id = 0;
        Cursor cursor = RCApplication.db.query("info",null, null, null, null, null, null);
        Gson gson = new Gson();
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") int myID = cursor.getInt(cursor.getColumnIndex("my_server_id"));
                @SuppressLint("Range") String ip = cursor.getString(cursor.getColumnIndex("robot_ip"));
                Log.d("IP",ip);
                id = myID;
                RCApplication.rosIP = ip;
            }
        }
        cursor.close();
        return id;
    }

    public void deleteMap(int id) {
        RCApplication.db.delete("map","id=?", new String[]{Integer.toString(id)});
    }

    public void deletePathOfMapID(int map_id) {
        RCApplication.db.delete("manual_path","map_id=?", new String[]{Integer.toString(map_id)});
    }

    public void deleteTimeTask(int id) {
        RCApplication.db.delete("time_task","id=?", new String[]{Integer.toString(id)});
    }
}
