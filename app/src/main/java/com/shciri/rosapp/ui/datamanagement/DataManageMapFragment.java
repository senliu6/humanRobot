package com.shciri.rosapp.ui.datamanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shciri.rosapp.R;
import com.shciri.rosapp.ui.myview.MapView;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataManageMapFragment extends Fragment {

    private OnBackPressedCallback mBackPressedCallback;
    private MapView mMapView;
    ArrayList<Path> mVirtualWallPaths = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_data_manage_map, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMapView = view.findViewById(R.id.mapView);
        mMapView = view.findViewById(R.id.mapView);
        Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.map_example);
        mMapView.setBitmap(map, 12);

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        view.findViewById(R.id.startEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.startEraseState();
            }
        });
        view.findViewById(R.id.undoEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.undoErase();
            }
        });
        view.findViewById(R.id.saveEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.saveErasedMap();
            }
        });
        view.findViewById(R.id.endEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.exitWithSaveEraseState();
            }
        });

        //虚拟墙
        view.findViewById(R.id.startVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.startVirtualWallState(mVirtualWallPaths);
            }
        });
        view.findViewById(R.id.saveVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.saveVirtualWallPathPoints();
                mVirtualWallPaths.addAll(mMapView.getVirtualWallPaths());
            }
        });
        view.findViewById(R.id.exitVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.exitVirtualWallState();
            }
        });
        view.findViewById(R.id.addVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.addVirtualWallPathPoint();
            }
        });
    }
}