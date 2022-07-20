package com.shciri.rosapp.dmros.tool;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Environment;
import android.util.Log;

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
     */
    public static void saveImage(String name, int mapID, Bitmap bitmap){
        String root = Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath();
        String dirName = "RobotLocalMap";
        File appDir = new File(root , dirName);
        if (!appDir.exists()) {
            if(!appDir.mkdirs()) {
                Log.d("mkdir fail!", "Save Path=" + appDir.getAbsolutePath());
                return;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
