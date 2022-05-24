package com.shciri.rosapp.ui;

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

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.ReceiveHandler;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.MyPGM;

public class TaskControlActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private OnBackPressedCallback mBackPressedCallback;
    private RosTopic rosTopic;
    private ReceiveHandler receiveHandler;

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

        windowSet();

        fragmentTrans();

        backPressedSet();

        RosData.RosDataInit();

        rosSubscribe();
    }

    public void openDrawerLayout() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void rosSubscribe() {
        if(RosInit.isConnect) {
            receiveHandler = new ReceiveHandler();
            rosTopic = new RosTopic();
            rosTopic.subscribeMapTopic(receiveHandler.getMapTopicHandler(), ((RCApplication) getApplication()).getRosClient());
            rosTopic.subscribeCmdVelTopic(receiveHandler.getCmdValTopicHandler(), ((RCApplication) getApplication()).getRosClient());
            rosTopic.subscribeTFTopic(receiveHandler.getTFTopicHandler(), ((RCApplication) getApplication()).getRosClient());
        }
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