package com.shciri.rosapp.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmManagerUtils {

    private static final long TIME_INTERVAL = 10 * 1000;//闹钟执行任务的时间间隔
    private Context context;
    public static AlarmManager alarmManager;
    public static PendingIntent pendingIntent;
    public static String ACTION_PERIOD_CLOCK = "com.shciri.rosapp.alarm";
    //singleton
    private static AlarmManagerUtils instance = null;

    private AlarmManagerUtils(Context aContext) {
        this.context = aContext;
        alarmManager = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
    }

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

    public void createPeriodAlarmManager(Calendar calendar, int alarmID, boolean fun_switch, boolean led_switch) {
//        Intent intent = new Intent(context, MyBroadCastReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Intent intent = new Intent();
        intent.setAction(ACTION_PERIOD_CLOCK);
        intent.putExtra("alarmID", alarmID); //此处闹钟id = 数据库id, 如果有多个周期组闹钟, 就可以代表唯一性,方便覆盖或者关闭闹钟
        intent.putExtra("fun_switch", fun_switch);
        intent.putExtra("led_switch", led_switch);
        intent.putExtra("week", calendar.get(Calendar.DAY_OF_WEEK));
        intent.putExtra("hour", calendar.get(Calendar.HOUR_OF_DAY));
        intent.putExtra("minute", calendar.get(Calendar.MINUTE));
        intent.putExtra("second", 1);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        //这里也是关键,超过闹钟时间了,代表下周要触发,加7天
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_WEEK, 7);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * @param alarmID 这个id就是自己的逻辑id了,自定义,比如我这闹钟实体存放在数据库的,就以数据库id为准,能够确保唯一性
     */
    public void cancelClockAlarm(int alarmID) {
        realCancelClock(ACTION_PERIOD_CLOCK, alarmID);
    }

    private void realCancelClock(String action, int clockId) {
        Intent intent = new Intent();
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, clockId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(pendingIntent);
        Log.d("Alarm", "取消闹钟" + clockId);
    }

//    public void cancelAlarmManager() {
//        Intent intent = new Intent(context, MyBroadCastReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        alarmManager.cancel(pendingIntent);
//    }

    private void getAlarmManagerList() {
        Intent intent = new Intent(context, MyBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 322, intent, PendingIntent.FLAG_IMMUTABLE);

        List<AlarmManager.AlarmClockInfo> alarmClockInfos = new ArrayList<>();

    // 获取全部闹钟信息
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            try {
                AlarmManager.AlarmClockInfo alarmClockInfo = alarmManager.getNextAlarmClock();
                if (alarmClockInfo != null) {
                    alarmClockInfos.add(alarmClockInfo);
                } else {
                    break; // 没有更多的闹钟信息了
                }
            } catch (Exception e) {
                e.printStackTrace();
                break; // 获取失败或发生异常，结束循环
            }
        }


    }
}

