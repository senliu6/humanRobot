package com.shciri.rosapp.ui.control;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.shciri.rosapp.R;
import com.shciri.rosapp.databinding.FragmentChooseTaskBinding;
import com.shciri.rosapp.ui.TaskControlActivity;
import com.shciri.rosapp.ui.myview.TaskBtView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChooseTaskFragment extends Fragment implements View.OnClickListener {

    private View mOpenDrawer;
    private View mHealthDialog;
    private TextView mCirculateTv1, mCirculateTv2, mCirculateTv3, mCirculateTv4;
    private TaskBtView mTaskBt1, mTaskBt2, mTaskBt3;
    private FragmentChooseTaskBinding binding;
    private OnBackPressedCallback mBackPressedCallback;
    private TextView startTaskTv;

    public static int currentTaskID;
    public static int taskCycleTimes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChooseTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onResume() {
        Log.v("ChooseTaskFragment", "onResume");
        if(currentTaskID == 0){
            mTaskBt1.requestFocus();
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startTaskTv = binding.startTaskBt;
        startTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskDetailFragment);
            }
        });

        mOpenDrawer = binding.openDrawerIv;

        // Set up the user interaction to manually show or hide the system UI.
        mOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TaskControlActivity) getActivity()).openFrawerLayout();
            }
        });
        InitialCirculationTimeView();

        InitialTaskView();

        mHealthDialog = binding.healthIv;
        mHealthDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthDialog healthDialog = new HealthDialog(getActivity());
                healthDialog.setCancelable(false);//是否可以点击DialogView外关闭Dialog
                healthDialog.setCanceledOnTouchOutside(false);//是否可以按返回按钮关闭Dialog
                healthDialog.show();
            }
        });

        binding.taskReportIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskReportActivity);
            }
        });
        binding.manualControlIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_manuaControlFragment);
            }
        });

        binding.moreTaskLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_addTaskFragment);
            }
        });
    }

    private void InitialTaskView() {
        mTaskBt1 = binding.taskBt1;
        mTaskBt2 = binding.taskBt2;
        mTaskBt3 = binding.taskBt3;
        mTaskBt1.setOnClickListener(this);
        mTaskBt2.setOnClickListener(this);
        mTaskBt3.setOnClickListener(this);
    }

    void setNoBackgroundCirculateTv() {
        mCirculateTv1.setBackgroundResource(0);
        mCirculateTv2.setBackgroundResource(0);
        mCirculateTv3.setBackgroundResource(0);
        mCirculateTv4.setBackgroundResource(0);
    }

    private void InitialCirculationTimeView() {
        mCirculateTv1 = binding.circulateTv1;
        mCirculateTv2 = binding.circulateTv2;
        mCirculateTv3 = binding.circulateTv3;
        mCirculateTv4 = binding.circulateTv4;
        mCirculateTv1.setOnClickListener(this);
        mCirculateTv2.setOnClickListener(this);
        mCirculateTv3.setOnClickListener(this);
        mCirculateTv4.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circulateTv1:
                setNoBackgroundCirculateTv();
                mCirculateTv1.setBackgroundColor(R.color.red);
                taskCycleTimes = 1;
                break;
            case R.id.circulateTv2:
                setNoBackgroundCirculateTv();
                mCirculateTv2.setBackgroundColor(R.color.red);
                taskCycleTimes = 2;
                break;
            case R.id.circulateTv3:
                setNoBackgroundCirculateTv();
                mCirculateTv3.setBackgroundColor(R.color.red);
                taskCycleTimes = 3;
                break;
            case R.id.circulateTv4:
                setNoBackgroundCirculateTv();
                mCirculateTv4.setBackgroundColor(R.color.red);
                taskCycleTimes = 255;
                break;
            case R.id.task_bt1:
                currentTaskID = 1;
                break;
            case R.id.task_bt2:
                currentTaskID = 2;
                break;
            case R.id.task_bt3:
                currentTaskID = 3;
                break;
            default:
                break;
        }
    }
}