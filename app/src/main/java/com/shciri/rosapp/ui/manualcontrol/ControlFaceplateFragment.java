package com.shciri.rosapp.ui.manualcontrol;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.shciri.rosapp.R;
import com.shciri.rosapp.ui.view.ControlFaceplateView;

public class ControlFaceplateFragment extends Fragment {

    private ControlFaceplateView.JointControlListener jointControlListener;//移动回调的接口

    private SeekBar seekBar1;
    private TextView textView1;
    private SeekBar seekBar2;
    private TextView textView2;
    private SeekBar seekBar3;
    private TextView textView3;
    private SeekBar seekBar4;
    private TextView textView4;
    private SeekBar seekBar5;
    private TextView textView5;
    private SeekBar seekBar6;
    private TextView textView6;
    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_faceplate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    public void initView(View root) {
        //初始化控件
        seekBar1 = root.findViewById(R.id.seekBar1);
        textView1 = root.findViewById(R.id.textView1);
        seekBar2 = root.findViewById(R.id.seekBar2);
        textView2 = root.findViewById(R.id.textView2);
        seekBar3 = root.findViewById(R.id.seekBar3);
        textView3 = root.findViewById(R.id.textView3);
        seekBar4 = root.findViewById(R.id.seekBar4);
        textView4 = root.findViewById(R.id.textView4);
        seekBar5 = root.findViewById(R.id.seekBar5);
        textView5 = root.findViewById(R.id.textView5);
        seekBar6 = root.findViewById(R.id.seekBar6);
        textView6 = root.findViewById(R.id.textView6);

        setSeekBarListener(0, seekBar1, textView1);
        setSeekBarListener(1, seekBar2, textView2);
        setSeekBarListener(2, seekBar3, textView3);
        setSeekBarListener(3, seekBar4, textView4);
        setSeekBarListener(4, seekBar5, textView5);
        setSeekBarListener(5, seekBar6, textView6);
    }
    //注册拖动监听器
    public void setSeekBarListener(int id, SeekBar seekBar, TextView textView) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value;
                value = (progress - 500) / 500f;
                textView.setText(Float.toString(value));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(500);
            }
        });
    }
}