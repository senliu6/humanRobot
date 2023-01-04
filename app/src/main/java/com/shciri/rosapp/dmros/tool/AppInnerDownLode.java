package com.shciri.rosapp.dmros.tool;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppInnerDownLode {
    public final static String SD_FOLDER = Environment.getExternalStorageDirectory()+ "/UpdateApk/";
    private static final String TAG = AppInnerDownLode.class.getSimpleName();

    /**
     * 从服务器中下载APK
     */
    @SuppressWarnings("unused")
    public void URLDownloadApk(final Context mContext, final String downURL, final String apkName ) {

        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(mContext);
        pd.setCancelable(false);// 必须一直下载完，不可取消
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载安装包，请稍后");
        pd.setTitle("版本升级");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    String fileName = downloadFile(downURL, apkName, pd);
                    sleep(2000);
                    installApk(mContext, fileName);
                    // 结束掉进度条对话框
                    pd.dismiss();
                } catch (Exception e) {
                    pd.dismiss();

                }
            }
        }.start();
    }

    /**
     * 从服务器下载最新更新文件
     *
     * @param path
     *            下载路径
     * @param pd
     *            进度条
     * @return
     * @throws Exception
     */
    private String downloadFile(String path, String apkName ,ProgressDialog pd) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        Log.d("SelfUpdate", "path = " + path);
        URL url = new URL(path);
        //打开连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");//请求方式
        String temp = conn.getHeaderField("Content-Disposition");
        Log.w(TAG, "HeaderField=" + temp);
        Log.i("ResponseCode", "prepare");
        Log.i("ResponseCode", conn.getResponseCode() + "");
        if (conn.getResponseCode() == 200) {//响应码==200 请求成功
            //打开输入流
            InputStream is = conn.getInputStream();
            // 获取到文件的大小
            int contentLength = conn.getContentLength();
            pd.setMax(contentLength);
            Log.i(TAG, "contentLength = " + contentLength);

            //创建文件夹，在存储卡下
            File file = new File(SD_FOLDER);
            boolean ok;
            // 目录不存在创建目录
            if (!file.exists())
                ok = file.mkdir();

            //下载后的文件名
            String fileName = SD_FOLDER + apkName;
            File file1 = new File(fileName);
            if (file1.exists()) {
                ok = file1.delete();
            }

            //创建字节流
            byte[] buffer = new byte[1024];
            int len;
            FileOutputStream fos = new FileOutputStream(file1);
            BufferedInputStream bis = new BufferedInputStream(is);

            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                pd.setProgress(total);
            }
            Log.d("SelfUpdate", "total = " + total);
            fos.close();
            bis.close();
            is.close();
            return fileName;
        }
        return null;
    }

    /**
     * 安装apk
     */
    private static void installApk(Context mContext, String fileName) {
        Log.d("SelfUpdate", fileName);
        File apk = new File(fileName);
        try {
            // 这里有文件流的读写，需要处理一下异常
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24
                String packageName = mContext.getApplicationContext().getPackageName();
                String authority = new StringBuilder(packageName).append(".fileprovider").toString();
                Uri uri = FileProvider.getUriForFile(mContext, authority, apk);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                Uri uri = Uri.fromFile(apk);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            Log.d("SelfUpdate", "installApk");
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // 安装的时候会进行版本自动检测，更新版本小于已有版本，是会走当前异常的，注意！
        }
    }

//    /**
//     * 获取应用程序版本（versionName）
//     *
//     * @return 当前应用的版本号
//     */
//
//    private static double getLocalVersion(Context context) {
//        PackageManager manager = context.getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(context.getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "获取应用程序版本失败，原因：" + e.getMessage());
//            return 0.0;
//        }
//
//        return Double.valueOf(info.versionName);
//    }
//    /**
//     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
//     *
//     * @param bytes
//     * @return
//     */
//    public static String bytes2kb(long bytes) {
//        BigDecimal filesize = new BigDecimal(bytes);
//        BigDecimal megabyte = new BigDecimal(1024 * 1024);
//        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
//                .floatValue();
//        if (returnValue > 1)
//            return (returnValue + "MB");
//        BigDecimal kilobyte = new BigDecimal(1024);
//        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
//                .floatValue();
//        return (returnValue + "KB");
//    }
}