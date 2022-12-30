package com.shciri.rosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.mydata.DBOpenHelper;
import com.shciri.rosapp.mydata.DBUtils;
import com.shciri.rosapp.server.ConnectServer;
import com.shciri.rosapp.server.ServerInfoTab;
import com.shciri.rosapp.ui.TaskControlActivity;
import com.shciri.rosapp.ui.myview.LoginKeyboardView;
import com.shciri.rosapp.ui.myview.StatusBarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import src.com.jilk.ros.rosbridge.ROSBridgeClient;
import android.app.ADWApiManager;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION ="cn.wch.wchusbdriver.USB_PERMISSION";
    private String TAG = MainActivity.class.getSimpleName();

    private EditText passwordEdit;

    private String password;

    private RosInit rosInit;

    private Handler handler;

    private AlertDialog alertDialog;

    private LinearLayout layConnectingLoading;

    private ImageView maskView;

    private StatusBarView statusBarView;

    private Spinner identitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        getBaseContext().deleteDatabase("test");
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this,"test",null,7);
        RCApplication.db = dbOpenHelper.getWritableDatabase();
//        RCApplication.db.delete("map","id=?",new String[]{"1"});
//        ContentValues values = new ContentValues();
//        values.put("name", "DAMon");
//        RCApplication.db.update("map", values, "name = ?", new String[]{"TT"});

        InitUI();

        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver,filter);
    }

    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
                Date date = new Date(System.currentTimeMillis());
                statusBarView.setTimeView(simpleDateFormat.format(date));
            }
        }
    };

    private void InitUI() {
        statusBarView = findViewById(R.id.login_statusBar);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        statusBarView.setTimeView(simpleDateFormat.format(date));
        statusBarView.setBatteryPercent((byte)(RCApplication.localBattery * 100));

        ContentLoadingProgressBar connectingProgressBar = findViewById(R.id.connecting_progress_bar);
        connectingProgressBar.setVisibility(View.VISIBLE);

        layConnectingLoading = findViewById(R.id.lay_connecting_loading);
        maskView = findViewById(R.id.login_mask);
        maskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 拦截点击事件 */
            }
        });

        /* 防止软键盘自动弹出 */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        String[] idOption = {"User","Admin"};
        ArrayAdapter<String> idAdapter = new ArrayAdapter<String>(this, R.layout.task_bt_spinner_item_select, idOption);
        //设置数组适配器的布局样式
        idAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        identitySpinner = findViewById(R.id.identity_select);
        identitySpinner.setAdapter(idAdapter);
        RCApplication.Operator = idOption[0];
        identitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RCApplication.Operator = identitySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        password = new String("");
        LoginKeyboardView loginKeyboardView = findViewById(R.id.loginKeyboard);
        loginKeyboardView.setLoginKeyboardListener(new LoginKeyboardView.LoginKeyboardListener() {
            @Override
            public void KeyInput(int key) {
                if (password.length() >= 12) {
                    Toast.makeText(getApplicationContext(), "密码超出最大长度!", Toast.LENGTH_SHORT).show();
                    return;
                }
                passwordSet(key);
            }
        });

        passwordEdit = findViewById(R.id.password_Edit);

        findViewById(R.id.loginBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals("1")) {
                    serverOperation();
                    if(!RosInit.isConnect) {
                        if(!alertDialog.isShowing()){
                            alertDialog.show();
                        }
                    }else{
                        layConnectingLoading.setVisibility(View.VISIBLE);
                        maskView.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(MainActivity.this, TaskControlActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "密码错误,请重试!", Toast.LENGTH_SHORT).show();
                    password = "";
                    passwordEdit.setText(password);
                }
            }
        });

        handler = new myHandler();

        alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Warning")
                .setMessage("请检查机器人底盘网络，或者进入离线模式？")
                .setIcon(R.mipmap.choosetask_duankailianjie4_21)
                .setCancelable(false)
                .setPositiveButton("进入离线模式", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RosInit.offLineMode = true;
                        layConnectingLoading.setVisibility(View.VISIBLE);
                        maskView.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(MainActivity.this, TaskControlActivity.class);
                        startActivity(intent);
                    }
                })

                .setNegativeButton("重试", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getBaseContext(), "重试中", Toast.LENGTH_SHORT).show();
                    }
                }).create();

        rosInit = new RosInit();
    }

    private void serverOperation() {
        ConnectServer connectServer = new ConnectServer();
        ServerInfoTab.id = DBUtils.getInstance().DBQueryInfo();
        if(ServerInfoTab.id == 0) {
            connectServer.addInfo();
        }else{
            connectServer.queryInfo();
//            connectServer.updateInfo();
        }

    }

    private class myHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.arg1 == 1)
                statusBarView.setConnectStatus(true);
            else
                statusBarView.setConnectStatus(false);
        }
    }

    public void rosConnectAndInit() {
        new Thread(() -> {
            while (true) {
                if(RosInit.isConnect || RosInit.offLineMode) {
                    return;
                }
                ROSBridgeClient client = rosInit.rosConnect(((RCApplication)getApplication()).rosIP,((RCApplication)getApplication()).rosPort);
                ((RCApplication)getApplication()).setRosClient(client);

                Message message = Message.obtain();
                if(RosInit.isConnect || RosInit.offLineMode){
                    message.arg1 = 1;
                    handler.sendMessage(message);
                    return;
                }else{
                    message.arg1 = 0;
                    handler.sendMessage(message);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void passwordSet(int key) {
        switch (key) {
            case 10:
                password = "";
                passwordEdit.setText(password);
                passwordEdit.setSelection(password.length());
                break;

            case 11:
                password += String.valueOf(0);
                passwordEdit.setText(password);
                passwordEdit.setSelection(password.length());
                break;

            case 12:
                if (password.length() >= 1) {
                    password = password.substring(0, password.length() - 1);
                    passwordEdit.setText(password);
                    passwordEdit.setSelection(password.length());
                }
                break;

            default:
                password += String.valueOf(key);
                passwordEdit.setText(password);
                passwordEdit.setSelection(password.length());
                break;
        }

        if (password.length() > 0) {
            findViewById(R.id.loginBt).setBackgroundResource(R.mipmap.login_denglu4_21);
        } else {
            findViewById(R.id.loginBt).setBackgroundResource(R.mipmap.denglu);
        }
    }

    /**
     * 重设 view 的宽高
     */
    public static void setViewLayoutParams(View view, int nWidth, int nHeight) {
        int theW = dip2px(view.getContext(), nWidth);
        int theH = dip2px(view.getContext(), nHeight);

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.height != theH || lp.width != theW) {
            lp.width = theW;
            lp.height = theH;
            view.setLayoutParams(lp);
        }
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestFullscreen();
        if(RosInit.isConnect) {
            RCApplication.client.disconnect();
            RosInit.offLineMode = false;
            RosInit.isConnect = false;
            System.out.println("onDestroy onDestroy onDestroyonDestroyonDestroy onDestroy ");
        }
        layConnectingLoading.setVisibility(View.INVISIBLE);
        maskView.setVisibility(View.INVISIBLE);
        rosConnectAndInit();
        System.out.println("onResume onResume onResume onResume onResume ");
//        setStatusBar();
        passwordEdit.setText("");
        password = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void setStatusBar() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

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
}