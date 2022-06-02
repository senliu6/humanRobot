package com.shciri.rosapp.ui.myview;


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
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.DataProcess;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.MyPGM;

import java.util.ArrayList;

public class MapView extends View {

    private Bitmap mBitmap = null;
    private Bitmap mOriginBitmap = null;
    private int mMapWidth, mMapHeight;
    private Matrix mCurrentMatrix = new Matrix();

    //robot
    private Bitmap mRobotImage = null;
    float mRobotImageWidth = 30;
    float mRobotImageHeight = 30;

    boolean isAddPathState = false;
    float mCentorX = 0.0F;
    float mCentorY = 0.0F;

    boolean isShowRobotState = false;
    boolean isShowRobotPath = false;
    float mRobotDirection = 0F;
    PointF mRobotPoint = new PointF();
    ArrayList<PointF> mRobotPath = new ArrayList<>();

    public boolean isSetGoal = false;

    DataProcess dataProcess = new DataProcess();

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

    Paint mErasePaint;
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
        //擦除功能
        mErasePaint = new Paint();
        mErasePaint.setAntiAlias(true);
        mErasePaint.setStrokeJoin(Paint.Join.ROUND);
        mErasePaint.setStrokeCap(Paint.Cap.ROUND);
        mErasePaint.setStyle(Paint.Style.STROKE);
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mErasePaint.setStrokeWidth(mEraseRadius * 2);

