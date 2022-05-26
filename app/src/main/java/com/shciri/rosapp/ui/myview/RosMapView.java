package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import android.view.View;
import android.widget.ImageView;
import com.rey.material.widget.RelativeLayout;
import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;

public class RosMapView extends RelativeLayout {

    PhotoView headerView;
    ImageView localView;

    public RosMapView(Context context) {
        super(context);
        initView(context);
    }

    public RosMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RosMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //初始化UI，可根据业务需求设置默认值。
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_map_ros, this, true);
        headerView = (PhotoView) findViewById(R.id.photo_view);
        localView = (ImageView) findViewById(R.id.local_view);

        headerView.setOnClickListener(new PhotoView.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.out.println("Click");
            }
        });

        headerView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                System.out.println("x:= " + x + "  y:= " + y);
                x -= RosData.MapData.poseX;
                y -= RosData.MapData.poseY;
                System.out.println("x:= " + x + "  y:= " + y);
                x *= 0.05F;
                y *= 0.05F;
                System.out.println("dstX =" + x + "  dstY =" + y);
                RosData.moveGoal.header.frame_id = "map";
                RosData.moveGoal.pose.position.x = x;
                RosData.moveGoal.pose.position.y = y;
                RosData.moveGoal.pose.orientation.z = 0.98f;
                RosData.moveGoal.pose.orientation.w = -0.019f;
                RosTopic.goalTopic.publish(RosData.moveGoal);
            }
        });
    }

    public void setHeaderView(Bitmap bitmap) {
        headerView.setImageBitmap(bitmap);
    }

    public void moveLocalView(float x, float y) {
        localView.setX(x);
        localView.setY(y);
    }
}
