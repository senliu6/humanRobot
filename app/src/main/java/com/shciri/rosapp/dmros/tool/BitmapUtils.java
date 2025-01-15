package com.shciri.rosapp.dmros.tool;

import static androidx.core.content.ContextCompat.*;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    /**
     * 将Bitmap转成本地图片
     * @param mapID 保存为本地图片的地址
     * @param bitmap 要转化的Bitmap
     * @return MD5
     */
    public static String saveImage(Context context, String name, int mapID, Bitmap bitmap) {
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return "";
        }

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        String dirName = "RobotLocalMap";
        File appDir = new File(root, dirName);
        if (!appDir.exists()) {
            if (!appDir.mkdirs()) {
                Log.d("mkdir fail!", "Save Path=" + appDir.getAbsolutePath());
                return "";
            }
        }

        String fileName = name + "_" + mapID + ".png";

        try {
            //获取文件
            File saveFile = new File(appDir, fileName);
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return MD5Utils.getFileMD5String(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /** 删除单个文件
     * @param name 要删除的map名
     * @param mapID 要删除的mapID
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteMap(Context context, String name, int mapID) {
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }

        String fileName = name + "_1" + ".png";
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        String dirName = "RobotLocalMap";
        String filePath$Name = root + "/" + dirName + "/" + fileName;
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.w("deleteMap", "delete map " + filePath$Name + " success!");
                return true;
            } else {
                Log.w("deleteMap", "delete single fail!");
                return false;
            }
        } else {
            Log.w("deleteMap", "delete single fail, path or file don't existed!");
            return false;
        }
    }
}

