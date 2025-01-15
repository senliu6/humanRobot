package com.shciri.rosapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;

import com.hjq.shape.view.ShapeTextView;
import com.shciri.rosapp.R;

/**
 * @author liudz
 */
public class LoginKeyboardView extends GridLayout {

    private LoginKeyboardListener loginKeyboardListener;
    //列表大小
    private int Number = 12;

    //结果
    private String result = "";

    //字数限制
    private int limit = 12;

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
        ShapeTextView[] key = new ShapeTextView[Number];
        key[0] = (ShapeTextView) findViewById(R.id.imageButton1);
        key[1] = (ShapeTextView) findViewById(R.id.imageButton2);
        key[2] = (ShapeTextView) findViewById(R.id.imageButton3);
        key[3] = (ShapeTextView) findViewById(R.id.imageButton4);
        key[4] = (ShapeTextView) findViewById(R.id.imageButton5);
        key[5] = (ShapeTextView) findViewById(R.id.imageButton6);
        key[6] = (ShapeTextView) findViewById(R.id.imageButton7);
        key[7] = (ShapeTextView) findViewById(R.id.imageButton8);
        key[8] = (ShapeTextView) findViewById(R.id.imageButton9);
        key[9] = (ShapeTextView) findViewById(R.id.imageButton10);
        key[10] = (ShapeTextView) findViewById(R.id.imageButton11);
        key[11] = (ShapeTextView) findViewById(R.id.imageButton12);

        for (int i = 0; i < Number; i++) {
            int finalI = i + 1;
            key[i].setOnClickListener(view -> {
                passwordSet(finalI);
                loginKeyboardListener.keyInput(result);
            });
        }
    }

    public interface LoginKeyboardListener {
        void keyInput(String result);
    }

    public void setLoginKeyboardListener(LoginKeyboardListener loginKeyboardListener) {
        this.loginKeyboardListener = loginKeyboardListener;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return this.limit;
    }

    private void passwordSet(int key) {
        switch (key) {
            case 10:
                result = "";
                break;

            case 11:
                if (result.length() < limit) {
                    result += String.valueOf(0);
                }
                break;

            case 12:
                if (result.length() >= 1) {
                    result = result.substring(0, result.length() - 1);
                }
                break;

            default:
                if (result.length() < limit) {
                    result += String.valueOf(key);
                }
                break;
        }
    }

    public void clearResult() {
        this.result = "";
    }
}
