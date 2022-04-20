package com.shciri.rosapp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

public class MyPGM {

    char ch0,   ch1;
    int  width, height;
    int  maxpix;

    DataInputStream in;

    private final byte RADIUS = 4;

    /***************************************************************
     * 读入.pgm或.ppm文件
     * type 5:pgm, 6:ppm
     ***************************************************************/
    public int[] readData(int iw, int ih, int type, byte[] data, int poseX, int poseY)
    {
        int[] pixels = new int[iw*ih];
//        try
//        {
            if(type == 5)
            {
                for (int i = 0; i < iw*ih; i++)
                {
                    int b = -data[i];
                    if(b < 0) b = b + 256;

                    int a = b;
                    if(b == 0) a = 15;


                    int current_y = i / iw;
                    int current_x = i - (current_y * iw);
                    int pixel;

                    // 行数 = y坐标  列数 = x坐标
                    if(current_x < (poseX)+RADIUS
                            && current_x > (poseX)-RADIUS   //坐标点附近5列
                            && current_y < (poseY)+RADIUS
                            && current_y > (poseY)-RADIUS){  //坐标点附近5行

                        pixel = ((100)<<24)|(255<<16)|(0<<8)|0;
//                        System.out.println("okok x=" + poseX/0.05 + "  y=" +poseY/0.05);
                    }
                    else
                        pixel = ((a)<<24)|(b<<16)|(b<<8)|b;

                    //(iw*ih) - (1+i)
                    pixels[i] = pixel;
                }
            }
            else if(type == 6)
            {
                for(int i = 0; i < iw*ih; i++)
                {
                    int r = data[i];
                    if(r < 0) r = r + 256;
                    int g = data[i];
                    if(g < 0) g = g + 256;
                    int b = data[i];
                    if(b < 0) b = b + 256;
                    pixels[i] = (255<<24)|(r<<16)|(g<<8)|b;
                }
            }
//        }
//        catch(IOException e1){System.out.println("Exception!");}

        return pixels;
    }

    /**
     * 将图片内容解析成字节数组
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    /**
     * 将字节数组转换为ImageView可调用的Bitmap对象
     * @param bytes
     * @return Bitmap
     */
    public Bitmap getPicFromBytes(byte[] bytes) {
        if (bytes != null){
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            System.out.println("getPicFromBytes null");
        }

        return null;
    }
    /**
     * 图片缩放
     * @param bitmap 对象
     * @param w 要缩放的宽度
     * @param h 要缩放的高度
     * @return newBmp 新 Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }

    /**
     * 把Bitmap转Byte
     * @Author HEH
     * @EditTime 2010-07-19 上午11:45:56
     */
    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    /**
     * 把字节数组保存为一个文件
     * @Author HEH
     * @EditTime 2010-07-19 上午11:45:56
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public void bytesToImageFile(byte[] bytes) {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaa.pgm");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
