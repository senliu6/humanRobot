package com.shciri.rosapp.ui.taskexe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.server.ConnectServer;
import com.shciri.rosapp.server.ServerInfoTab;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TaskExeFragment extends Fragment {

    private ViewPager viewPager;
    TextView tabLeftIv;
    TextView tabRightIv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task_exe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RosData.taskPercent = 0;

//        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).navigateUp();
//            }
//        });
        viewPager = view.findViewById(R.id.task_exe_view_pager);
        TasksExePagerAdapter tasksDetailAdapter = new TasksExePagerAdapter( getChildFragmentManager());
        viewPager.setAdapter(tasksDetailAdapter);
        tabLeftIv = view.findViewById(R.id.tabLeftIv);
        tabLeftIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTabLeft();
                viewPager.setCurrentItem(0);
            }
        });
        tabRightIv = view.findViewById(R.id.tabRightIv);
        tabRightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTabRight();
                viewPager.setCurrentItem(1);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    selectTabLeft();
                }else{
                    selectTabRight();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        selectTabLeft();
    }

    private void selectTabLeft(){
        tabRightIv.setBackgroundResource(0);
        tabLeftIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
    }

    private void selectTabRight(){
        tabLeftIv.setBackgroundResource(0);
        tabRightIv.setBackgroundResource(R.mipmap.choosetask_kaishianniu4_21);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e("TaskExe", "stop");
    }
}