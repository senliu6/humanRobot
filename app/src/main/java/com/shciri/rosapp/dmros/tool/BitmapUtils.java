package com.shciri.rosapp.dmros.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapUtils {
    /**
     * 将Bitmap转成本地图片
     * @param mapID 保存为本地图片的地址
     * @param bitmap 要转化的Bitmap
     * @return MD5
     */
    public static String saveImage(String name, int mapID, Bitmap bitmap){
        String root = Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath();
        String dirName = "RobotLocalMap";
        File appDir = new File(root , dirName);
        if (!appDir.exists()) {
            if(!appDir.mkdirs()) {
                Log.d("mkdir fail!", "Save Path=" + appDir.getAbsolutePath());
                return "";
            }
        }

        String fileName = name+"_"+Integer.toString(mapID)+".png";

        try {
            //获取文件
            File saveFile = new File(appDir, fileName);
            FileOutputStream fos = new FileOutputStream(saveFile);;
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
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
    public static boolean deleteMap(String name, int mapID) {
        String fileName = name+"_"+Integer.toString(mapID)+".png";
        String root = Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath();
        String dirName = "RobotLocalMap";
        String filePath$Name = root+"/"+dirName+"/"+fileName;
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.w("deleteMap", "delete map " + filePath$Name + "success!");
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
