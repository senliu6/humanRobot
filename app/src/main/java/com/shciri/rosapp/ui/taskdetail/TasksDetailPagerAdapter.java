package com.shciri.rosapp.ui.taskdetail;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class TasksDetailPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = new String[]{"工作视图","地图视图"};

    public TasksDetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TaskDetailWorkViewFragment();
            case 1:
                return new TaskDetailMapViewFragment();
            default:
                return new Fragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}