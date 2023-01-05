package com.shciri.rosapp.dmros.tool;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 版本更新管理类
 * 用法:
 * 1. 初始化 VersionUpdateManager
 * 2. 在activity的 onResume() 中调用 registerDownloadBroadcast注册广播
 * 3. 在activity的 onPause() 中调用 unregisterDownloadBroadcast解除注册广播
 * 4. 在初始化回调接口中处理 没有权限 和 下载完成 的处理
 *  (1) 没有权限: 跳转系统应用设置页面
 *  (2) 下载完成: 调用 install()方法安装apk, 如果没有注册广播收不到监听
 */
public class VersionUpdateManager extends AppInnerDownLode{

    /*没有权限请求时的requestCode*/
    public static int VersionUpdateManagerPermissionCode = 8;

    /*传入的context*/
    private Context mContext;
    /*监听listener*/
    private VersionUpdateManager.versionUpdateListener mListener;
    /*最新的版本号*/
    public int updateVersion;
    /* 当前的版本号 */
    public int versionName = 3;
    /*下载链接*/
    public String downloadURL;
    /*是否有安装apk的权限*/
    private Boolean haveInstallPermission = true;
    /*下载apk的manager*/
    private DownloadManager downloadManager;
    String apkURL = "https://www.zqn.ink/q2.php";
    private final int appID = 70;
    private int aid;
    private String updateSummary;
    private String apkName;

    /**
     * 初始化
     * @param context context
     * @param listener listener
     */
    public VersionUpdateManager(Context context, VersionUpdateManager.versionUpdateListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void queryAPKInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                String url = apkURL + "?" + "q="+"new_app" + "&dbg_mod="+1 + "$app_id="+appID;
                Log.d("SelfUpdate",url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String res = response.body().string();
                    Log.d("SelfUpdate", "response = " + res);
                    if (res != null && !res.trim().equals("")) {
                        JSONObject jsonObject = new JSONObject(res);
                        aid = jsonObject.getInt("aid[0]");
                        updateVersion = jsonObject.getInt("version");
                        updateSummary = jsonObject.getString("orgfilename[0]") + "\n" +
                                "file size: " + jsonObject.getInt("filesize[0]")/1000000.0f + "M";
                        apkName = jsonObject.getString("orgfilename[0]");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 检查版本更新
     */
    public void checkUpdateVersion() {
        queryAPKInfo();
        if (compareVersion(updateVersion, versionName) == 1) {
            //版本判断
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //获取是否有安装未知来源应用的权限
                haveInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
            }
            downloadURL = "https://www.zqn.ink/?attach-download-" + aid + ".htm";
            Log.d("SelfUpdate", downloadURL);
            showUpdateTipdialog(Integer.toString(updateVersion), updateSummary, downloadURL);
        } else {
            mListener.alreadyLastVersion();
        }
    }

    /**
     * 版本号比较
     *
     * @param lastVersion 服务器版本号
     * @param localVersion 当前版本号
     * @return 0代表相等，1代表version1大于version2，-1代表version1小于version2
     */
    private int compareVersion(String lastVersion, String localVersion) {
        if (lastVersion.equals(localVersion)) {
            return 0;
        }
        String[] version1Array = lastVersion.split("\\.");
        String[] version2Array = localVersion.split("\\.");
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    private int compareVersion(int lastVersion, int localVersion) {
        Log.d("SelfUpdate", "lastVersion = " + lastVersion + " localVersion = "+ localVersion);
        return Integer.compare(lastVersion, localVersion);
    }
    
    /**
     * 展示版本更新提示
     * @param serverVersion 服务器版本号
     * @param updateSummary 更新内容
     * @param downloadURL 下载url
     */
    private void showUpdateTipdialog(String serverVersion, String updateSummary, String downloadURL) {
        int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

        new QMUIDialog.MessageDialogBuilder(mContext)
                .setTitle("检查到有新版本")
                .setMessage("last version: " + serverVersion + "\n" + updateSummary)
                .setCanceledOnTouchOutside(false)
                .setSkinManager(QMUISkinManager.defaultInstance(mContext))
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        mListener.clickCancel();
                        dialog.dismiss();
                    }
                })
                .addAction(0, "立即更新", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Log.d("update ","点击立即更新");
                        if (!haveInstallPermission) {
                            Toast.makeText(mContext, "请打开安装未知来源应用的权限", Toast.LENGTH_SHORT).show();

                            Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
                            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);

                            mListener.haveNoInstallPermission(intent, VersionUpdateManagerPermissionCode);
                        }else {
                            //下载更新
                            URLDownloadApk(mContext, downloadURL, apkName);
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    /**
     * manager的回调接口
     */
    public interface versionUpdateListener {
        /**
         * 没有安装权限
         */
        void haveNoInstallPermission(Intent intent, int requestCode);

        /**
         * 已经是最新版本
         */
        void alreadyLastVersion();

        /**
         * 点击取消
         */
        void clickCancel();
    }

}