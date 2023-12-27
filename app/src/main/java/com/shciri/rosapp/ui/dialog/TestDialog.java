package com.shciri.rosapp.ui.dialog;


import android.widget.RadioGroup;

import androidx.fragment.app.FragmentActivity;

import com.hjq.shape.view.ShapeButton;
import com.hjq.shape.view.ShapeRadioButton;
import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.data.LanguageType;

/**
 * 功能：抱夹测试弹窗
 *
 * @author ：liudz
 * 日期：2023年10月20日
 */
public final class TestDialog {

    public static final class Builder extends BuildDialog.Builder<Builder> {
        private final RadioGroup radioGroup;
        private final ShapeRadioButton radioButtonChinese, radioButtonEnglish, radioButtonFan;

        private final ShapeButton buttonConfirm, buttonCancel;
        private byte type = 0;

        private OnClickListen listen;
        private String currentLanguage;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_test);
            setAnimStyle(BaseAttrDialog.AnimStyle.IOS);

            radioGroup = findViewById(R.id.radioList);
            radioButtonChinese = findViewById(R.id.btnChinese);
            radioButtonEnglish = findViewById(R.id.btnEnglish);
            radioButtonFan = findViewById(R.id.btnFan);

            buttonConfirm = findViewById(R.id.tvConfirm);
            buttonCancel = findViewById(R.id.tvCancel);

            initView();
            setCanceledOnTouchOutside(false);

        }

        private void initView() {
            //点击发送
            buttonConfirm.setOnClickListener(v -> {
                if (type == 0) {
                    Toaster.showShort("请选择切换的语言");
                } else {
                    listen.onConfirm(currentLanguage);
                }
            });
            //点击选择
            radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                if (i == R.id.btnChinese) {
                    radioButtonChinese.setChecked(true);
                    type = 0x01;
                    currentLanguage = LanguageType.CHINESE.getLanguage();
                } else if (i == R.id.btnFan) {
                    radioButtonFan.setChecked(true);
                    type = 0x02;
                    currentLanguage = LanguageType.FAN.getLanguage();
                } else if (i == R.id.btnEnglish) {
                    type = 0x03;
                    currentLanguage = LanguageType.ENGLISH.getLanguage();
                    radioButtonEnglish.setChecked(true);
                }
            });
            //点击取消
            buttonCancel.setOnClickListener(v -> dismiss());
        }

        public Builder setListener(OnClickListen listener) {
            listen = listener;
            return this;
        }

        public Builder setLanguage(String language) {
            currentLanguage = language;
            if (currentLanguage.equals(LanguageType.CHINESE.getLanguage())) {
                radioButtonChinese.setChecked(true);
            } else if (currentLanguage.equals(LanguageType.ENGLISH.getLanguage())) {
                radioButtonEnglish.setChecked(true);
            } else {
                radioButtonFan.setChecked(true);
            }
            return this;
        }

    }


    public interface OnClickListen {
        void onConfirm(String language);
    }
}
