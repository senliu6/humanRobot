package com.shciri.rosapp.ui.taskshistory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.shciri.rosapp.R;
import com.shciri.rosapp.ui.TaskControlActivity;

public class TasksHistoryReportActivity extends AppCompatActivity {
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_histroy_report);

        viewPager = findViewById(R.id.report_view_pager_ac);
        TasksHistoryReportPagerAdapter tasksPagerAdapter = new TasksHistoryReportPagerAdapter( getSupportFragmentManager());
        viewPager.setAdapter(tasksPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);;
        tabs.setupWithViewPager(viewPager);
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
    }
}