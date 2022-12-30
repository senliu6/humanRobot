package com.shciri.rosapp.mydata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="CREATE TABLE IF NOT EXISTS user(id integer primary key autoincrement,username varchar(20),password varchar(20),age integer)";
        db.execSQL(sql);
        sql="CREATE TABLE IF NOT EXISTS map(id integer primary key autoincrement,name varchar(20),time varchar(20),width integer,height integer, md5 varchar(32))";
        db.execSQL(sql);
        sql="CREATE TABLE IF NOT EXISTS time_task(id integer primary key autoincrement,task_name varchar(20),time varchar(20),date varchar(20),map_name varchar(20),loop integer)";
        db.execSQL(sql);
        sql="CREATE TABLE IF NOT EXISTS manual_path(id integer primary key autoincrement,name varchar(20),map_id integer,point blob)";
        db.execSQL(sql);
        sql="CREATE TABLE IF NOT EXISTS task(id integer primary key autoincrement,name varchar(20),map_id integer,path_id blob, date_created varchar(20))";
        db.execSQL(sql);
        sql="CREATE TABLE IF NOT EXISTS task_history(id integer primary key autoincrement,date_created varchar(20),task_name varchar(20),task_type varchar(20),operator varchar(20), mode varchar(20), percentage integer)";
        db.execSQL(sql);
        sql="CREATE TABLE IF NOT EXISTS info(id integer primary key autoincrement,my_server_id int, robot_ip varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        String sql="ALTER TABLE map ADD COLUMN md5 varchar(32) NOT NULL DEFAULT 0;";
        String sql="CREATE TABLE IF NOT EXISTS info(id integer primary key autoincrement,my_server_id int)";
        db.execSQL(sql);
    }
}
