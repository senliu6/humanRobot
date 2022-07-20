package com.shciri.rosapp.ui.control;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.databinding.FragmentChooseTaskBinding;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.ControlMapEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.TaskControlActivity;
import com.shciri.rosapp.ui.myview.MapView;
import com.shciri.rosapp.ui.myview.TaskBtView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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

    public static int currentPathID;
    public static int taskCycleTimes;
    public static String currentTaskName;

    private Spinner mapSpinner;
    public static ArrayAdapter<String> mapAdapter;
    private TextView mapText;
    public List<String> mapList;
    public List<Integer> mapID;

    private ManageTaskDB manageTaskDB;

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
        if(currentPathID == 0){
            mTaskBt1.requestFocus();
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitialTaskView(view);

        mapSpinner = binding.chooseTaskMapSpinner;
        mapText = binding.chooseTaskMapNameTv;
        mapText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSpinner.performClick();
            }
        });
        mapList = new ArrayList<String>();
        mapID = new ArrayList<Integer>();

        startTaskTv = binding.startTaskBt;
        startTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ManageTaskDB.currentTaskIndex == 0)
                    ManageTaskDB.taskLists.get(0).mode = mTaskBt1.getCurrentMode();
                else if(ManageTaskDB.currentTaskIndex == 1)
                    ManageTaskDB.taskLists.get(1).mode = mTaskBt2.getCurrentMode();
                else if(ManageTaskDB.currentTaskIndex == 2)
                    ManageTaskDB.taskLists.get(2).mode = mTaskBt3.getCurrentMode();
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskExeFragment);
            }
        });

        mOpenDrawer = binding.openDrawerIv;
        // Set up the user interaction to manually show or hide the system UI.
        mOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TaskControlActivity) getActivity()).openDrawerLayout();
            }
        });
        InitialCirculationTimeView();

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
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_moreTaskFragment);
            }
        });

        initSpinner(getContext());
    }

    private void queryMapList() {
        //查询全部数据
        Cursor cursor = RCApplication.db.query("map",new String[]{"name","id"},null, null, null, null, null);

        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") Integer id = cursor.getInt(cursor.getColumnIndex("id"));
                mapList.add(name);
                mapID.add(id);
            }
        }
        cursor.close();
    }

    private void initSpinner(Context context) {
        queryMapList();
        mapAdapter = new ArrayAdapter<String>(context, R.layout.task_bt_spinner_item_select, mapList);
        //设置数组适配器的布局样式
        mapAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        //设置下拉框的数组适配器
        mapSpinner.setAdapter(mapAdapter);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        mapSpinner.setOnItemSelectedListener(new MyMapSpinner());
    }

    class MyMapSpinner implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mapText.setText(mapAdapter.getItem(position));
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath()+
                    "/RobotLocalMap"+
                    "/"+mapAdapter.getItem(position)+"_1"+".png");
            RosData.rosBitmap = bitmap;
            RosData.currentMapID = mapID.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void InitialTaskView(View view) {
        mTaskBt1 = binding.taskBt1;
        mTaskBt2 = binding.taskBt2;
        mTaskBt3 = binding.taskBt3;
        manageTaskDB = new ManageTaskDB();
        manageTaskDB.queryTask();
        if (ManageTaskDB.taskLists.size() == 1) {
            mTaskBt1.setTitleTv(ManageTaskDB.taskLists.get(0).taskName);
            mTaskBt2.setTitleTv("空");
            mTaskBt3.setTitleTv("空");
            mTaskBt1.setOnClickListener(this);
            ManageTaskDB.currentTaskIndex = 0;
        }
        else if (ManageTaskDB.taskLists.size() == 2) {
            mTaskBt1.setTitleTv(ManageTaskDB.taskLists.get(0).taskName);
            mTaskBt2.setTitleTv(ManageTaskDB.taskLists.get(1).taskName);
            mTaskBt3.setTitleTv("空");
            mTaskBt1.setOnClickListener(this);
            mTaskBt2.setOnClickListener(this);
            ManageTaskDB.currentTaskIndex = 0;
        } else if (ManageTaskDB.taskLists.size() >= 3) {
            mTaskBt1.setTitleTv(ManageTaskDB.taskLists.get(0).taskName);
            mTaskBt2.setTitleTv(ManageTaskDB.taskLists.get(1).taskName);
            mTaskBt3.setTitleTv(ManageTaskDB.taskLists.get(2).taskName);
            mTaskBt1.setOnClickListener(this);
            mTaskBt2.setOnClickListener(this);
            mTaskBt3.setOnClickListener(this);
            ManageTaskDB.currentTaskIndex = 0;
        }
        mTaskBt1.detailPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskDetailFragment);
            }
        });

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
                ManageTaskDB.currentTaskIndex = 0;
                break;
            case R.id.task_bt2:
                ManageTaskDB.currentTaskIndex = 1;
                break;
            case R.id.task_bt3:
                ManageTaskDB.currentTaskIndex = 2;
                break;
            default:
                break;
        }
        System.out.println("taskname = " + currentTaskName);
    }
}