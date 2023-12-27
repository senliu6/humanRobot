package com.shciri.rosapp;

import android.app.ADWApiManager;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.dmros.data.LanguageType;
import com.shciri.rosapp.dmros.data.Settings;
import com.shciri.rosapp.ui.CrashActivity;
import com.shciri.rosapp.utils.LanguageUtil;
import com.shciri.rosapp.utils.SharedPreferencesUtil;
import com.shciri.rosapp.utils.UartVCP;
import com.shciri.rosapp.utils.protocol.ReplyIPC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RCApplication extends Application {
    public static ADWApiManager adwApiManager;
    public static ROSBridgeClient client;
    public static String rosIP = "11.11.11.111";
    public static String rosPort = "9090";
    public static SQLiteDatabase db;
    public static String Operator;
    public static byte localBattery;
    public static UartVCP uartVCP;
    public static ReplyIPC replyIPC;
    public static MediaPlayer mediaPlayer;

    private String TAG = "RCApplication";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mediaPlayer = new MediaPlayer();
        //无需再调用setDataSource
        mediaPlayer = MediaPlayer.create(this, R.raw.disinfecting_warning);
        uartVCP = new UartVCP();
        replyIPC = new ReplyIPC();
        try {
            String manufacturer = Build.MANUFACTURER;
            String manufacturerDefault = "rockchip";
            if (manufacturerDefault.equals(manufacturer)) {
                adwApiManager = new ADWApiManager(this);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 0);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio40/value", 0);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio41/value", 0);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio42/value", 0);
                adwApiManager.SetDeviceTimeZone("Asia/Shanghai");
                String string = adwApiManager.GetDeviceRamSize();
                Log.d(TAG, "adwApiManager ram = " + string);
                Log.d(TAG, "adwApiManager ip = " + adwApiManager.GetDeviceIpAddr());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        new Thread(() -> {
            while (true) {
                replyIPC.getFullData();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Toaster.init(this);

        // Crash 捕捉界面
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
                .enabled(true)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                // 重启的 Activity
                .restartActivity(MainActivity.class)
                // 错误的 Activity
                .errorActivity(CrashActivity.class)
                // 设置监听器
                //.eventListener(new YourCustomEventListener())
                .apply();

        String language = SharedPreferencesUtil.Companion.getValue(getContext(), Settings.LANGUAGE, LanguageType.CHINESE.getLanguage(), String.class);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.INSTANCE.changeAppLanguage(getApplicationContext(), language);
        }
    }

    private void pushMessageToIPC() {

    }

    @Override
    public void onTerminate() {
        if (client != null) {
            client.disconnect();
        }
        super.onTerminate();
    }

    public ROSBridgeClient getRosClient() {
        return client;
    }

    public void setRosClient(ROSBridgeClient client) {
        RCApplication.client = client;
    }

    //线程池数量
    private static final int CORE_POOL_SIZE = 5;
    //最大线程数
    private static final int MAXIMUM_POOL_SIZE = 10;
    //存活时间
    private static final long KEEP_ALIVE_TIME = 10L;
    //时间类型
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private static ExecutorService executorService;

    public static ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = createThreadPool();
        }
        return executorService;
    }

    private static ExecutorService createThreadPool() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TIME_UNIT,
                new LinkedBlockingQueue<>(),
                new MyThreadFactory()
        );
    }


    private static class MyThreadFactory implements ThreadFactory {
        private final AtomicInteger threadCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "MyThreadPool-" + threadCount.getAndIncrement());
        }
    }

    public static Context getContext() {
        return context;
    }
}
