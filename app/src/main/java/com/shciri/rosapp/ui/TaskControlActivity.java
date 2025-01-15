package com.shciri.rosapp.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.base.BaseActivity;
import com.shciri.rosapp.databinding.ActivityTaskControlBinding;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosService;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.ReceiveHandler;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.data.UserList;
import com.shciri.rosapp.server.AlarmService;
import com.shciri.rosapp.ui.view.StatusBarView;
import com.shciri.rosapp.utils.AlarmManagerUtils;
import com.shciri.rosapp.utils.MyBroadCastReceiver;
import com.shciri.rosapp.utils.protocol.ReplyIPC;
import com.shciri.rosapp.utils.protocol.RequestIPC;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import src.com.jilk.ros.message.StateMachineRequest;

/**
 * @author
 */
public class TaskControlActivity extends BaseActivity {
    private OnBackPressedCallback mBackPressedCallback;
    private RosTopic rosTopic = new RosTopic();
    private RosService rosService = new RosService();
    private ReceiveHandler receiveHandler = new ReceiveHandler();
    private StatusBarView statusBarView;

    private ActivityTaskControlBinding binding;

    private ExecutorService executorService = RCApplication.getExecutorService();

    private List<String> topicingList = new ArrayList<String>();

    private static RosInit rosInit = RosInit.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rosTopic.setContext(this);

        EventBus.getDefault().register(this);

        AlarmManagerUtils.getInstance(this);


        fragmentTrans();

        backPressedSet();

        RosData.RosDataInit();

        rosSubscribe();
        rosServiceInit();
        Intent intent = new Intent(this, AlarmService.class);
        startService(intent);

        statusBarView = findViewById(R.id.statusBar);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        statusBarView.setTimeView(simpleDateFormat.format(date));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(AlarmManagerUtils.ACTION_PERIOD_CLOCK);
        registerReceiver(myTimeReceiver, filter);

        statusBarView.setConnectStatus(RosInit.isConnect);

        statusBarView.setBatteryPercent(RCApplication.replyIPC.batteryReply.getCapacity_percent());

