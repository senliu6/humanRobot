package com.shciri.rosapp.ui.taskshistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.shciri.rosapp.R;
import com.shciri.rosapp.databinding.ActivityTaskHistroyReportBinding;

public class TasksHistoryReportActivity extends Fragment {
    private ActivityTaskHistroyReportBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskHistroyReportBinding.inflate(inflater, container, false);
        initView();
        initData();
        return binding.getRoot();
    }

    private void initData() {

        TasksHistoryReportPagerAdapter tasksPagerAdapter = new TasksHistoryReportPagerAdapter(getParentFragmentManager());
        binding.reportViewPagerAc.setAdapter(tasksPagerAdapter);
        binding.reportViewPagerAc.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    selectTabLeft();
                } else if (position == 1) {
                    selectTabMid();
                } else {
                    selectTabRight();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {

        binding.returnLl.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());


        binding.tabLeftIv.setOnClickListener(v -> {
            selectTabLeft();
            binding.reportViewPagerAc.setCurrentItem(0);
        });

        binding.tabMidIv.setOnClickListener(v -> {
            selectTabMid();
            binding.reportViewPagerAc.setCurrentItem(1);
        });

        binding.tabRightIv.setOnClickListener(v -> {
            selectTabRight();
            binding.reportViewPagerAc.setCurrentItem(2);
        });
        selectTabLeft();

    }

    private void selectTabLeft() {
        binding.tabLeftIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
        binding.tabMidIv.setBackgroundResource(0);
        binding.tabRightIv.setBackgroundResource(0);
    }

    private void selectTabMid() {
        binding.tabLeftIv.setBackgroundResource(0);
        binding.tabMidIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
        binding.tabRightIv.setBackgroundResource(0);
    }

    private void selectTabRight() {
        binding.tabLeftIv.setBackgroundResource(0);
        binding.tabMidIv.setBackgroundResource(0);
        binding.tabRightIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
    }
}