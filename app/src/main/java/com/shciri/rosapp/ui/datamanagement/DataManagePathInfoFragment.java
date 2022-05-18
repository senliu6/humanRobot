package com.shciri.rosapp.ui.datamanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shciri.rosapp.MyPGM;
import com.shciri.rosapp.R;
import com.shciri.rosapp.data.RosData;
import com.shciri.rosapp.ui.myview.MapView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataManagePathInfoFragment extends Fragment {

    private OnBackPressedCallback mBackPressedCallback;
    private MapView mMapView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_data_manage_path_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mMapView = view.findViewById(R.id.ros_map);
//        Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.map_example);
//        mMapView.setBitmap(map, 12);

//        MyPGM pgm = new MyPGM();
//        int[] pix;
//        pix = pgm.readData(RosData.map.info.width, RosData.map.info.height, 5, RosData.map.data, RosData.MapData.poseX, RosData.MapData.poseY);   //P5-Gray image
//        Bitmap bitmap = Bitmap.createBitmap(RosData.map.info.width, RosData.map.info.height, Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pix, 0, RosData.map.info.width, 0, 0, RosData.map.info.width, RosData.map.info.height);
//        Matrix invert = new Matrix();
//        invert.setScale(1, -1); //镜像翻转以与真实地图对应
//        Bitmap rosBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), invert ,true);
//        mMapView.setBitmap(rosBitmap, 12);

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        view.findViewById(R.id.add_path_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.startPathState();
            }
        });

        view.findViewById(R.id.add_rect_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.startRect();
            }
        });

        view.findViewById(R.id.record_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMapView.isAddPathState()) {
                    mMapView.addPathPoint();
                }else if(mMapView.isRectState()){
                    mMapView.addRect();
                }
            }
        });

        view.findViewById(R.id.exit_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.exitAllState();
            }
        });

        view.findViewById(R.id.reset_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.reset();
            }
        });
    }
}