        rosInit.setOnRosConnectListener(connected -> {
            if (!isFinishing()) {
                toast(R.string.ros_connect_fail);
                runOnUiThread(() -> statusBarView.setConnectStatus(connected));
            }
        });

    }

    private final BroadcastReceiver myTimeReceiver = new MyBroadCastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
                Date date = new Date(System.currentTimeMillis());
                statusBarView.setTimeView(simpleDateFormat.format(date));
                statusBarView.setConnectStatus(RosInit.isConnect);

                byte[] data = RequestIPC.batteryRequest();
                RCApplication.uartVCP.sendData(data);
                byte[] response = new byte[100];
                int len = RCApplication.uartVCP.readData(response);
                RCApplication.replyIPC.ipc_put_rx_byte(response, len);
            }
        }
    };

    public void openDrawerLayout() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void rosSubscribe() {
        if (RosInit.isConnect) {
            executorService.submit(() -> {
                try {
                    long start = System.currentTimeMillis();
                    Log.d("CeshiTAG", "topicList" + RCApplication.client.getTopics().length);
//                    rosTopic.subscribeMapTopic(receiveHandler.getMapTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                    topicingList.clear();
                    for (String s : RCApplication.client.getTopics()) {

                        for (int i = 0; i < RosTopic.TopicName.size(); i++) {
//                            Log.d("CeshiTAG", "可订阅的topic==" + s);
                            if (s.equals(RosTopic.TopicName.get(i))) {
                                topicingList.add(RosTopic.TopicName.get(i));
                                Log.d("CeshiTAG", "已订阅的topic" + RosTopic.TopicName.get(i));
                                rosTopic.TopicMatch[i] = true;
                                switch (i) {
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
                                        rosTopic.initCoveragePointsTopic(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 5:
                                        rosTopic.initCoveragePathTopic(receiveHandler.getCoveragePathTopicHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 6:
                                        rosTopic.subscribeJointVelocityTopic(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 7:
                                        rosTopic.subscribeEmergencyTopic(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 8:
                                        rosTopic.subscribeBatteryTopic(receiveHandler.getBatteryHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 9:
                                        rosTopic.subscribeStatesReplyTopic(receiveHandler.getStatusHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 10:
                                        rosTopic.subscribeStatesRequestTopic(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 11:
                                        rosTopic.subscribeStatesNotificationTopic(receiveHandler.getStatusNotifyHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 12:
                                        rosTopic.subscribeMapPathTopic(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 13:
                                        rosTopic.subscribeControlPathTopic(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 14:
                                        rosTopic.subscribeClampControl(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 15:
                                        rosTopic.subscribeClampHardwareControl(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 16:
                                        rosTopic.subscribeEnterManual(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 17:
                                        rosTopic.subscribeRobotPose(receiveHandler.getRobotPoseHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 18:
                                        rosTopic.subscribeWatchMap(receiveHandler.getWatchMapHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 19:
                                        rosTopic.subscribeNavPace(receiveHandler.getNavPaceHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 20:
                                        rosTopic.advertiseClampLocation(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 21:
                                        rosTopic.subscribeClampLocation(receiveHandler.getClampNotifyHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 22:
                                        rosTopic.subscribeAir(receiveHandler.getAirHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 23:
                                        rosTopic.subscribeVideoFrames(receiveHandler.getImageHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 24:
                                        rosTopic.subscribeVideoFramesPro(receiveHandler.getImageHandlerPro(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 25:
                                        rosTopic.subscribeIndexVideoFrames(receiveHandler.getImageIndexHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 26:
//                                        rosTopic.subscribeIndexVideoFramesPro(receiveHandler.getImageIndexHandlerPro(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 27:
                                        rosTopic.subscribeModalitiesControl(((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 28:
                                        rosTopic.subscribePointCloud(receiveHandler.getPointCloudHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 29:
                                        rosTopic.subscribeProgress(receiveHandler.getProgressHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    case 30:
                                        rosTopic.subscribeGlobalPath(receiveHandler.getGlobalPathHandler(), ((RCApplication) getApplication()).getRosClient());
                                        break;
                                    default:
                                }
                            }
                        }
                    }

                    long end = System.currentTimeMillis();
                    Log.d("CeshiTAG", "订阅耗时" + (end - start));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        initHardware();
    }

    private void rosServiceInit() {
        executorService.submit(() -> {
            if (RosInit.isConnect) {
                try {
                    for (String s : RCApplication.client.getServices()) {
                        if (s.equals(rosService.ServiceName[0])) {
                            rosService.initCoverageMapService(((RCApplication) getApplication()).getRosClient());
                            return;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReplyIPC.BatteryReply event) {
        statusBarView.setBatteryPercent(event.getCapacity_percent());
    }


    private void windowSet() {
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //点击事件
    private void fragmentTrans() {
        //注销登录
        binding.drawerCloseLl.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            finish();
        });
        //系统设置
        binding.systemSetLl.setOnClickListener(v -> navigateTo(R.id.systemSetFragment));
        //定时任务
        binding.timeTaskLl.setOnClickListener(v -> navigateTo(R.id.mapManagerFragment));
        //主页
        binding.locationLl.setOnClickListener(v -> navigateTo(R.id.nav_home));
        //用户管理
        binding.tvUserManager.setOnClickListener(v -> navigateTo(R.id.userFragment));


        //用户权限控制
        if (!UserList.INSTANCE.getArray()[0].equals(RCApplication.Operator)) {
            binding.tvUserManager.setVisibility(View.GONE);
        } else {
            binding.tvUserManager.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 跳转界面
     *
     * @param name 跳转地址
     */
    private void navigateTo(int name) {
        NavController navController = Navigation.findNavController(TaskControlActivity.this, R.id.fragment_control_main);
        navController.navigate(name);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }


    private void backPressedSet() {
        mBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
            @SuppressLint("RestrictedApi")
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(TaskControlActivity.this, R.id.fragment_control_main);
                if (navController.getBackStack().size() <= 2) {
                    finish();
                } else {
                    navController.navigateUp();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, mBackPressedCallback);
    }

    private void initHardware() {
        if (topicingList.size() > 0) {
            executorService.submit(() -> {
                StateMachineRequest stateMachineRequest = new StateMachineRequest();
                //开机初始化全部开关
                stateMachineRequest.hardware_control = 11;
                RosTopic.publishStateMachineRequest(stateMachineRequest);
            });
        }
    }
}