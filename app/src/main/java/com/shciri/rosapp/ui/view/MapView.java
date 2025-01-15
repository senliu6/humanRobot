package com.shciri.rosapp.ui.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.data.DataProcess;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.utils.PointDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import src.com.jilk.ros.message.Point;
import src.com.jilk.ros.message.custom.Pose2D;

public class MapView extends View {
    //显示图片
    public Bitmap mBitmap = null;
    //
    private Bitmap mOriginBitmap = null;
    //图宽和高
    private int viewWidth, viewHeight;
    //当前的图片的矩阵参数
    private Matrix mCurrentMatrix = new Matrix();

    //robot
    private Bitmap mRobotImage = null;
    private Bitmap mChargeImage = null;
    float mRobotImageWidth = 30;
    float mRobotImageHeight = 30;

    boolean isAddPathState = false;
    float mCentorX = 0.0F;
    float mCentorY = 0.0F;

    boolean isShowRobotState = false;
    public boolean isShowRobotPath = false;
    float mRobotDirection = 0F;
    PointF mRobotPoint = new PointF();
    ArrayList<PointF> mRobotPath = new ArrayList<>();
    /* 自动规划的路径点数组 */
    public ArrayList<PointF> coveragePath = new ArrayList<>();
    public boolean isSetGoal = false;

    DataProcess dataProcess = new DataProcess();

    private float initMapToCenterOffset = 0f;
    private int updateMapNum = 0;
    public static boolean scanning;

    public static Pose2D pose = new Pose2D();

    public static Point[] pointsArray = null;

    public static Point onClickPoint = new Point();

    public boolean upLocation;

    public Point onClickPointPost = new Point();

    public static List<Point> wallPoints = new ArrayList<>();
    private Point wallPoint = new Point();

    private Paint wallpaint = new Paint();

    public boolean isShowWall = true;


