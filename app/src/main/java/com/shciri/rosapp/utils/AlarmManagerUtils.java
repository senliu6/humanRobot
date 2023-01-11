package com.shciri.rosapp.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.shciri.rosapp.server.TaskTimeService;

import java.util.Calendar;

public class AlarmManagerUtils {

    private static final long TIME_INTERVAL = 10 * 1000;//闹钟执行任务的时间间隔
    private Context context;
    public static AlarmManager am;
    public static PendingIntent pendingIntent;

    //
    private AlarmManagerUtils(Context aContext) {
        this.context = aContext;
    }

    //singleton
    private static AlarmManagerUtils instance = null;

    public static AlarmManagerUtils getInstance(Context aContext) {
        if (instance == null) {
            synchronized (AlarmManagerUtils.class) {
                if (instance == null) {
                    instance = new AlarmManagerUtils(aContext);
                }
            }
        }
        return instance;
    }

    public void createGetUpAlarmManager() {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyBroadCastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void getUpAlarmManagerStartWork(Calendar calendar) {
//        calendar.setTimeInMillis(System.currentTimeMillis());
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, pendingIntent);
    }
}