        mErasePointPaint = new Paint();
        mErasePointPaint.setColor(Color.RED);
        mErasePointPaint.setAntiAlias(true);
        mErasePointPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 1));
        mErasePointPaint.setStyle(Paint.Style.STROKE);
        mErasePointPaint.setStrokeCap(Paint.Cap.ROUND);
        mErasePointPaint.setStrokeWidth(3);

        //添加路径功能
        mAddPathPointPaint = new Paint();
        mAddPathPointPaint.setColor(Color.BLUE);
        mAddPathStartPointPaint = new Paint();
        mAddPathStartPointPaint.setColor(Color.RED);

        mAddPathPaint = new Paint();
        mAddPathPaint.setColor(Color.YELLOW);
        mAddPathPaint.setAntiAlias(true);
        mAddPathPaint.setStyle(Paint.Style.STROKE);
        mAddPathPaint.setStrokeWidth(2);

        //选择区域功能
        mShowRectPaint = new Paint();
        mShowRectPaint.setAntiAlias(true);
        mShowRectPaint.setStrokeWidth(2);
        mShowRectPaint.setStyle(Paint.Style.STROKE);
        mShowRectPaint.setColor(Color.RED);

        mShowOldRectPaint = new Paint();
        mShowOldRectPaint.setAntiAlias(true);
        mShowOldRectPaint.setStrokeWidth(2);
        mShowOldRectPaint.setStyle(Paint.Style.STROKE);
        mShowOldRectPaint.setColor(Color.YELLOW);

        mShowRectPointPaint = new Paint();
        mShowRectPointPaint.setAntiAlias(true);
        mShowRectPointPaint.setStrokeWidth(2);
        mShowRectPointPaint.setStyle(Paint.Style.STROKE);
        mShowRectPointPaint.setColor(Color.RED);
        mShowRectPointPaint.setStyle(Paint.Style.FILL);

        //机器人路径
        mRobotPathPaint = new Paint();
        mRobotPathPaint.setColor(Color.GREEN);
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
    }

    /**
     * 通用接口
     **/
    public void setBitmap(Bitmap map, int mapId) {
        this.mBitmap = map.copy(Bitmap.Config.ARGB_8888, true);
        mCurrentMatrix = new Matrix();
        if (getWidth() != 0 && getHeight() != 0) {
            mCurrentMatrix.setScale(((float) mMapWidth) / mBitmap.getWidth(), (((float) mMapHeight) / mBitmap.getHeight()));
        }
        postInvalidate();
    }

    public void reset() {
        mCurrentMatrix.setScale(((float) mMapWidth) / mBitmap.getWidth(), ((float) mMapHeight) / mBitmap.getHeight());
        exitWithoutSaveEraseState();
        postInvalidate();
    }

    public void SetRobotBitmap(Bitmap robot) {
        this.mRobotImage = robot;
        scaleRobotImage();
        postInvalidate();
    }

    public void exitAllState() {
        isShowRectState = false;
        isShowRobotState = false;
        isAddPathState = false;
        exitWithoutSaveEraseState();
        postInvalidate();
    }

    /**
     * 添加路径
     **/
    ArrayList<PointF> mAddPathPoints = new ArrayList<>();

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
        mAddPathPoints.add(new PointF(transPoints[0], transPoints[1]));
        postInvalidate();
    }

    public ArrayList<PointF> savePath() {
        return mAddPathPoints;
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
        postInvalidate();
    }

    public void saveVirtualWallPathPoints() {
        if (mVirtualWallPath != null) {
            mVirtualWallPaths.add(mVirtualWallPath);
            mVirtualWallPath = null;
            mVirtualWallEndPoint = null;
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

    public void
    setRobotPosition(float x, float y, float direction, boolean isShowPath) {
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

    /**
     * 添加矩形
     **/
    private boolean isShowRectState = false;
    private RectF mRect = null;
    private ArrayList<float[]> mRecordRectPoints = new ArrayList<>();

    public void startRectState() {
        exitAllState();
        isShowRectState = true;
        postInvalidate();
    }

    public boolean isRectState() {
        return isShowRectState;
    }

    public void exitRectState() {
        isShowRectState = false;
        postInvalidate();
    }

    public void addRect() {
        float[] transPoints = new float[]{mRect.left, mRect.top, mRect.right, mRect.top, mRect.left, mRect.bottom, mRect.right, mRect.bottom};
        transPoints = getInvertPoints(transPoints);
        mRecordRectPoints.add(transPoints);
    }

    public ArrayList<float[]> saveRects() {
        return mRecordRectPoints;
    }

    /**
     * 擦除功能
     */
    private PorterDuffXfermode mPorterDuffXfermode;
    boolean isEraseState = false;
    Path mErasePath = null;
    PointF mErasePoint = null;
    float mEraseRadius = 24.0f;
    ArrayList<Path> mErasePaths = new ArrayList<>();

    public void startEraseState() {
        mOriginBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        exitAllState();
        isEraseState = true;
        postInvalidate();
    }

    public boolean isEraseState() {
        return isEraseState;
    }

    public Bitmap saveErasedMap() {
        if (!isEraseState) {
            return null;
        }
        Bitmap erasedMap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Matrix invert = new Matrix();
        mCurrentMatrix.invert(invert);
        Canvas canvas = new Canvas(erasedMap);
        canvas.setMatrix(invert);
        if (!mErasePaths.isEmpty()) {
            for (Path path : mErasePaths) {
                canvas.drawPath(path, mErasePaint);
            }
        }
        if (mErasePath != null) {
            canvas.drawPath(mErasePath, mErasePaint);
        }
        mErasePaths.clear();
        //当前显示擦除的页面
        mBitmap = erasedMap;
        postInvalidate();
        return erasedMap;
    }

    public void undoErase() {
        if (!isEraseState) {
            return;
        }
        if (mErasePaths.size() > 0) {
            mErasePaths.remove(mErasePaths.size() - 1);
        }
        postInvalidate();
    }

    // 退出擦除模式化后，显示擦除的界面
    public void exitWithSaveEraseState() {
        if (!isEraseState) {
            return;
        }
        saveErasedMap();
        isEraseState = false;
        mOriginBitmap = null;
        mErasePaths.clear();
        mErasePath = null;
        postInvalidate();
    }

    public void exitWithoutSaveEraseState() {
        if (!isEraseState) {
            return;
        }
        mBitmap = mOriginBitmap;
        mOriginBitmap = null;
        mErasePaths.clear();
        isEraseState = false;
        mErasePath = null;
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
        mMapWidth = getWidth();
        mMapHeight = getHeight();
        //set centor point
        mCentorX = mMapWidth / 2;
        mCentorY = mMapHeight / 2;
        //set rect position
        mRect = new RectF(mMapWidth / 3, mMapHeight / 3, 2 * mMapWidth / 3, 2 * mMapHeight / 3);

        //get rect position
        if (mBitmap != null) {
            mCurrentMatrix.setScale(((float) mMapWidth) / mBitmap.getWidth(), ((float) mMapHeight) / mBitmap.getHeight());
        }
        //set robot image
        if (mRobotImage == null) {
            mRobotImage = BitmapFactory.decodeResource(getResources(), R.drawable.zhixiang);
        }
        scaleRobotImage();
    }

    private void scaleRobotImage() {
        float scaleWidth = mRobotImageWidth / mRobotImage.getWidth();
        float scaleHeight = mRobotImageHeight / mRobotImage.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        mRobotImage = Bitmap.createBitmap(mRobotImage, 0, 0, mRobotImage.getWidth(), mRobotImage.getHeight(), matrix, true);
    }

    public int measureSize(int measureSpec, boolean isWidth) {
        int viewMode = MeasureSpec.getMode(measureSpec);
        int viewSize = MeasureSpec.getSize(measureSpec);
        if (viewMode == MeasureSpec.AT_MOST) {
            //wrap_content
            if (mBitmap != null) {
                mMapWidth = mBitmap.getWidth();
                mMapHeight = mBitmap.getHeight();
                return MeasureSpec.makeMeasureSpec(isWidth ? mBitmap.getWidth() : mBitmap.getHeight(), viewMode);
            }
        }
        if (isWidth) {
            mMapWidth = viewSize;
        } else {
            mMapHeight = viewSize;
        }
        return measureSpec;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.v("MapView", "onDraw");
        super.onDraw(canvas);
        if (mBitmap != null) {
            //画布使用透明填充
            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawBitmap(mBitmap, mCurrentMatrix, null);
        }

        if (isEraseState) {
            // 每次在canvas上画线，方便撤销步骤
            if (!mErasePaths.isEmpty()) {
                for (Path path : mErasePaths) {
                    canvas.drawPath(path, mErasePaint);
                }
            }
            if (mErasePath != null) {
                canvas.drawPath(mErasePath, mErasePaint);
            }

            if (mErasePoint == null) {
                canvas.drawCircle(mCentorX, mCentorY, mEraseRadius, mErasePointPaint);
            } else {
                canvas.drawCircle(mErasePoint.x, mErasePoint.y, mEraseRadius, mErasePointPaint);
            }
        }

        if (isAddPathState) {
            // draw record paths
            float[] transPoint = new float[]{0, 0};
            Path path = new Path();
            for (int i = 0; i < mAddPathPoints.size(); i++) {
                PointF point = mAddPathPoints.get(i);
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
            if (mAddPathPoints.size() > 0) {
                path.lineTo(mCentorX, mCentorY);
            }
            canvas.drawPath(path, mAddPathPaint);
            canvas.drawCircle(mCentorX, mCentorY, 8, mAddPathStartPointPaint);
        }

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

        if (isShowRectState) {
            // draw record rect
            Path path = new Path();
            for (float[] transPoints : mRecordRectPoints) {
                float[] points = transPoints.clone();
                mCurrentMatrix.mapPoints(points);
                path.moveTo(points[0], points[1]);
                path.lineTo(points[2], points[3]);
                path.lineTo(points[6], points[7]);
                path.lineTo(points[4], points[5]);
                path.lineTo(points[0], points[1]);
                canvas.drawPath(path, mShowOldRectPaint);
            }

            float[] xy = {mRect.left, mRect.top, mRect.right, mRect.top, mRect.right, mRect.bottom, mRect.left, mRect.bottom};
            Matrix matrixInvert = new Matrix();
            mCurrentMatrix.invert(matrixInvert);
            matrixInvert.mapPoints(xy);
            dataProcess.coverageMapProcess((int)xy[1], (int)xy[5], (int)xy[0], (int)xy[2]);

            // draw current rect
            canvas.drawRect(mRect, mShowRectPaint);
            canvas.drawCircle(mRect.right, mRect.bottom, 12, mShowRectPointPaint);
        }

        if (isShowRobotPath) {
            if (isShowRobotPath) {
                Path path = new Path();
                float[] transPoint = new float[2];
                for (int i = 0; i < mRobotPath.size(); i++) {
                    PointF pointf = mRobotPath.get(i);
                    transPoint[0] = pointf.x;
                    transPoint[1] = pointf.y;
                    mCurrentMatrix.mapPoints(transPoint);
                    if (i == 0) {
                        path.moveTo(transPoint[0], transPoint[1]);
                    } else {
                        path.lineTo(transPoint[0], transPoint[1]);
                    }
                }
                canvas.drawPath(path, mRobotPathPaint);
            }
            float[] point = new float[]{mRobotPoint.x, mRobotPoint.y};
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.setRotate(mRobotDirection);
            mCurrentMatrix.mapPoints(point);
            Bitmap robotImage = Bitmap.createBitmap(mRobotImage, 0, 0, mRobotImage.getWidth(), mRobotImage.getHeight(), rotationMatrix, true);
            canvas.drawBitmap(robotImage, point[0] - mRobotImage.getWidth() / 2, point[1] - mRobotImage.getHeight() / 2, null);
        }
    }

    private int mTouchMode = NOTHING_MODE;
    private static final int NOTHING_MODE = 0;//啥也不干
    private static final int MOVE_MODE = 1;//拖动
    private static final int SCALE_MODE = 2;//放大
    private static final int RECT_MODE = 3;//移动矩形
    private static final int ERASE_MODE = 4;//擦除模式
    private PointF moveFromPoint = new PointF();
    private PointF scaleFromPoint0 = new PointF();
    private PointF scaleFromPoint1 = new PointF();

    private void setScaleFromPoint(MotionEvent event) {
        scaleFromPoint0.set(event.getX(0), event.getY(0));
        scaleFromPoint1.set(event.getX(1), event.getY(1));
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.v("MapView", "onTouchEvent");
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(isSetGoal) {
                    float[] xy = {event.getX(), event.getY()};
                    Matrix matrixInvert = new Matrix();
                    mCurrentMatrix.invert(matrixInvert);
                    matrixInvert.mapPoints(xy);
                    dataProcess.moveGoalSend(xy[0], xy[1]);
                }
                if (isShowRectState && 30 > getDistance(event.getX(), event.getY(), mRect.right, mRect.bottom)) {
                    mTouchMode = RECT_MODE;
                } else if (isEraseState) {
                    mTouchMode = ERASE_MODE;
                    mErasePath = new Path();
                    mErasePath.moveTo(event.getX(), event.getY());
                    mErasePoint = new PointF(event.getX(), event.getY());
                } else {
                    mTouchMode = MOVE_MODE;
                    moveFromPoint.set(event.getX(), event.getY());
                }
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
                } else if (mTouchMode == RECT_MODE) {
                    mRect.right = event.getX();
                    mRect.bottom = event.getY();
                } else if (mTouchMode == ERASE_MODE) {
                    mErasePath.lineTo(event.getX(), event.getY());
                    mErasePoint = new PointF(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_UP:
                mTouchMode = NOTHING_MODE;
                if (isEraseState) {
                    mErasePaths.add(mErasePath);
                    mErasePath = null;
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
}
