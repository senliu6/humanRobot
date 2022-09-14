package com.shciri.rosapp.mydata;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.ui.control.ManageTaskDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBUtils {

    private static DBUtils dbUtils;

    public static DBUtils getInstance() {
        if(dbUtils == null){
            dbUtils = new DBUtils();
            return dbUtils;
        }
        return dbUtils;
    }

    public void DBInsertInfo(int id) {
        ContentValues values = new ContentValues();
        values.put("my_server_id",id);
        RCApplication.db.insert("info",null,values);
    }

    public int DBQueryInfo() {
        int id = 0;
        Cursor cursor = RCApplication.db.query("info",null, null, null, null, null, null);
        Gson gson = new Gson();
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") int myID = cursor.getInt(cursor.getColumnIndex("my_server_id"));
                id = myID;
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
}
