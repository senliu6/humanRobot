package com.shciri.rosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shciri.rosapp.ui.TaskControlActivity;
import com.shciri.rosapp.ui.myview.LoginKeyboardView;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private EditText passwordEdit;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* 防止软键盘自动弹出 */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        password = new String("");
        LoginKeyboardView loginKeyboardView = findViewById(R.id.loginKeyboard);
        loginKeyboardView.setLoginKeyboardListener(new LoginKeyboardView.LoginKeyboardListener() {
            @Override
            public void KeyInput(int key) {
                System.out.println("key = " + key);
                if (password.length() >= 12) {
                    Toast.makeText(getApplicationContext(), "密码超出最大长度!", Toast.LENGTH_SHORT).show();
                    return;
                }
                passwordSet(key);
            }
        });

        passwordEdit = findViewById(R.id.password_Edit);

        findViewById(R.id.loginBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals("123456")) {
                    Intent intent = new Intent(MainActivity.this, TaskControlActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "密码错误,请重试!", Toast.LENGTH_SHORT).show();
                    password = "";
                    passwordEdit.setText(password);
                }
            }
        });
    }

    private void passwordSet(int key) {
        switch (key) {
            case 10:
                password = "";
                passwordEdit.setText(password);
                passwordEdit.setSelection(password.length());
                break;

            case 11:
                password += String.valueOf(0);
                passwordEdit.setText(password);
                passwordEdit.setSelection(password.length());
                break;

            case 12:
                if (password.length() >= 1) {
                    password = password.substring(0, password.length() - 1);
                    passwordEdit.setText(password);
                    passwordEdit.setSelection(password.length());
                }
                break;

            default:
                password += String.valueOf(key);
                passwordEdit.setText(password);
                passwordEdit.setSelection(password.length());
                break;
        }

        if (password.length() > 0) {
            findViewById(R.id.loginBt).setBackgroundResource(R.mipmap.login_denglu4_21);
//            setViewLayoutParams(findViewById(R.id.loginBt), 481, 84);
        } else {
            findViewById(R.id.loginBt).setBackgroundResource(R.mipmap.denglu);
//            setViewLayoutParams(findViewById(R.id.loginBt), 461, 64);
        }
    }

    /**
     * 重设 view 的宽高
     */
    public static void setViewLayoutParams(View view, int nWidth, int nHeight) {
        int theW = dip2px(view.getContext(), nWidth);
        int theH = dip2px(view.getContext(), nHeight);

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.height != theH || lp.width != theW) {
            lp.width = theW;
            lp.height = theH;
            view.setLayoutParams(lp);
        }
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestFullscreen();
//        setStatusBar();
    }

    protected void setStatusBar() {
        Window window = getWindow();

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private int getStatusBarHeight(Context baseContext) {
        return 0;
    }

    protected void requestFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE  /* 稳定布局，主要是在全屏和非全屏切换时，布局不要有大的变化。 */
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  /* 粘性沉浸模式 */
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION /* content doesn't resize when the system bars hide and show. */
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}