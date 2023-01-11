package com.shciri.rosapp.server;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.shciri.rosapp.utils.AlarmManagerUtils;

public class TaskTimeService extends Service {
    private static final String TAG = "TaskTimeService";
    public TaskTimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}

