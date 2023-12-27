package com.shciri.rosapp.ui.control;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.base.BaseFragment;
import com.shciri.rosapp.databinding.FragmentChooseTaskBinding;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.StateNotifyHeadEvent;
import com.shciri.rosapp.mydata.DBUtils;
import com.shciri.rosapp.ui.TaskControlActivity;
import com.shciri.rosapp.ui.datamanagement.MapAdapter;
import com.shciri.rosapp.ui.dialog.WaitDialog;
import com.shciri.rosapp.ui.myview.TaskBtView;
import com.shciri.rosapp.utils.ToolsUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import src.com.jilk.ros.message.StateMachineRequest;
import src.com.jilk.ros.message.StateNotificationHeartbeat;
import src.com.jilk.ros.message.requestparam.ManualPathParameter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChooseTaskFragment extends BaseFragment implements View.OnClickListener {

    private View mOpenDrawer;
    private View mHealthDialog;
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
    public List<String> mapNameList;

    private ManageTaskDB manageTaskDB;
    private WaitDialog waitDialog;

    private StateMachineRequest stateMachineRequest = new StateMachineRequest();
    private ManualPathParameter manualPathParameter = new ManualPathParameter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChooseTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        if (currentPathID == 0) {
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
        mapText.setOnClickListener(v -> mapSpinner.performClick());

        startTaskTv = binding.startTaskBt;
        startTaskTv.setOnClickListener(v -> {
            if (!ManageTaskDB.taskLists.isEmpty()) {
                if (ManageTaskDB.currentTaskIndex == 0) {
                    ManageTaskDB.taskLists.get(0).mode = mTaskBt1.getCurrentMode();
                } else if (ManageTaskDB.currentTaskIndex == 1) {
                    ManageTaskDB.taskLists.get(1).mode = mTaskBt2.getCurrentMode();
                } else if (ManageTaskDB.currentTaskIndex == 2) {
                    ManageTaskDB.taskLists.get(2).mode = mTaskBt3.getCurrentMode();
                }
                if (DBUtils.getInstance().getPointS(Integer.toString(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).pathID), true) != null) {
                    stateMachineRequest.navigation_task = 1;
                    RosTopic.publishStateMachineRequest(stateMachineRequest);
                    manualPathParameter.point = DBUtils.getInstance().getPointS(Integer.toString(ManageTaskDB.taskLists.get(ManageTaskDB.currentTaskIndex).pathID), true);
                    manualPathParameter.loop_num = (short) taskCycleTimes;
                    RosTopic.publishManualPathParameterTopic(manualPathParameter);
                    stateMachineRequest.navigation_task = 3;
                    toast(R.string.start_task);
                    RosTopic.publishStateMachineRequest(stateMachineRequest);
                    ToolsUtil.INSTANCE.playRingtone(getActivity());
                    Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskExeFragment);
                }
            }else {
                toast(R.string.no_task);
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
        mHealthDialog.setOnClickListener(v -> {
            HealthDialog healthDialog = new HealthDialog(getActivity());
            healthDialog.setCancelable(false);//是否可以点击DialogView外关闭Dialog
            healthDialog.setCanceledOnTouchOutside(false);//是否可以按返回按钮关闭Dialog
            healthDialog.show();
        });

        binding.taskReportIv.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.taskReportActivity));
        binding.manualControlIv.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_nav_home_to_manuaControlFragment));

        binding.moreTaskLl.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_nav_home_to_moreTaskFragment));

        waitDialog = new WaitDialog.Builder(requireActivity()).setTitle("重定位中").setLoadingDurationMillis(60000).build();
        //点击重定位按钮
        binding.btnRetargeting.setOnClickListener(v -> {
            if (!waitDialog.isShowing()) {
                waitDialog.show();
            }
        });

        initSpinner(getContext());
    }

    private void queryMapList() {
        //查询全部数据
        Cursor cursor = RCApplication.db.query("map", null, null, null, null, null, null);
        MapAdapter.mapLists = new ArrayList<>();
        mapNameList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex("time"));
                @SuppressLint("Range") int width = cursor.getInt(cursor.getColumnIndex("width"));
                @SuppressLint("Range") int height = cursor.getInt(cursor.getColumnIndex("height"));
                mapNameList.add(name);
                MapAdapter.MapList mapList = new MapAdapter.MapList(id, name, time, width, height);
                MapAdapter.mapLists.add(mapList);
            }
        }
        cursor.close();
    }

    private void initSpinner(Context context) {
        queryMapList();
        mapAdapter = new ArrayAdapter<String>(context, R.layout.task_bt_spinner_item_select, mapNameList);
        //设置数组适配器的布局样式
        mapAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        //设置下拉框的数组适配器
        mapSpinner.setAdapter(mapAdapter);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        mapSpinner.setOnItemSelectedListener(new MyMapSpinner());
        if (!MapAdapter.mapLists.isEmpty()) {
            mapText.setText(mapAdapter.getItem(0));
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath() + "/RobotLocalMap" + "/" + mapAdapter.getItem(0) + "_1" + ".png");
            if (bitmap != null && bitmap.getByteCount() > 1) {
                RosData.rosBitmap = bitmap;
            }
            RosData.currentMapID = MapAdapter.mapLists.get(0).id;
        }
    }

    class MyMapSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mapText.setText(mapAdapter.getItem(position));
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath() + "/RobotLocalMap" + "/" + mapAdapter.getItem(position) + "_1" + ".png");
            if (bitmap != null && bitmap.getByteCount() > 1) {
                RosData.rosBitmap = bitmap;
            }
            RosData.currentMapID = MapAdapter.mapLists.get(position).id;
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
        } else if (ManageTaskDB.taskLists.size() == 2) {
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
        mTaskBt1.detailPage.setOnClickListener(this::navToTaskDetailFragment);
        mTaskBt2.detailPage.setOnClickListener(this::navToTaskDetailFragment);
        mTaskBt3.detailPage.setOnClickListener(this::navToTaskDetailFragment);
    }

    /**
     * 设置次数
     */
    private void InitialCirculationTimeView() {
        binding.circulateTv1.setChecked(true);
        taskCycleTimes = 1;
        binding.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.circulateTv4) {
                binding.circulateTv4.setChecked(true);
                taskCycleTimes = 255;
            } else if (i == R.id.circulateTv3) {
                binding.circulateTv3.setChecked(true);
                taskCycleTimes = 3;
            } else if (i == R.id.circulateTv2) {
                binding.circulateTv2.setChecked(true);
                taskCycleTimes = 2;
            } else if (i == R.id.circulateTv1) {
                binding.circulateTv1.setChecked(true);
                taskCycleTimes = 1;
            }
        });
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

    @SuppressLint({"ResourceAsColor", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        Log.d("CeshiTAG", "下标----" + ManageTaskDB.currentTaskIndex);
        System.out.println("taskname = " + currentTaskName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StateNotifyHeadEvent event) {
        StateNotificationHeartbeat stateNotificationHeartbeat = event.getState();
        switch (stateNotificationHeartbeat.slam_state) {
            //定位中
            case 0x01:
                waitDialog.show();
                break;
            //定位成功
            case 0x02:
                //定位失败
            case 0x03:
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }
                toast(R.string.location_success);
                break;
            default:

        }
    }

    private void navToTaskDetailFragment(View view) {
        if (!ManageTaskDB.taskLists.isEmpty()) {
            Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskDetailFragment);
        } else {
            toast(R.string.please_create_new_task);
        }
    }
}