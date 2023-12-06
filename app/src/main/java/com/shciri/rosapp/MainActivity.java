package com.shciri.rosapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.base.BaseDialog;
import com.shciri.rosapp.databinding.ActivityLoginBinding;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.Settings;
import com.shciri.rosapp.dmros.tool.AudioMngHelper;
import com.shciri.rosapp.dmros.tool.UserRepository;
import com.shciri.rosapp.mydata.DBOpenHelper;
import com.shciri.rosapp.mydata.DBUtils;
import com.shciri.rosapp.server.ConnectServer;
import com.shciri.rosapp.server.ServerInfoTab;
import com.shciri.rosapp.ui.TaskControlActivity;
import com.shciri.rosapp.ui.dialog.InputDialog;
import com.shciri.rosapp.ui.dialog.WaitDialog;
import com.shciri.rosapp.utils.SharedPreferencesUtil;
import com.shciri.rosapp.utils.ToolsUtil;
import com.shciri.rosapp.utils.protocol.ReplyIPC;
import com.shciri.rosapp.utils.protocol.RequestIPC;
import com.shciri.rosapp.utils.regex.LimitInputTextWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import src.com.jilk.ros.message.custom.Battery;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;


/**
 * @author asus
 */
public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";
    private String TAG = MainActivity.class.getSimpleName();

    private String password = "";
    private String passwordDef = "1";

    private RosInit rosInit = RosInit.getInstance();

    private Handler handler;

    private BaseDialog intentDialog;


    private AudioMngHelper audioMngHelper;

    // 广播监测USB的插入与拔出
    private BroadcastReceiver UsbReceiver = null;
    private IntentFilter filter = null;
    // 获取当前usb连接状态
    private boolean isUSBConnected = false;

    private ActivityLoginBinding binding;

    private WaitDialog waitDialog;

    private ExecutorService executorService = RCApplication.getExecutorService();

    private InputDialog inputDialog;
    private String robotIp = "11.11.11.111";
    private String[] idOption;
    private ArrayAdapter<String> idAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //如果修改db的表内容，则需要在此提高db的version
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this, "test", null, 9);
        RCApplication.db = dbOpenHelper.getWritableDatabase();
