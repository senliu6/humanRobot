package com.shciri.rosapp.ui.addtask;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
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

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.ui.control.ChooseTaskFragment;
import com.shciri.rosapp.utils.AlarmManagerUtils;

import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AddNewTimeTaskFragment extends Fragment {

    private TimePickerDialog timePickerDialog;
    private int year, monthOfYear, dayOfMonth, hourOfD, min;

    private Spinner loopSpinner;
    private Spinner dateSpinner;
    private Spinner mapSpinner;
    private Spinner modeSpinner;
    private TextView timeView;
    private TextView cancelBtn;
    private TextView confirmBtn;

    private String date;

    private AlarmManagerUtils alarmManagerUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_new_time_task, container, false);
    }

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
                Toast.makeText(getContext(), hourOfDay + ":" + minute,
                        Toast.LENGTH_LONG).show();
                timeView.setText((String)(" "+hourOfDay+":"+minute));
                hourOfD = hourOfDay;
                min = minute;
            }
        }, hourOfD, min, true);

        timeView = view.findViewById(R.id.add_time_task_time_select);
        timeView.setText((String)(" "+hourOfD+":"+min));
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

        mapSpinner = view.findViewById(R.id.add_time_task_map_spinner);
        mapSpinner.setAdapter(ChooseTaskFragment.mapAdapter);

        String[] modeOption = {"消杀与空气进化","仅消杀","仅空气净化","空跑"};
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getContext(), R.layout.task_bt_spinner_item_select, modeOption);
        //设置数组适配器的布局样式
        modeAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        modeSpinner = view.findViewById(R.id.add_time_task_mode_spinner);
        modeSpinner.setAdapter(modeAdapter);

        alarmManagerUtils = AlarmManagerUtils.getInstance(getContext());
        alarmManagerUtils.createGetUpAlarmManager();

        confirmBtn = view.findViewById(R.id.add_time_task_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfD);
                calendar.set(Calendar.MINUTE, min);
                calendar.set(Calendar.SECOND, 0);
                alarmManagerUtils.getUpAlarmManagerStartWork(calendar);
                Toast.makeText(getContext(),"设置成功: "+hourOfD+":"+min,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DBInsertTimeTask(String taskName, String time, String date, String mapName, int loop) {
        ContentValues values = new ContentValues();
        values.put("task_name",taskName);
        values.put("time",time);
        values.put("date",date);
        values.put("map_name",mapName);
        values.put("loop",loop);
        RCApplication.db.insert("time_task",null,values);
    }
}
