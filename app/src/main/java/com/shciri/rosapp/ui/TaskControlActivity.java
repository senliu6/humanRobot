package com.shciri.rosapp.ui;

import static com.shciri.rosapp.R.id.tasks_view;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.shciri.rosapp.MyPGM;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RosInit;
import com.shciri.rosapp.data.RosData;

public class TaskControlActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private OnBackPressedCallback mBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_control);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        findViewById(R.id.drawer_close_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                finish();
            }
        });

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

        if(RosInit.isConnect) {
            MyPGM pgm = new MyPGM();
            int[] pix;
            pix = pgm.readData(RosData.map.info.width, RosData.map.info.height, 5, RosData.map.data, RosData.MapData.poseX, RosData.MapData.poseY);   //P5-Gray image
            Bitmap bitmap = Bitmap.createBitmap(RosData.map.info.width, RosData.map.info.height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pix, 0, RosData.map.info.width, 0, 0, RosData.map.info.width, RosData.map.info.height);
            Matrix invert = new Matrix();
            invert.setScale(1, -1); //镜像翻转以与真实地图对应
            RosData.rosBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), invert ,true);
        }
    }

    public void openFrawerLayout() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }
}