//        RCApplication.db.delete("map","id=?",new String[]{"1"});
//        ContentValues values = new ContentValues();
//        values.put("name", "DAMon");
//        RCApplication.db.update("map", values, "name = ?", new String[]{"TT"});

        initView();
        initData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, filter);

        EventBus.getDefault().register(this);

        //热插拔，如果有的话就去回调下边的代码，监听在哪里就在哪里回调
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        RCApplication.uartVCP.InitUartVCP(manager);

        // 广播监听热插拔
        UsbBroadcastReceiver();


        byte[] data = RequestIPC.batteryRequest();
        RCApplication.uartVCP.sendData(data);
        byte[] response = new byte[100];
        int len = RCApplication.uartVCP.readData(response);
        RCApplication.replyIPC.ipc_put_rx_byte(response, len);
    }

    private void initData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        binding.loginStatusBar.setTimeView(simpleDateFormat.format(date));
        binding.loginStatusBar.setBatteryPercent((byte) (RCApplication.localBattery * 100));

        robotIp = SharedPreferencesUtil.Companion.getValue(this.getApplicationContext(),
                Settings.ROBOT_IP, "11.11.11.111", String.class);

        idOption = UserRepository.INSTANCE.getUserNameArray();
        idAdapter = new ArrayAdapter<String>(this, R.layout.task_bt_spinner_item_select, idOption);
        //设置数组适配器的布局样式
        idAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        binding.identitySelect.setAdapter(idAdapter);
        RCApplication.Operator = idOption[0];

        audioMngHelper = new AudioMngHelper(this);
        waitDialog = new WaitDialog.Builder(this)
                .setLoadingText(getString(R.string.loading))
                .setCancelText(getResources().getString(R.string.cancel))
                .build();

        handler = new myHandler();
        intentDialog = new BaseDialog.Builder(this)
                .setTitle("Warning")
                .setContent(getString(R.string.please_check_intent))
                .setCancelText(getString(R.string.retry))
                .setConfirmText(getString(R.string.enter_offline_mode))
                .setCanceledOnTouchOutside(false)
                .setOnCancelClick(view -> {
                    Toaster.showShort(R.string.retrying);
                    executorService.submit(() -> {
                        ROSBridgeClient client = rosInit.rosConnect(robotIp, RCApplication.rosPort);
                        ((RCApplication) getApplication()).setRosClient(client);
                    });

                })
                .setOnConfirmClick(view -> {
                    waitDialog.show();
                    RosInit.offLineMode = true;
                    Intent intent = new Intent(MainActivity.this, TaskControlActivity.class);
                    startActivity(intent);
                })
                .build();
        rosInit.setOnRosConnectListener(connected -> {
            runOnUiThread(() -> binding.loginStatusBar.setConnectStatus(connected));
        });

    }

    // 广播监听热插拔
    private void UsbBroadcastReceiver() {
        if (UsbReceiver == null) {
            UsbReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(ACTION_USB_PERMISSION)) {
                        boolean connected = intent.getExtras().getBoolean("connected");
                        if (connected) {
                            if (!isUSBConnected) {
                                isUSBConnected = true;
                                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                                RCApplication.uartVCP.InitUartVCP(manager);
                                Toaster.showShort("已经连接USB");
                            }
                        } else {
                            if (isUSBConnected) {
                                isUSBConnected = false;
                                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                                RCApplication.uartVCP.InitUartVCP(manager);
                                Toaster.showShort("已经断开USB");
                            }
                        }
                    }
                }
            };
        }
        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction("android.hardware.usb.action.USB_STATE");
            // 注册广播
            registerReceiver(UsbReceiver, filter);
        }
    }

    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
                Date date = new Date(System.currentTimeMillis());
                binding.loginStatusBar.setTimeView(simpleDateFormat.format(date));

                byte[] data = RequestIPC.batteryRequest();
                RCApplication.uartVCP.sendData(data);
                byte[] response = new byte[100];
                int len = RCApplication.uartVCP.readData(response);
                RCApplication.replyIPC.ipc_put_rx_byte(response, len);
            }
        }
    };

    private void initView() {
        /* 防止软键盘自动弹出 */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        binding.identitySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RCApplication.Operator = binding.identitySelect.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.loginKeyboard.setLoginKeyboardListener(result -> {
            if (result.length() >= binding.loginKeyboard.getLimit()) {
                Toaster.show(R.string.password_outIndex);
            }
            password = result;
            setLoginBackGround();
            binding.passwordEdit.setSelection(result.length());

        });

        binding.buttonLogin.setOnClickListener(v -> {
//                audioMngHelper.setVoice100(100);
//                RCApplication.mediaPlayer.start();
            binding.loginStatusBar.setConnectStatus(RosInit.isConnect);
            if (UserRepository.INSTANCE.isValidUser(RCApplication.Operator, password)) {
                if (!RosInit.isConnect) {
                    if (!intentDialog.isShowing()) {
                        intentDialog.show();
                    }
                } else {
                    waitDialog.show();
                    Intent intent = new Intent(MainActivity.this, TaskControlActivity.class);
                    startActivity(intent);
                }
            } else {
                Toaster.showLong(R.string.password_error);
                password = "";
                binding.loginKeyboard.clearResult();
                setLoginBackGround();
            }
        });
//        serverOperation();

        inputDialog = new InputDialog.Builder(this)
                .setTitle(getString(R.string.please_input_ip))
                .setOnCancelClick(v -> inputDialog.dismiss())
                .setOnConfirmClick(inputText -> {
                    if (inputText.matches(LimitInputTextWatcher.REGEX_IP)) {
                        DBUtils.getInstance().DBUpdateInfo(ServerInfoTab.id, inputText);
                        SharedPreferencesUtil.Companion.saveValue(this.getApplicationContext(), Settings.ROBOT_IP, inputText);
                        Toaster.showShort(getString(R.string.ip_set_success) + inputText);
                        robotIp = inputText;
                        binding.tvIp.setText(String.format(getResources().getString(R.string.robot_id), inputText));
                        inputDialog.dismiss();
                    } else {
                        Toaster.showShort(R.string.input_format);
                    }

                })
                .build();
        binding.tvIp.setOnClickListener(v -> inputDialog.show());
    }

    private void serverOperation() {
        ConnectServer connectServer = new ConnectServer();
        ServerInfoTab.id = DBUtils.getInstance().DBQueryInfo();
        if (ServerInfoTab.id == 0) {
            connectServer.addInfo();
        } else {
            connectServer.queryInfo();
//            connectServer.updateInfo();
        }

    }

    private class myHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.loginStatusBar.setConnectStatus(msg.arg1 == 1);
        }
    }

    public void rosConnectAndInit() {
        executorService.execute(() -> {
            while (true) {
                if (RosInit.isConnect || RosInit.offLineMode) {
                    return;
                }
                ROSBridgeClient client = rosInit.rosConnect(robotIp, RCApplication.rosPort);
                ((RCApplication) getApplication()).setRosClient(client);

                Message message = Message.obtain();
                if (RosInit.isConnect || RosInit.offLineMode) {
                    message.arg1 = 1;
                    handler.sendMessage(message);
                    return;
                } else {
                    message.arg1 = 0;
                    handler.sendMessage(message);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        runOnUiThread(() -> binding.tvIp.setText(String.format(getResources().getString(R.string.robot_id), robotIp)));
    }

    /**
     * 重设 view 的宽高
     */
    public static void setViewLayoutParams(View view, int nWidth, int nHeight) {
        int theW = ToolsUtil.INSTANCE.dip2px(view.getContext().getApplicationContext(), nWidth);
        int theH = ToolsUtil.INSTANCE.dip2px(view.getContext().getApplicationContext(), nHeight);

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.height != theH || lp.width != theW) {
            lp.width = theW;
            lp.height = theH;
            view.setLayoutParams(lp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestFullscreen();
        if (RosInit.isConnect) {
//            RCApplication.client.disconnect();
            RosInit.isConnect = false;
            System.out.println("onDestroy onDestroy onDestroyDestroyDestroy onDestroy ");
        }
        RosInit.offLineMode = false;
        rosConnectAndInit();
        System.out.println("onResume onResume onResume onResume onResume ");
//        setStatusBar();
        password = "";
        binding.loginKeyboard.clearResult();
        setLoginBackGround();
        if (idOption != null) {
            idOption = UserRepository.INSTANCE.getUserNameArray();

        }
        if (idAdapter != null) {
            idAdapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁广播
        if (UsbReceiver != null) {
            unregisterReceiver(UsbReceiver);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (waitDialog != null) {
            waitDialog.dismiss();
        }
    }

    protected void setStatusBar() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ReplyIPC.BatteryReply event) {
        binding.loginStatusBar.setBatteryPercent(event.getCapacity_percent());
        Battery battery = new Battery();
        battery.battery_percent = (byte) event.getCapacity_percent();
        battery.current = (short) event.getCurrent();
        if (null != RosTopic.batteryTopic) {
            RosTopic.batteryTopic.publish(battery);
        }
    }

    ;

    private int getStatusBarHeight(Context baseContext) {
        return 0;
    }

    protected void requestFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE  /* 稳定布局，主要是在全屏和非全屏切换时，布局不要有大的变化。 */
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  /* 粘性沉浸模式 */
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION /* content doesn't resize when the system bars hide and show. */
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //设置登录按钮的背景
    @SuppressLint("ResourceType")
    private void setLoginBackGround() {
        binding.passwordEdit.setText(password);
        if (TextUtils.isEmpty(password)) {
            binding.buttonLogin.getShapeDrawableBuilder()
                    .setSolidColor(Color.parseColor(getResources().getString(R.color.f9fcfc_33))).intoBackground();
            binding.buttonLogin.setTextColor(getResources().getColor(R.color.gray_02a496));
        } else {
            binding.buttonLogin.getShapeDrawableBuilder()
                    .setSolidColor(Color.parseColor(getResources().getString(R.color.blue_00a5b7)))
                    .setSolidGradientColors(ToolsUtil.INSTANCE.intToColorInt(getApplicationContext(), R.color.white_00fdfa), ToolsUtil.INSTANCE.intToColorInt(getApplicationContext(), R.color.gray_00899c))
                    .setSolidGradientOrientation(270).intoBackground();
            binding.buttonLogin.setTextColor(Color.WHITE);
        }
    }
}