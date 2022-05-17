package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.shciri.rosapp.R;

public class LoginKeyboardView extends GridLayout {

    private LoginKeyboardListener loginKeyboardListener;

    public LoginKeyboardView(Context context) {
        super(context);
    }

    public LoginKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoginKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_login_keyboard, this, true);
        ImageView key[] = new ImageView[12];
        key[0] = (ImageView) findViewById(R.id.imageButton1);
        key[1] = (ImageView) findViewById(R.id.imageButton2);
        key[2] = (ImageView) findViewById(R.id.imageButton3);
        key[3] = (ImageView) findViewById(R.id.imageButton4);
        key[4] = (ImageView) findViewById(R.id.imageButton5);
        key[5] = (ImageView) findViewById(R.id.imageButton6);
        key[6] = (ImageView) findViewById(R.id.imageButton7);
        key[7] = (ImageView) findViewById(R.id.imageButton8);
        key[8] = (ImageView) findViewById(R.id.imageButton9);
        key[9] = (ImageView) findViewById(R.id.imageButton10);
        key[10] = (ImageView) findViewById(R.id.imageButton11);
        key[11] = (ImageView) findViewById(R.id.imageButton12);

        for (int i=0; i<12; i++) {
            int finalI = i + 1;
            key[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginKeyboardListener.KeyInput(finalI);
                }
            });
        }
    }

    public interface LoginKeyboardListener {
        void KeyInput(int key);
    }

    public void setLoginKeyboardListener(LoginKeyboardListener loginKeyboardListener) {
        this.loginKeyboardListener = loginKeyboardListener;
    }
}
