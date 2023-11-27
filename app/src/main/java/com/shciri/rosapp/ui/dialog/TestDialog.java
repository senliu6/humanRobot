package com.shciri.rosapp.ui.dialog;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentActivity;

import com.hjq.shape.view.ShapeButton;
import com.hjq.shape.view.ShapeRadioButton;
import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.client.RosTopic;

import src.com.jilk.ros.message.requestparam.ClampControl;

/**
 * 功能：抱夹测试弹窗
 *
 * @author ：liudz
 * 日期：2023年10月20日
 */
public final class TestDialog {
    public static final class Builder extends BuildDialog.Builder<Builder> {
        private EditText editX, editY, editH, editBottomH;
        private RadioGroup radioGroup;
        private ShapeRadioButton radioButtonClown, radioButtonPut;

        private ShapeButton buttonConfirm, buttonCancel, buttonStop, buttonReset;
        private ImageView ivExit;
        private byte type = 0;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_test);
            setAnimStyle(BaseAttrDialog.AnimStyle.IOS);
            editX = findViewById(R.id.editX);
            editY = findViewById(R.id.editY);
            editH = findViewById(R.id.editH);
            editBottomH = findViewById(R.id.editBottomH);

            radioGroup = findViewById(R.id.radioList);
            radioButtonClown = findViewById(R.id.btnClown);
            radioButtonPut = findViewById(R.id.btnPut);

            buttonConfirm = findViewById(R.id.tvConfirm);
            buttonCancel = findViewById(R.id.tvCancel);
            buttonStop = findViewById(R.id.btnStop);
            buttonReset = findViewById(R.id.btnReset);

            ivExit = findViewById(R.id.ivExit);

            initView();
            setCanceledOnTouchOutside(false);

        }

        private void initView() {
            //点击发送
            buttonConfirm.setOnClickListener(v -> {
                if (type == 0) {
                    Toaster.showShort("请选择抱或者取");
                } else {
                    if (!TextUtils.isEmpty(editX.getText()) && !TextUtils.isEmpty(editY.getText()) && !TextUtils.isEmpty(editH.getText())
                            && !TextUtils.isEmpty(editBottomH.getText())) {
                        ClampControl clampControl = new ClampControl();
                        clampControl.goal_id = Byte.parseByte(editX.getText().toString());
                        clampControl.plan_mode = Byte.parseByte(editY.getText().toString());
                        clampControl.top_height = Short.parseShort(editH.getText().toString());
                        clampControl.bottom_height = Short.parseShort(editBottomH.getText().toString());
                        clampControl.claw = type;
                        RosTopic.publishClampControl(clampControl);
                    } else {
                        Toaster.showShort("请输入正确的值");
                    }
                }
            });
            //点击选择
            radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                if (i == R.id.btnClown) {
                    radioButtonClown.setChecked(true);
                    type = 0x01;
                } else if (i == R.id.btnPut) {
                    radioButtonPut.setChecked(true);
                    type = 0x02;
                }
            });
            //点击取消
            buttonCancel.setOnClickListener(v -> {
                ClampControl clampControl = new ClampControl();
                clampControl.exit_task = true;
                RosTopic.publishClampControl(clampControl);
            });
            //点击急停
            buttonStop.setOnClickListener(v -> {
                ClampControl clampControl = new ClampControl();
                clampControl.emergency_stop = true;
                RosTopic.publishClampControl(clampControl);
            });
            //点击复位
            buttonReset.setOnClickListener(v -> {
                ClampControl clampControl = new ClampControl();
                clampControl.reset = true;
                RosTopic.publishClampControl(clampControl);
            });
            //点击取消
            ivExit.setOnClickListener(v -> dismiss());
        }


    }
}
