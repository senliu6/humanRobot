package com.shciri.rosapp.ui.addtask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.base.BaseFragment;
import com.shciri.rosapp.databinding.FragmentAddNewTimeTaskBinding;
import com.shciri.rosapp.dmros.data.WeekString;
import com.shciri.rosapp.server.AlarmService;
import com.shciri.rosapp.ui.control.ManageTaskDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AddNewTimeTaskFragment extends BaseFragment {

    private TimePickerDialog timePickerDialog;
    private int year, monthOfYear, dayOfMonth, hourStart, minStart, hourEnd, minEnd;

    private FragmentAddNewTimeTaskBinding binding;
    //是否结束时间弹窗
    private boolean endTime;
    private Intent alarmIntent;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public AddNewTimeTaskFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewTimeTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alarmIntent = new Intent(getActivity(), AlarmService.class);
        binding.returnLl.setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());
        binding.addTimeTaskCancelBtn.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        String[] loopOption = {"1", "2", "3", "5", "10"};
        ArrayAdapter<String> loopAdapter = new ArrayAdapter<>(getContext(), R.layout.task_bt_spinner_item_select, loopOption);
        //设置数组适配器的布局样式
        loopAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        binding.addTimeTaskLoopSpinner.setAdapter(loopAdapter);

        // 通过Calendar对象来获取年、月、日、时、分的信息
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hourStart = calendar.get(Calendar.HOUR_OF_DAY);
        minStart = calendar.get(Calendar.MINUTE);
        hourEnd = calendar.get(Calendar.HOUR_OF_DAY);
        minEnd = calendar.get(Calendar.MINUTE);


        timePickerDialog = new TimePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, (view12, hourOfDay, minute) -> {
            if (endTime) {
                hourEnd = hourOfDay;
                minEnd = minute;
                if (hourStart > hourEnd) {
                    Toaster.showLong(R.string.end_time_tips);
                } else {
                    if (hourStart == hourEnd && minStart >= minEnd) {
                        Toaster.showLong(getString(R.string.end_time_tips));
                    } else {
                        binding.addTimeTaskEndTimeSelect.setText((String) (" " + hourOfDay + ":" + minute));
                    }
                }
            } else {
                hourStart = hourOfDay;
                minStart = minute;
                binding.addTimeTaskTimeSelect.setText((String) (" " + hourOfDay + ":" + minute));
            }
        }, hourStart, minStart, true);

        binding.addTimeTaskTimeSelect.setText((String) (" " + String.format("%02d", hourStart) + ":" + String.format("%02d", minStart)));

        //开始时间点击事件
        binding.addTimeTaskTimeSelect.setOnClickListener(v -> {
            endTime = false;
            timePickerDialog.show();
        });
        //结束时间点击事件
        binding.addTimeTaskEndTimeSelect.setOnClickListener(v -> {
            endTime = true;
            timePickerDialog.show();
        });


        String[] dateOption = {getString(R.string.every_day), getString(R.string.one_to_five), getString(R.string.weekend), getString(R.string.on_three), getString(R.string.two_four)};
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(getContext(), R.layout.task_bt_spinner_item_select, dateOption);
        //设置数组适配器的布局样式
        dateAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        binding.addTimeTaskDateSpinner.setAdapter(dateAdapter);

        ArrayAdapter<String> taskItemListArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.task_bt_spinner_item_select, ManageTaskDB.taskNameList);
        taskItemListArrayAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        binding.addOriginTaskMapSpinner.setAdapter(taskItemListArrayAdapter);

        String[] modeOption = {getString(R.string.disinfection_air), getString(R.string.disinfection), getString(R.string.air), getString(R.string.empty_run)};
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(getContext(), R.layout.task_bt_spinner_item_select, modeOption);
        //设置数组适配器的布局样式
        modeAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        binding.addTimeTaskModeSpinner.setAdapter(modeAdapter);

        binding.addTimeTaskConfirmBtn.setOnClickListener(v -> {
            if (binding.addOriginTaskMapSpinner.getSelectedItem() != null) {
                DialogBuilder(view);
            } else {
                toast(R.string.no_task);
            }
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 1);
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio40/value", 1);
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio41/value", 1);
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio42/value", 1);
        });

    }

    private void DialogBuilder(View view) {
//        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(getContext());
//        final String items[] = new String[]{getString(R.string.air), getString(R.string.disinfection_air)};
//        builder.setTitle(getString(R.string.disinfection_air))
//                .addItems(items, (dialog, which) -> {
//
//                }).setCheckedItems(new int[]{0})
//                .addAction("取消", (dialog, index) -> dialog.dismiss())
//                .addAction("确定", (dialog, index) -> {
//                    int keyID = DBUtils.getInstance().DBInsertTimeTask(
//                            binding.addOriginTaskMapSpinner.getSelectedItem().toString(),
//                            ManageTaskDB.taskLists.get(ManageTaskDB.taskNameList.indexOf(binding.addOriginTaskMapSpinner.getSelectedItem().toString())).ID,
//                            String.format("%02d", hourStart) + ":" + String.format("%02d", minStart),
//                            binding.addTimeTaskDateSpinner.getSelectedItem().toString(),
//                            RosData.currentMapID,
//                            binding.addTimeTaskLoopSpinner.getSelectedItem().toString(),
//                            binding.addTimeTaskModeSpinner.getSelectedItem().toString());
//
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.set(Calendar.HOUR_OF_DAY, hourStart);
//                    calendar.set(Calendar.MINUTE, minStart);
//                    calendar.set(Calendar.SECOND, 0);
////                        AlarmManagerUtils.getInstance(null).createPeriodAlarmManager(calendar, keyID, builder.getCheckedItemRecord().get(0), builder.getCheckedItemRecord().get(1));
//                    Log.d("Alarm", "fan_switch = " + builder.getCheckedItemRecord().get(0) + "   led_switch = " + builder.getCheckedItemRecord().get(1));
//
//                    alarmIntent.putExtra("taskId", String.valueOf(keyID));
//                    alarmIntent.putExtra("alarmTime", dateFormat.format(calendar.getTime()));
//                    alarmIntent.putExtra("week", getWeekSet(binding.addTimeTaskDateSpinner.getSelectedItemPosition()));
//                    alarmIntent.putExtra("loopNum", Short.valueOf(binding.addTimeTaskLoopSpinner.getSelectedItem().toString()));
//                    requireActivity().startService(alarmIntent);
//                    dialog.dismiss();
//                    Navigation.findNavController(view).navigateUp();
//                })
//                .show();
    }

    private String getWeekSet(int weekID) {
        switch (weekID) {
            case 0:
                return WeekString.EVERY_DAY;
            case 1:
                return WeekString.WORK_DAY;
            case 2:
                return WeekString.WEEK_DAY;
            case 3:
                return WeekString.ONE_THREE;
            case 4:
                return WeekString.TWO_FOUR;
            default:
        }
        return WeekString.EVERY_DAY;
    }
}
