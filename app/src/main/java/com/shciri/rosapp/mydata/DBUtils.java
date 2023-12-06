package com.shciri.rosapp.mydata;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.PointF;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;

import java.util.ArrayList;
import java.util.List;

import src.com.jilk.ros.message.Point;

public class DBUtils {

    private static DBUtils dbUtils;

    java.lang.reflect.Type type = new TypeToken<ArrayList<PointF>>() {
    }.getType();


    public static DBUtils getInstance() {
        if (dbUtils == null) {
            synchronized (DBUtils.class) {
                if (dbUtils == null) {
                    dbUtils = new DBUtils();
                }
            }
        }
        return dbUtils;
    }

    public void DBInsertInfo(int id, String robot_ip) {
        ContentValues values = new ContentValues();
        values.put("my_server_id", id);
        values.put("robot_ip", robot_ip);
        RCApplication.rosIP = robot_ip;
        RCApplication.db.insert("info", null, values);
    }

    public void DBUpdateInfo(int id, String robot_ip) {
        ContentValues values = new ContentValues();
        values.put("robot_ip", robot_ip);
        RCApplication.rosIP = robot_ip;
        RCApplication.db.update("info", values, "my_server_id=?", new String[]{Integer.toString(id)});
    }

    public int DBInsertTimeTask(String originTaskName, Integer originTaskID, String time, String date, Integer mapID, String loop, String mode) {
        ContentValues values = new ContentValues();
        values.put("origin_task_name", originTaskName);
        values.put("origin_task_id", originTaskID);
        values.put("time", time);
        values.put("date", date);
        values.put("map_id", mapID);
        values.put("loop", Integer.valueOf(loop));
        values.put("mode", mode);
        return (int) RCApplication.db.insert("time_task", null, values);
    }

    public int DBQueryInfo() {
        int id = 0;
        Cursor cursor = RCApplication.db.query("info", null, null, null, null, null, null);
        Gson gson = new Gson();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int myID = cursor.getInt(cursor.getColumnIndex("my_server_id"));
                @SuppressLint("Range") String ip = cursor.getString(cursor.getColumnIndex("robot_ip"));
                Log.d("IP", ip);
                id = myID;
                RCApplication.rosIP = ip;
            }
        }
        cursor.close();
        return id;
    }

    public void deleteMap(int id) {
        RCApplication.db.delete("map", "id=?", new String[]{Integer.toString(id)});
    }

    public void deletePathOfMapID(int map_id) {
        RCApplication.db.delete("manual_path", "map_id=?", new String[]{Integer.toString(map_id)});
    }

    public void deleteTimeTask(int id) {
        RCApplication.db.delete("time_task", "id=?", new String[]{Integer.toString(id)});
    }

    /**
     * @param id 任务ID
     * @return 根据任务id获取路径ID
     */
    @SuppressLint("Range")
    public String getPathId(String id) {
        try {
            // 构建查询语句
            String query = "SELECT * FROM time_task WHERE map_id = ? AND id = ?";

            // 执行查询
            String[] selectionArgs = new String[]{Integer.toString(RosData.currentMapID), id};
            Cursor cursor = RCApplication.db.rawQuery(query, selectionArgs);
            if (null != cursor && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    return String.valueOf(cursor.getInt(cursor.getColumnIndex("origin_task_id")));
                }
                cursor.close();
            }

        } catch (SQLiteException e) {
            // 处理异常
            e.printStackTrace();

        }
        return "-2";
    }

    /**
     * @param id 任务ID
     * @return 根据任务id获取任务次数
     */
    @SuppressLint("Range")
    public int getLoopNum(String id) {
        try {
            // 构建查询语句
            String query = "SELECT * FROM time_task WHERE map_id = ? AND id = ?";

            // 执行查询
            String[] selectionArgs = new String[]{Integer.toString(RosData.currentMapID), id};
            Cursor cursor = RCApplication.db.rawQuery(query, selectionArgs);
            if (null != cursor && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    return cursor.getInt(cursor.getColumnIndex("loop"));
                }
                cursor.close();
            }

        } catch (SQLiteException e) {
            // 处理异常
            e.printStackTrace();

        }
        return 1;
    }

    /**
     * @param id 路径ID
     * @return 路径点集合
     */
    @SuppressLint("Range")
    public Point[] getPointS(String id, boolean pathId) {
        try {
            // 构建查询语句
            String query = "SELECT * FROM manual_path WHERE map_id = ? AND id = ?";

            String[] selectionArgs = new String[]{};
            // 执行查询
            if (pathId) {
                selectionArgs = new String[]{Integer.toString(RosData.currentMapID), id};
            } else {
                selectionArgs = new String[]{Integer.toString(RosData.currentMapID), getPathId(id)};
            }
            Cursor cursor = RCApplication.db.rawQuery(query, selectionArgs);
            List<PointF> pointfs = new ArrayList<PointF>();
            Point[] points;
            if (null != cursor && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    pointfs = new Gson().fromJson(cursor.getString(cursor.getColumnIndex("point")), type);
                }
                cursor.close();
                points = new Point[pointfs.size()];
                for (int i = 0; i < pointfs.size(); i++) {
                    points[i] = RosData.getActualXY(pointfs.get(i).x, pointfs.get(i).y);
                }
                return points;
            }
        } catch (SQLiteException e) {
            // 处理异常
            e.printStackTrace();

        }
        return null;
    }

}
