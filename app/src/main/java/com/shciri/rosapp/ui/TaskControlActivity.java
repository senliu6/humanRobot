package com.shciri.rosapp.ui;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosService;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.ReceiveHandler;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.MyPGM;
import com.shciri.rosapp.mydata.CH34xAction;
import com.shciri.rosapp.ui.myview.StatusBarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

public class TaskControlActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private OnBackPressedCallback mBackPressedCallback;
    private RosTopic rosTopic = new RosTopic();
    private RosService rosService = new RosService();
    private ReceiveHandler receiveHandler = new ReceiveHandler();

    private CH34xAction ch34xAction;
    private StatusBarView statusBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_control);

        drawerLayout = findViewById(R.id.drawer_layout);
        findViewById(R.id.drawer_close_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                finish();
            }
        });

        //windowSet();

        fragmentTrans();

        backPressedSet();

        RosData.RosDataInit();

        rosSubscribe();
        rosServiceInit();

        statusBarView = findViewById(R.id.statusBar);
        ch34xAction = new CH34xAction(this, statusBarView);
        ch34xAction.queryBatteryInfo();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        statusBarView.setTimeView(simpleDateFormat.format(date));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(myTimeReceiver,filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        ch34xAction.queryAirQuality();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private final BroadcastReceiver myTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
                Date date = new Date(System.currentTimeMillis());
                statusBarView.setTimeView(simpleDateFormat.format(date));
                ch34xAction.queryBatteryInfo();
            }
        }
    };

    public void openDrawerLayout() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void rosSubscribe() {
        if(RosInit.isConnect) {
            try{
                for(String s : RCApplication.client.getTopics()) {
                    for (int i=0; i<rosTopic.TopicName.length; i++) {
                        if(s.equals(rosTopic.TopicName[i])){
                            rosTopic.TopicMatch[i] = true;
                            switch (i){
                                case 0:
                                    rosTopic.subscribeMapTopic(receiveHandler.getMapTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                                    break;
                                case 1:
                                    rosTopic.subscribeCmdVelTopic(receiveHandler.getCmdValTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                                    break;
                                case 2:
                                    rosTopic.subscribeTFTopic(receiveHandler.getTFTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                                    break;
                                case 3:
                                    rosTopic.initGoalTopic(receiveHandler.getGoalTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                                    break;
                                case 4:
                                    rosTopic.initCoverageMapTopic(receiveHandler.getCoverageMapTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                                    break;
                                case 5:
                                    rosTopic.initCoveragePathTopic(receiveHandler.getCoveragePathTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                                    break;
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void rosServiceInit() {
        if(RosInit.isConnect) {
            try {
                for (String s : ((RCApplication) getApplication()).getRosClient().getServices()) {
                    if(s.equals(rosService.ServiceName[0])) {
                        rosService.initCoverageMapService(((RCApplication) getApplication()).getRosClient());
                        return;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void windowSet() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void fragmentTrans() {
        findViewById(R.id.manager_data_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(TaskControlActivity.this, R.id.fragment_control_main);
                navController.navigate(R.id.manageDataFragment);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.location_ll).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(TaskControlActivity.this, R.id.fragment_control_main);
            navController.navigate(R.id.nav_home);
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        findViewById(R.id.tasks_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(TaskControlActivity.this, R.id.fragment_control_main);
                navController.navigate(R.id.manageDataFragment);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    private void backPressedSet() {
        mBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @SuppressLint("RestrictedApi")
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(TaskControlActivity.this, R.id.fragment_control_main);
                if(navController.getBackStack().size() <= 2){
                    finish();
                }else{
                    navController.navigateUp();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, mBackPressedCallback);
    }
}