    public MapView(Context context) {
        super(context);
        initPaints();
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    //擦除画笔
    Paint mErasePaint;
    //
    Paint mErasePointPaint;

    Paint mAddPathPaint;
    Paint mAddPathPointPaint;
    Paint mAddPathStartPointPaint;

    Paint mVirtualWallPathPaint;
    Paint mVirtualWallPathPointPaint;
    Paint mVirtualWallPathStartPointPaint;
    Paint mVirtualWallUnsavePath;

    Paint mShowRectPaint;
    Paint mShowOldRectPaint;
    Paint mShowRectPointPaint;

    Paint mRobotPathPaint;

    private void initPaints() {

        //添加路径功能
        mAddPathPointPaint = new Paint();
        mAddPathPointPaint.setColor(Color.BLUE);
        mAddPathStartPointPaint = new Paint();
        mAddPathStartPointPaint.setColor(0xFFFF7F50);

        mAddPathPaint = new Paint();
        mAddPathPaint.setColor(Color.GREEN);
        mAddPathPaint.setAntiAlias(true);
        mAddPathPaint.setStyle(Paint.Style.STROKE);
        mAddPathPaint.setStrokeWidth(2);


        //机器人路径
        mRobotPathPaint = new Paint();
        mRobotPathPaint.setColor(Color.YELLOW);
        mRobotPathPaint.setAntiAlias(true);
        mRobotPathPaint.setStyle(Paint.Style.STROKE);
        mRobotPathPaint.setStrokeCap(Paint.Cap.ROUND);
        mRobotPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mRobotPathPaint.setStrokeWidth(8);

        //添加虚拟墙
        mVirtualWallPathPaint = new Paint();
        mVirtualWallPathPaint.setPathEffect(new DashPathEffect(new float[]{5, 3}, 1));
        mVirtualWallPathPaint.setColor(Color.BLACK);
        mVirtualWallPathPaint.setAntiAlias(true);
        mVirtualWallPathPaint.setStyle(Paint.Style.STROKE);
        mVirtualWallPathPaint.setStrokeWidth(10);

        mVirtualWallPathPointPaint = new Paint();
        mVirtualWallPathPointPaint.setColor(Color.BLUE);
        mVirtualWallPathStartPointPaint = new Paint();
        mVirtualWallPathStartPointPaint.setColor(Color.RED);

        mVirtualWallUnsavePath = new Paint();
        mVirtualWallUnsavePath.setColor(Color.GRAY);
        mVirtualWallUnsavePath.setStrokeWidth(10);
        mVirtualWallUnsavePath.setPathEffect(new DashPathEffect(new float[]{5, 3}, 1));

        wallpaint = new Paint();
        wallpaint.setStrokeWidth(5f);
        wallpaint.setColor(Color.BLACK);

    }

    /**
     * 通用接口
     **/
    public void setBitmap(Bitmap map, updateMapID mapId) {
        this.mBitmap = map.copy(Bitmap.Config.ARGB_8888, true);

        if (mapId == updateMapID.SCANNING) {
            float xScale = ((float) viewWidth) / mBitmap.getWidth();
            float yScale = ((float) viewHeight) / mBitmap.getHeight();
            float scale = Math.min(xScale, yScale);
            mCurrentMatrix.setScale(scale, scale);
            if (scale == xScale) {
                initMapToCenterOffset = (((float) viewHeight) - (mBitmap.getHeight() * scale)) / 2;
                mCurrentMatrix.postTranslate(0, initMapToCenterOffset);
            } else {
                initMapToCenterOffset = (((float) viewWidth) - (mBitmap.getWidth() * scale)) / 2;
                mCurrentMatrix.postTranslate(initMapToCenterOffset, 0);
            }
        } else {
            if (getWidth() != 0 && getHeight() != 0) {
                if (updateMapNum == 0) {
                    mCurrentMatrix = new Matrix();
                    float xScale = ((float) viewWidth) / mBitmap.getWidth();
                    float yScale = ((float) viewHeight) / mBitmap.getHeight();
                    float scale = Math.min(xScale, yScale);
                    mCurrentMatrix.setScale(scale, scale);
                    if (scale == xScale) {
                        initMapToCenterOffset = (((float) viewHeight) - mBitmap.getHeight() * scale) / 2;
                        mCurrentMatrix.postTranslate(0, initMapToCenterOffset);
                    } else {
                        initMapToCenterOffset = (((float) viewWidth) - mBitmap.getWidth() * scale) / 2;
                        mCurrentMatrix.postTranslate(initMapToCenterOffset, 0);
                    }
                }
            }
        }
        updateMapNum++;
        postInvalidate();
    }

    public void reset() {
        float xScale = ((float) viewWidth) / mBitmap.getWidth();
        float yScale = ((float) viewHeight) / mBitmap.getHeight();
        float scale = Math.min(xScale, yScale);
        mCurrentMatrix.setScale(scale, scale);
        if (scale == xScale) {
            initMapToCenterOffset = (((float) viewHeight) - mBitmap.getHeight() * scale) / 2;
            mCurrentMatrix.postTranslate(0, initMapToCenterOffset);
        } else {
            initMapToCenterOffset = (((float) viewWidth) - mBitmap.getWidth() * scale) / 2;
            mCurrentMatrix.postTranslate(initMapToCenterOffset, 0);
        }
        pathPointList.clear();
        postInvalidate();
    }

    public void SetRobotBitmap(Bitmap robot) {
        this.mRobotImage = robot;
        scaleRobotImage();
        postInvalidate();
    }

    //推出所有状态
    public void exitAllState() {
        isShowRobotState = false;
        isAddPathState = false;
        postInvalidate();
    }

    /**
     * 添加路径
     **/
    public ArrayList<PointF> pathPointList = new ArrayList<>();
    public ArrayList<PointF> DBPathPointList = new ArrayList<>();

    private boolean isShowDBPath;

    public void setShowDBPath(boolean show) {
        isShowDBPath = show;
        isAddPathState = !show;
    }

    private void performShowDBPath(Canvas canvas) {
        if (isShowDBPath) {
            // draw record paths
            float[] transPoint = new float[]{0, 0};
            Path path = new Path();
            for (int i = 0; i < DBPathPointList.size(); i++) {
                PointF point = DBPathPointList.get(i);
                transPoint[0] = point.x;
                transPoint[1] = point.y;
                mCurrentMatrix.mapPoints(transPoint);
                if (i == 0) {
                    path.moveTo(transPoint[0], transPoint[1]);
                } else {
                    path.lineTo(transPoint[0], transPoint[1]);
                }
                canvas.drawCircle(transPoint[0], transPoint[1], 5, mAddPathPointPaint);
            }
            canvas.drawPath(path, mAddPathPaint);
        }
    }


    public void startPathState() {
        exitAllState();
        isAddPathState = true;
        postInvalidate();
    }

    public boolean isAddPathState() {
        return isAddPathState;
    }

    public void addPathPoint() {
        float[] transPoints = new float[]{mCentorX, mCentorY};
        transPoints = getInvertPoints(transPoints);
        pathPointList.add(new PointF(transPoints[0], transPoints[1]));
        postInvalidate();
    }

    public ArrayList<PointF> savePath() {
        return pathPointList;
    }

    public void exitAddPathState() {
        isAddPathState = false;
        postInvalidate();
    }

    /**
     * 添加虚拟墙
     **/
    boolean isVirtualWallState = false;
    Path mVirtualWallPath = null;
    PointF mVirtualWallEndPoint = null;
    ArrayList<Path> mVirtualWallPaths = new ArrayList<>();

    private int index = 0;

    Path mVirtualWallPathS = null;

    public void startVirtualWallState(ArrayList<Path> oldPaths) {
        exitAllState();
        if (oldPaths != null) {
            mVirtualWallPaths.addAll(oldPaths);
        }
        isVirtualWallState = true;
        postInvalidate();
    }

    public boolean isVirtualWallState() {
        return isAddPathState;
    }

    public void addVirtualWallPathPoint() {
        float[] transPoints = new float[]{mCentorX, mCentorY};
        transPoints = getInvertPoints(transPoints);
        mVirtualWallEndPoint = new PointF(transPoints[0], transPoints[1]);
        if (mVirtualWallPath == null) {
            mVirtualWallPath = new Path();
            mVirtualWallPath.moveTo(transPoints[0], transPoints[1]);
        } else {
            mVirtualWallPath.lineTo(transPoints[0], transPoints[1]);
        }
        wallPoint = new Point();
        wallPoint.x = transPoints[0];
        wallPoint.y = transPoints[1];
        wallPoints.add(wallPoint);
        postInvalidate();
    }

    public void saveVirtualWallPathPoints(String mapName) {
        if (mVirtualWallPath != null) {
            mVirtualWallPaths.add(mVirtualWallPath);
            mVirtualWallPath = null;
            mVirtualWallEndPoint = null;
        }
        if (wallPoints.size() > 0) {
            PointDataManager.INSTANCE.savePoints(getContext(), mapName, wallPoints);
            wallPoints.clear();
        }
    }

    public ArrayList<Path> getVirtualWallPaths() {
        return mVirtualWallPaths;
    }

    public void exitVirtualWallState() {
        mVirtualWallPath = null;
        mVirtualWallEndPoint = null;
        isVirtualWallState = false;
        postInvalidate();
    }


    /**
     * 添加机器人
     **/
    public void startRobotPositionState() {
        exitAllState();
        isShowRobotState = true;
        postInvalidate();
    }

    public void setRobotPosition(float x, float y, float direction, boolean isShowPath) {
        isShowRobotState = true;
        mRobotDirection = direction;
        mRobotPoint.x = x;
        mRobotPoint.y = y;
        if (isShowRobotPath != isShowPath) {
            mRobotPath.clear();
            isShowRobotPath = isShowPath;
        }
        if (isShowPath) {
            PointF pointf = new PointF();
            pointf.set(mRobotPoint);
            mRobotPath.add(pointf);
        }
        postInvalidate();
    }

    public void  setLocalPath(ArrayList<PointF> pointFS){
        mRobotPath = pointFS;
    }
    
    public PointF getRobotPosition() {
        PointF point = new PointF();
        point.set(mRobotPoint);
        return point;
    }

    public ArrayList<PointF> saveRobotPath() {
        return mRobotPath;
    }

    public void exitRobotState() {
        isShowRobotState = false;
        isShowRobotPath = false;
        mRobotDirection = 0F;
        postInvalidate();
    }


    @NonNull
    private float[] getInvertPoints(float[] points) {
        Matrix invertMatrix = new Matrix();
        mCurrentMatrix.invert(invertMatrix);
        invertMatrix.mapPoints(points);
        return points;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(measureSize(widthMeasureSpec, true), measureSize(heightMeasureSpec, false));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        viewWidth = getWidth();
        viewHeight = getHeight();
        //set centor point
        mCentorX = viewWidth / 2;
        mCentorY = viewHeight / 2;
        //set rect position

        //get rect position
        if (mBitmap != null) {
            float xScale = ((float) viewWidth) / mBitmap.getWidth();
            float yScale = ((float) viewHeight) / mBitmap.getHeight();
            float scale = Math.min(xScale, yScale);
            mCurrentMatrix.setScale(scale, scale);
            if (scale == xScale) {
                initMapToCenterOffset = (((float) viewHeight) - mBitmap.getHeight() * scale) / 2;
                mCurrentMatrix.postTranslate(0, initMapToCenterOffset);
            } else {
                initMapToCenterOffset = (((float) viewWidth) - mBitmap.getWidth() * scale) / 2;
                mCurrentMatrix.postTranslate(initMapToCenterOffset, 0);
            }
        }
        //set robot image
        if (mRobotImage == null) {
            mRobotImage = BitmapFactory.decodeResource(getResources(), R.mipmap.robot_pose);
        }

        scaleRobotImage();
        scaleChargeImage();
    }

    private void scaleRobotImage() {
        float scaleWidth = mRobotImageWidth / mRobotImage.getWidth();
        float scaleHeight = mRobotImageHeight / mRobotImage.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        mRobotImage = Bitmap.createBitmap(mRobotImage, 0, 0, mRobotImage.getWidth(), mRobotImage.getHeight(), matrix, true);
    }

    private void scaleChargeImage() {
//        float scaleWidth = mRobotImageWidth / mChargeImage.getWidth();
//        float scaleHeight = mRobotImageHeight / mChargeImage.getHeight();
//        Matrix matrix = new Matrix();
//        matrix.postScale(scaleWidth, scaleHeight);
//        mChargeImage = Bitmap.createBitmap(mChargeImage, 0, 0, mChargeImage.getWidth(), mChargeImage.getHeight(), matrix, true);
    }

    public int measureSize(int measureSpec, boolean isWidth) {
        int viewMode = MeasureSpec.getMode(measureSpec);
        int viewSize = MeasureSpec.getSize(measureSpec);
        if (viewMode == MeasureSpec.AT_MOST) {
            //wrap_content
            if (mBitmap != null) {
                viewWidth = mBitmap.getWidth();
                viewHeight = mBitmap.getHeight();
                return MeasureSpec.makeMeasureSpec(isWidth ? mBitmap.getWidth() : mBitmap.getHeight(), viewMode);
            }
        }
        if (isWidth) {
            viewWidth = viewSize;
        } else {
            viewHeight = viewSize;
        }
        return measureSpec;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.v("MapView", "onDraw");
        super.onDraw(canvas);
        if (mBitmap != null) {
            //画布使用透明填充
            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawBitmap(mBitmap, mCurrentMatrix, null);
        }

        if (isAddPathState) {
            // draw record paths
            float[] transPoint = new float[]{0, 0};
            Path path = new Path();
            for (int i = 0; i < pathPointList.size(); i++) {
                PointF point = pathPointList.get(i);
                transPoint[0] = point.x;
                transPoint[1] = point.y;
                mCurrentMatrix.mapPoints(transPoint);
                if (i == 0) {
                    path.moveTo(transPoint[0], transPoint[1]);
                } else {
                    path.lineTo(transPoint[0], transPoint[1]);
                }
                canvas.drawCircle(transPoint[0], transPoint[1], 5, mAddPathPointPaint);
            }
            if (pathPointList.size() > 0) {
                path.lineTo(mCentorX, mCentorY);
            }
            canvas.drawPath(path, mAddPathPaint);
            canvas.drawCircle(mCentorX, mCentorY, 8, mAddPathStartPointPaint);
        }

        performShowDBPath(canvas);

        if (isVirtualWallState) {
            // 画已保存段
            for (int i = 0; i < mVirtualWallPaths.size(); i++) {
                Path path = new Path(mVirtualWallPaths.get(i));
                path.transform(mCurrentMatrix);
                canvas.drawPath(path, mVirtualWallPathPaint);
            }

            if (mVirtualWallPath != null) {
                Path path = new Path(mVirtualWallPath);
                path.transform(mCurrentMatrix);
                canvas.drawPath(path, mVirtualWallPathPaint);

                //画未保存的线段
                float[] transPoint = new float[]{0, 0};
                transPoint[0] = mVirtualWallEndPoint.x;
                transPoint[1] = mVirtualWallEndPoint.y;
                mCurrentMatrix.mapPoints(transPoint);
                canvas.drawLine(transPoint[0], transPoint[1], mCentorX, mCentorY, mVirtualWallUnsavePath);
            }
            canvas.drawCircle(mCentorX, mCentorY, 8, mVirtualWallPathStartPointPaint);
        }


        if (isShowRobotPath) {
            Path path = new Path();
            for (int i = 0; i < mRobotPath.size(); i++) {
                PointF pointf = mRobotPath.get(i);
                float[] mappedPoints = new float[]{(float) RosData.getPixelXY( pointf.x,pointf.y).x, (float) RosData.getPixelXY(pointf.x, pointf.y).y};
                mCurrentMatrix.mapPoints(mappedPoints);
                if (i == 0) {
                    path.moveTo(mappedPoints[0], mappedPoints[1]);
                } else {
                    path.lineTo(mappedPoints[0], mappedPoints[1]);
                }
            }
            canvas.drawPath(path, mRobotPathPaint);
//            float[] point = new float[]{mRobotPoint.x, mRobotPoint.y};
//            Matrix rotationMatrix = new Matrix();
//            rotationMatrix.setRotate(mRobotDirection);
//            mCurrentMatrix.mapPoints(point);
//            Bitmap robotImage = Bitmap.createBitmap(mRobotImage, 0, 0, mRobotImage.getWidth(), mRobotImage.getHeight(), rotationMatrix, true);
//            canvas.drawBitmap(robotImage, point[0] - mRobotImage.getWidth() / 2, point[1] - mRobotImage.getHeight() / 2, null);
        }

        if (pose != null) {
            @SuppressLint("DrawAllocation")
            float[] Points = new float[]{(float) RosData.getPixelXY(pose.x, pose.y).x, (float) RosData.getPixelXY(pose.x, pose.y).y};
            mCurrentMatrix.mapPoints(Points);
//            Log.d("CeshiTAG", "位置画布" + Points[0] + "===" + Points[1]);
            @SuppressLint("DrawAllocation") Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawCircle(Points[0], Points[1], 10, paint);
//            canvas.drawBitmap(mRobotImage, Points[0], Points[1], wallpaint);
        }

        if (pointsArray != null) {
            Paint paint = new Paint();

            for (Point point : pointsArray) {
                if (point != null) {
                    float[] mappedPoints = new float[]{(float) RosData.getPixelXY(point.x, point.y).x, (float) RosData.getPixelXY(point.x, point.y).y};
                    mCurrentMatrix.mapPoints(mappedPoints);
                    // 绘制红色小圆
                    paint.setColor(Color.RED);
                    canvas.drawCircle(mappedPoints[0], mappedPoints[1], 5, paint);
                }
            }

//            Pose2D savedPose = SharedPreferencesUtil.Companion.getObject(RCApplication.getContext(), "charge_poi"+RosData.currentMapID, Pose2D.class);
//            if (savedPose != null) {
//                float[] mappedPoints = new float[]{(float) RosData.getPixelXY(savedPose.x, savedPose.y).x, (float) RosData.getPixelXY(savedPose.x, savedPose.y).y};
//                mCurrentMatrix.mapPoints(mappedPoints);
//                paint.setColor(Color.RED);
//                paint.setColor(Color.YELLOW);
//                Bitmap robotImage = Bitmap.createBitmap(mChargeImage, 0, 0, mChargeImage.getWidth(), mChargeImage.getHeight(), mCurrentMatrix, true);
//                canvas.drawBitmap(robotImage, mappedPoints[0] - mChargeImage.getWidth() / 2, mappedPoints[1] - mChargeImage.getHeight() / 2, null);
//
//
//
//            }
        }

        if (upLocation) {
            // 绘制红色小圆
            @SuppressLint("DrawAllocation") Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            canvas.drawCircle((float) onClickPoint.x, (float) onClickPoint.y, 10, paint);
        }


        if (isShowWall) {
            // 获取单个地图的多个点列表
            Map<String, List<Point>> mapPoints = PointDataManager.INSTANCE.getPoints(getContext(), RosData.currentMapName);
            for (Map.Entry<String, List<Point>> entry : mapPoints.entrySet()) {
                String id = entry.getKey();
                List<Point> pointList = entry.getValue();
//            System.out.println("Points ID: " + id);
                for (int i = 0; i < pointList.size(); i++) {
                    Point point = pointList.get(i);
                    float[] transPoint = new float[]{(float) point.x, (float) point.y};
                    mCurrentMatrix.mapPoints(transPoint);
                    if (i > 0) {
                        Point prevPoint = pointList.get(i - 1);
                        float[] prevTransPoint = new float[]{(float) prevPoint.x, (float) prevPoint.y};
                        mCurrentMatrix.mapPoints(prevTransPoint);
                        canvas.drawLine(prevTransPoint[0], prevTransPoint[1], transPoint[0], transPoint[1], wallpaint);
                    }
                }
            }
        }


    }

    private int mTouchMode = NOTHING_MODE;
    private static final int NOTHING_MODE = 0;//啥也不干
    private static final int MOVE_MODE = 1;//拖动
    private static final int SCALE_MODE = 2;//放大
    private static final int RECT_MODE = 3;//移动矩形

    private PointF moveFromPoint = new PointF();
    private PointF scaleFromPoint0 = new PointF();
    private PointF scaleFromPoint1 = new PointF();

    private void setScaleFromPoint(MotionEvent event) {
        scaleFromPoint0.set(event.getX(0), event.getY(0));
        scaleFromPoint1.set(event.getX(1), event.getY(1));
    }

    public boolean onTouchEvent(MotionEvent event) {
        //Log.v("MapView", "onTouchEvent");
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (isSetGoal) {
                    float[] xy = {event.getX(), event.getY()};
                    Matrix matrixInvert = new Matrix();
                    mCurrentMatrix.invert(matrixInvert);
                    matrixInvert.mapPoints(xy);
                    dataProcess.moveGoalSend(xy[0], xy[1]);
                }
                mTouchMode = MOVE_MODE;
                moveFromPoint.set(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == MOVE_MODE) {
                    float dx = event.getX() - moveFromPoint.x;
                    float dy = event.getY() - moveFromPoint.y;
                    moveFromPoint.set(event.getX(), event.getY());
                    mCurrentMatrix.postTranslate(dx, dy);

                } else if (mTouchMode == SCALE_MODE) {
                    Matrix scaleMatrix = new Matrix();
                    float[] pointSrc = {scaleFromPoint0.x, scaleFromPoint0.y, scaleFromPoint1.x, scaleFromPoint1.y};
                    float[] pointDest = {event.getX(0), event.getY(0), event.getX(1), event.getY(1)};
                    scaleMatrix.setPolyToPoly(pointSrc, 0, pointDest, 0, 2);
                    mCurrentMatrix.postConcat(scaleMatrix);
                    setScaleFromPoint(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                mTouchMode = NOTHING_MODE;
                if (upLocation) {
                    onClickPoint.x = event.getX();
                    onClickPoint.y = event.getY();

                    float[] xy = {event.getX(), event.getY()};
                    Matrix matrixInvert = new Matrix();
                    mCurrentMatrix.invert(matrixInvert);
                    matrixInvert.mapPoints(xy);
                    onClickPointPost.x = xy[0];
                    onClickPointPost.y = xy[1];
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = NOTHING_MODE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchMode = SCALE_MODE;
                setScaleFromPoint(event);
                break;
        }
        invalidate();
        return true;
    }

    private float getDistance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        float result = (float) Math.sqrt(x * x + y * y);
        return result;
    }

    public enum updateMapID {
        RUNNING, SCANNING,
    }
}

