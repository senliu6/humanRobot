package com.shciri.rosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.shciri.rosapp.ui.TaskControlActivity;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.loginBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, TaskControlActivity.class);
                startActivity(intent);
            }
        });
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
}