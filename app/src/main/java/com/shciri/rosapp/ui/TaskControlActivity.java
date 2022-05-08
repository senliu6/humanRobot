package com.shciri.rosapp.ui;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.shciri.rosapp.R;

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
        findViewById(R.id.drawer_close_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
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

    public void openFrawerLayout() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

}