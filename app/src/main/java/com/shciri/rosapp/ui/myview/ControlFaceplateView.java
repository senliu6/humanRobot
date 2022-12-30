package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.shciri.rosapp.R;

public class ControlFaceplateView extends RelativeLayout  {

    private JointControlListener jointControlListener;//移动回调的接口

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

    public ControlFaceplateView(Context context) throws InterruptedException {
        super(context);
        initView(context);
    }

    public ControlFaceplateView(Context context, AttributeSet attrs) throws InterruptedException {
        super(context, attrs);
        initView(context);
    }

    public ControlFaceplateView(Context context, AttributeSet attrs, int defStyleAttr) throws InterruptedException {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) throws InterruptedException {

        LayoutInflater.from(context).inflate(R.layout.activity_control_faceplate, this, true);
        //初始化控件
        seekBar1 = findViewById(R.id.seekBar1);
        textView1 = findViewById(R.id.textView1);
        seekBar2 = findViewById(R.id.seekBar2);
        textView2 = findViewById(R.id.textView2);
        seekBar3 = findViewById(R.id.seekBar3);
        textView3 = findViewById(R.id.textView3);
        seekBar4 = findViewById(R.id.seekBar4);
        textView4 = findViewById(R.id.textView4);
        seekBar5 = findViewById(R.id.seekBar5);
        textView5 = findViewById(R.id.textView5);
        seekBar6 = findViewById(R.id.seekBar6);
        textView6 = findViewById(R.id.textView6);

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
                jointControlListener.jointControl(id , value);
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

    public void setJointControlListener(JointControlListener jointControlListener) {
        this.jointControlListener = jointControlListener;
    }

    /**
     *回调事件的接口
     **/
    public interface JointControlListener {
        void jointControl(int id, float dx);
    }
}