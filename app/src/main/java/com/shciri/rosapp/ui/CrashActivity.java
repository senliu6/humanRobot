package com.shciri.rosapp.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.shciri.rosapp.R;
import com.shciri.rosapp.databinding.ActivityCrashBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * time   : 2023/12/04
 * desc   : 崩溃捕捉界面
 *
 * @author :liudz
 */
public final class CrashActivity extends AppCompatActivity {

    private CaocConfig mConfig;

    //用于格式化日期，作为日志文件名的一部分
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    String SD_Log = Environment.getExternalStorageDirectory() + "/Logcat/";

    private ActivityCrashBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initData();
    }


    protected void initView() {
        binding.btnCrashRestart.setOnClickListener(v -> CustomActivityOnCrash.restartApplication(CrashActivity.this, mConfig));
        binding.btnCrashLog.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(CrashActivity.this)
                    .setTitle(R.string.crash_error_details)
                    .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent()))
                    .setPositiveButton(R.string.crash_close, null)
                    .setNeutralButton(R.string.crash_copy_log, (dialog1, which) -> copyErrorToClipboard())
                    .show();
            TextView textView = dialog.findViewById(android.R.id.message);
            if (textView != null) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
        });
    }

    protected void initData() {
        mConfig = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        if (mConfig == null) {
            // 这种情况永远不会发生，只要完成该活动就可以避免递归崩溃。
            finish();
        }
        saveCrashInfo2File(CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent()));
    }

    /**
     * 复制报错信息到剪贴板
     */
    @SuppressWarnings("all")
    private void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent());
        ContextCompat.getSystemService(this, ClipboardManager.class).setPrimaryClip(ClipData.newPlainText(getString(R.string.crash_error_info), errorInformation));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * errorMessage
     *
     * @param errorMessage
     * @return
     */
    private String saveCrashInfo2File(String errorMessage) {
        File dir = new File(SD_Log);
        if (dir.exists()) {
            //  Logger.e("文件夹已存在，无须创建");
        } else {
            Log.d("CeshiTAG", "创建文件");
            dir.mkdirs();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(errorMessage);
        //存到文件
        String time = dateFormat.format(new Date());
        String fileName = "crash-" + time + ".txt";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                Log.d("CeshiTAG", "建立log文件");
                File path = new File(SD_Log);
                FileOutputStream fos = new FileOutputStream(path + "/" + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }
}