package com.shciri.rosapp.ui.taskshistory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.shciri.rosapp.R;
import com.shciri.rosapp.ui.TaskControlActivity;

public class TasksHistoryReportActivity extends AppCompatActivity {
    ViewPager viewPager;
    TextView tabLeftIv;
    TextView tabMidIv;
    TextView tabRightIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_histroy_report);

        viewPager = findViewById(R.id.report_view_pager_ac);
        TasksHistoryReportPagerAdapter tasksPagerAdapter = new TasksHistoryReportPagerAdapter( getSupportFragmentManager());
        viewPager.setAdapter(tasksPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    selectTabLeft();
                }else if(position == 1){
                    selectTabMid();
                }else{
                    selectTabRight();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TasksHistoryReportActivity.this, TaskControlActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        tabLeftIv =findViewById(R.id.tabLeftIv);
        tabLeftIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTabLeft();
                viewPager.setCurrentItem(0);
            }
        });
        tabMidIv =findViewById(R.id.tabMidIv);
        tabMidIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTabMid();
                viewPager.setCurrentItem(1);
            }
        });
        tabRightIv =findViewById(R.id.tabRightIv);
        tabRightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTabRight();
                viewPager.setCurrentItem(2);
            }
        });

        selectTabLeft();
    }

    private void selectTabLeft(){
        tabLeftIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
        tabMidIv.setBackgroundResource(0);
        tabRightIv.setBackgroundResource(0);
    }

    private void selectTabMid(){
        tabLeftIv.setBackgroundResource(0);
        tabMidIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
        tabRightIv.setBackgroundResource(0);
    }

    private void selectTabRight(){
        tabLeftIv.setBackgroundResource(0);
        tabMidIv.setBackgroundResource(0);
        tabRightIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
    }
}