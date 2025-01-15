package com.shciri.rosapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.shciri.rosapp.R;

public class AirStatusView extends ConstraintLayout {

    View view;
    TextView titleView;
    TextView valueView;
    TextView unitView;

    public AirStatusView(Context context) {
        super(context);
    }

    public AirStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs, context);
    }

    public AirStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, context);
    }

    private void initView(AttributeSet attrs, Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.air_status_view, this, true);
        titleView = view.findViewById(R.id.air_view_title);
        valueView = view.findViewById(R.id.air_view_value);
        unitView = view.findViewById(R.id.unit_view);

        if (attrs != null){
            //获取AttributeSet中所有的XML属性的数量
            int count = attrs.getAttributeCount();
            //遍历AttributeSet中的XML属性
            for (int i = 0; i < count; i++){
                //获取attr的资源ID
                int attrResId = attrs.getAttributeNameResource(i);
                switch (attrResId){
                    case R.attr.titleText:
                        titleView.setText(attrs.getAttributeValue(i));
                        break;

                    case R.attr.unitText:
                        unitView.setText(attrs.getAttributeValue(i));
                        break;
                }
            }
        }
    }

    public void setValueView(String value) {
        valueView.setText(value);
    }

    public void setTitleView(String value) {
        titleView.setText(value);
    }
}

