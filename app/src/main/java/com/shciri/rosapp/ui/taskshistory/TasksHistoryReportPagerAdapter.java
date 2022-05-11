package com.shciri.rosapp.ui.taskshistory;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class TasksHistoryReportPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = new String[]{"最近24小时","一周内", "最近一个月"};

    public TasksHistoryReportPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("TasksReportPagerAdapter", "getItem: "+ position);
        return TasksHistoryFragment.newInstance(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        return mContext.getResources().getString(TAB_TITLES[position]);
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return 3;
    }
}