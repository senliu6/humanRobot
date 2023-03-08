package com.shciri.rosapp.ui.addtask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.mydata.DBUtils;
import com.shciri.rosapp.ui.control.ChooseTaskFragment;
import com.shciri.rosapp.ui.control.ManageTaskDB;
import com.shciri.rosapp.utils.AlarmManagerUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AddNewTimeTaskFragment extends Fragment {

    private TimePickerDialog timePickerDialog;
    private int year, monthOfYear, dayOfMonth, hourOfD, min;

    private Spinner loopSpinner;
    private Spinner dateSpinner;
    private Spinner originTaskSpinner;
    private Spinner modeSpinner;
    private TextView timeView;
    private TextView confirmBtn;

    private String date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_new_time_task, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        view.findViewById(R.id.add_time_task_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        String[] loopOption = {"1","2","3","5","10"};
        ArrayAdapter<String> loopAdapter = new ArrayAdapter<String>(getContext(), R.layout.task_bt_spinner_item_select, loopOption);
        //设置数组适配器的布局样式
        loopAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        loopSpinner = view.findViewById(R.id.add_time_task_loop_spinner);
        loopSpinner.setAdapter(loopAdapter);

        // 通过Calendar对象来获取年、月、日、时、分的信息
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hourOfD = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeView.setText((String)(" "+hourOfDay+":"+minute));
                hourOfD = hourOfDay;
                min = minute;
            }
        }, hourOfD, min, true);

        timeView = view.findViewById(R.id.add_time_task_time_select);
        timeView.setText((String)(" "+String.format("%02d", hourOfD)+":"+String.format("%02d", min)));
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });


        String[] dateOption = {"每天","周一到周五","周末","一、三、五","自定义"};
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(getContext(), R.layout.task_bt_spinner_item_select, dateOption);
        //设置数组适配器的布局样式
        dateAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        dateSpinner = view.findViewById(R.id.add_time_task_date_spinner);
        dateSpinner.setAdapter(dateAdapter);

        originTaskSpinner = view.findViewById(R.id.add_origin_task_map_spinner);
        ArrayAdapter<String> taskItemListArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.task_bt_spinner_item_select, ManageTaskDB.taskNameList);
        taskItemListArrayAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        originTaskSpinner.setAdapter(taskItemListArrayAdapter);

        String[] modeOption = {"消杀与空气进化","仅消杀","仅空气净化","空跑"};
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getContext(), R.layout.task_bt_spinner_item_select, modeOption);
        //设置数组适配器的布局样式
        modeAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        modeSpinner = view.findViewById(R.id.add_time_task_mode_spinner);
        modeSpinner.setAdapter(modeAdapter);

        confirmBtn = view.findViewById(R.id.add_time_task_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBuilder(view);
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 1);
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio40/value", 1);
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio41/value", 1);
//                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio42/value", 1);
            }
        });
    }

    private void DialogBuilder(View view) {
         final QMUIDialog.MultiCheckableDialogBuilder  builder = new QMUIDialog.MultiCheckableDialogBuilder(getContext());
         final  String items []=new String[]{"开启空气净化","开启消杀LED"};
         builder.setTitle("是否开启消杀灯与空气净化")
                 .addItems(items, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                 }).setCheckedItems(new int[]{0})
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {

                        int keyID = DBUtils.getInstance().DBInsertTimeTask(
                                originTaskSpinner.getSelectedItem().toString(),
                                ManageTaskDB.taskLists.get(ManageTaskDB.taskNameList.indexOf(originTaskSpinner.getSelectedItem().toString())).ID,
                                String.format("%02d", hourOfD)+":"+String.format("%02d", min),
                                dateSpinner.getSelectedItem().toString(),
                                RosData.currentMapID,
                                loopSpinner.getSelectedItem().toString(),
                                modeSpinner.getSelectedItem().toString());

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfD);
                        calendar.set(Calendar.MINUTE, min);
                        calendar.set(Calendar.SECOND, 0);
                        AlarmManagerUtils.getInstance(null).createPeriodAlarmManager(calendar, keyID, builder.getCheckedItemRecord().get(0), builder.getCheckedItemRecord().get(1));
                        Toast.makeText(getContext(),"设置成功: "+hourOfD+":"+min,Toast.LENGTH_SHORT).show();
                        Log.d("Alarm", "fan_switch = " + builder.getCheckedItemRecord().get(0) + "   led_switch = " + builder.getCheckedItemRecord().get(1));

                        dialog.dismiss();
                        Navigation.findNavController(view).navigateUp();
                    }
                })
                .show();
    }
}
