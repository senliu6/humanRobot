package com.shciri.rosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.shciri.rosapp.myfragment.HomeFragment;
import com.shciri.rosapp.peripheral.Buzzer;
import com.shciri.rosapp.peripheral.Led;

import java.util.ArrayList;

import src.com.jilk.ros.Service;
import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.Clock;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.Log;
import src.com.jilk.ros.rosapi.message.Empty;
import src.com.jilk.ros.rosapi.message.GetTime;
import src.com.jilk.ros.rosapi.message.MessageDetails;
import src.com.jilk.ros.rosapi.message.Type;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1024;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    //    requestPermission();

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_home, homeFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestFullscreen();
    }

    protected void requestFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE   /* 设置状态栏和导航栏中的图标变小，变模糊或者弱化其效果 */
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE  /* 稳定布局，主要是在全屏和非全屏切换时，布局不要有大的变化。 */
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  /* 粘性沉浸模式 */
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION /* content doesn't resize when the system bars hide and show. */
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                System.out.println("获取存储权限失败");
            }
        }
    }

}