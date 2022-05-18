package com.shciri.rosapp.ui.myview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shciri.rosapp.R;

import java.util.ArrayList;

public class MapView extends View {

    private Bitmap mBitmap = null;
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

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setBitmap(Bitmap map, int mapId) {
        this.mBitmap = map;
        mCurrentMatrix = new Matrix();
        if (getWidth() != 0 && getHeight() != 0) {
            mCurrentMatrix.setScale(((float) mMapWidth) / mBitmap.getWidth(), (((float) mMapHeight) / mBitmap.getHeight()));
        }
        postInvalidate();
    }

    public void reset() {
        mCurrentMatrix.setScale(((float) mMapWidth) / mBitmap.getWidth(), ((float) mMapHeight) / mBitmap.getHeight());
        postInvalidate();
    }

    public void SetRobotBitmap(Bitmap robot) {
        this.mRobotImage = robot;
        scaleRobotImage();
        postInvalidate();
    }

    /**
     * 添加路径
     **/
    ArrayList<PointF> pathPoints = new ArrayList<>();

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
        pathPoints.add(new PointF(transPoints[0], transPoints[1]));
        postInvalidate();
    }

    public ArrayList<PointF> savePath() {
        return pathPoints;
    }

    public void exitAddPathState() {
        isAddPathState = false;
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

    public PointF getRobotPosition(){
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

    public void startRect() {
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

    public void exitAllState() {
        isShowRectState = false;
        isShowRobotState = false;
        isAddPathState = false;
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
        Paint paint = new Paint();
        super.onDraw(canvas);
        if (mBitmap != null) {
            //画布使用透明填充
            canvas.drawColor(Color.TRANSPARENT);
            canvas.drawBitmap(mBitmap, mCurrentMatrix, null);
        }
        if (isAddPathState) {
            // draw record paths
            paint.setColor(Color.BLUE);
            float[] transPoint = new float[]{0, 0};
            Path path = new Path();
            for (int i = 0; i < pathPoints.size(); i++) {
                PointF point = pathPoints.get(i);
                transPoint[0] = point.x;
                transPoint[1] = point.y;
                mCurrentMatrix.mapPoints(transPoint);
                if (i == 0) {
                    path.moveTo(transPoint[0], transPoint[1]);
                } else {
                    path.lineTo(transPoint[0], transPoint[1]);
                }
                canvas.drawCircle(transPoint[0], transPoint[1], 5, paint);
            }
            Paint pathPaint = new Paint();
            pathPaint.setColor(Color.YELLOW);
            pathPaint.setAntiAlias(true);
            pathPaint.setStyle(Paint.Style.STROKE);
            pathPaint.setStrokeWidth(2);
            if (pathPoints.size() > 0) {
                path.lineTo(mCentorX, mCentorY);
            }
            canvas.drawPath(path, pathPaint);
            paint.setColor(Color.RED);
            canvas.drawCircle(mCentorX, mCentorY, 8, paint);
        }

        if (isShowRectState) {
            Paint rectPaint = new Paint();
            rectPaint.setAntiAlias(true);
            rectPaint.setStrokeWidth(2);
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setColor(Color.YELLOW);

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
                canvas.drawPath(path, rectPaint);
            }

            // draw current rect
            rectPaint.setColor(Color.RED);
            canvas.drawRect(mRect, rectPaint);
            rectPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mRect.right, mRect.bottom, 12, rectPaint);
        }

        if (isShowRobotPath) {
            if(isShowRobotPath){
                Path path = new Path();
                float[] transPoint = new float[2];
                for(int i = 0; i < mRobotPath.size(); i++){
                    PointF pointf = mRobotPath.get(i);
                    transPoint[0] = pointf.x;
                    transPoint[1] = pointf.y;
                    mCurrentMatrix.mapPoints(transPoint);
                    if(i == 0){
                        path.moveTo(transPoint[0], transPoint[1]);
                    }else{
                        path.lineTo(transPoint[0], transPoint[1]);
                    }
                }
                Paint pathPaint = new Paint();
                pathPaint.setColor(Color.GREEN);
                pathPaint.setAntiAlias(true);
                pathPaint.setStyle(Paint.Style.STROKE);
                pathPaint.setStrokeCap(Paint.Cap.ROUND);
                pathPaint.setStrokeJoin(Paint.Join.ROUND);
                pathPaint.setStrokeWidth(8);
                canvas.drawPath(path, pathPaint);
            }
            float[] point = new float[]{mRobotPoint.x, mRobotPoint.y};
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.setRotate(mRobotDirection);
            mCurrentMatrix.mapPoints(point);
            Bitmap robotImage = Bitmap.createBitmap(mRobotImage, 0, 0, mRobotImage.getWidth(), mRobotImage.getHeight(),rotationMatrix,true);
            canvas.drawBitmap(robotImage, point[0] - mRobotImage.getWidth()/2, point[1] - mRobotImage.getHeight()/2, null);
        }
    }

    private int mode = NOTHING_MODE;
    private static final int NOTHING_MODE = 0;//啥也不干
    private static final int DRAG_MODE = 1;//拖动
    private static final int ZOOM_MODE = 2;//放大
    private static final int RECT_MODE = 3;//移动矩形
    private PointF moveFromPoint = new PointF();

    private PointF scaleFromPoint0 = new PointF();
    private PointF scaleFromPoint1 = new PointF();

    private void setScaleFromPoint(MotionEvent event) {
        scaleFromPoint0.set(event.getX(0), event.getY(0));
        scaleFromPoint1.set(event.getX(1), event.getY(1));
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (isShowRectState && 30 > getDistance(event.getX(), event.getY(), mRect.right, mRect.bottom)) {
                    mode = RECT_MODE;
                } else {
                    mode = DRAG_MODE;
                    moveFromPoint.set(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG_MODE) {
                    float dx = event.getX() - moveFromPoint.x;
                    float dy = event.getY() - moveFromPoint.y;
                    moveFromPoint.set(event.getX(), event.getY());
                    mCurrentMatrix.postTranslate(dx, dy);

                } else if (mode == ZOOM_MODE) {
                    Matrix scaleMatrix = new Matrix();
                    float[] pointSrc = {scaleFromPoint0.x, scaleFromPoint0.y, scaleFromPoint1.x, scaleFromPoint1.y};
                    float[] pointDest = {event.getX(0), event.getY(0), event.getX(1), event.getY(1)};
                    scaleMatrix.setPolyToPoly(pointSrc, 0, pointDest, 0, 2);
                    mCurrentMatrix.postConcat(scaleMatrix);
                    setScaleFromPoint(event);
                } else if (mode == RECT_MODE) {
                    mRect.right = event.getX();
                    mRect.bottom = event.getY();
                }
                break;

            case MotionEvent.ACTION_UP:
                mode = NOTHING_MODE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NOTHING_MODE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM_MODE;
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
