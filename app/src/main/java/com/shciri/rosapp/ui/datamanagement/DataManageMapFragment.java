package com.shciri.rosapp.ui.datamanagement;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.BitmapUtils;
import com.shciri.rosapp.dmros.tool.ControlMapEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.mydata.DBUtils;
import com.shciri.rosapp.ui.myview.MapView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import src.com.jilk.ros.message.StartMapping;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataManageMapFragment extends Fragment {

    private OnBackPressedCallback mBackPressedCallback;
    private MapView mapView;
    ArrayList<Path> mVirtualWallPaths = new ArrayList<>();
    SwipeMenuListView swipeMenuListView;
    private MapAdapter mapAdapter;

    private TextView scanBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_data_manage_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapView);
        if(RosData.rosBitmap != null){
            mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        }else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
            mapView.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        swipeMenuListView = view.findViewById(R.id.map_manage_swipeList);
        mapTitleListInit();

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        view.findViewById(R.id.startEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.startEraseState();
            }
        });
        view.findViewById(R.id.undoEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.undoErase();
            }
        });
        view.findViewById(R.id.saveEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.saveErasedMap();
            }
        });
        view.findViewById(R.id.endEraseMapTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.exitWithSaveEraseState();
            }
        });

        //虚拟墙
        view.findViewById(R.id.startVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.startVirtualWallState(mVirtualWallPaths);
            }
        });
        view.findViewById(R.id.saveVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.saveVirtualWallPathPoints();
                mVirtualWallPaths.addAll(mapView.getVirtualWallPaths());
            }
        });
        view.findViewById(R.id.exitVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.exitVirtualWallState();
            }
        });
        view.findViewById(R.id.addVirtualWallTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.addVirtualWallPathPoint();
            }
        });

        scanBtn = view.findViewById(R.id.scan_new_map);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RosTopic.startMappingsTopic != null) {
                    if (v.isActivated()) {
                        StartMapping startMapping = new StartMapping();
                        startMapping.control = 2;
                        RosTopic.startMappingsTopic.publish(startMapping);
                        MapView.scanning = false;
                        //在API29及之后是不需要申请的，默认是允许的
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                        } else {
                            //保存图片到相册
                            showInputDialog();
                        }
                        scanBtn.setText("+扫描地图");
                        v.setActivated(false);
                    } else {
                        StartMapping startMapping = new StartMapping();
                        startMapping.control = 1;
                        RosTopic.startMappingsTopic.publish(startMapping);
                        MapView.scanning = true;
                        scanBtn.setText("保存地图");
                        v.setActivated(true);
                    }
                }else {
                    Toast.makeText(getContext(), "离线模式或服务未开启，请退回登录界面重试！", Toast.LENGTH_SHORT).show();
                }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                showInputDialog();
            }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //保存图片到相册
                showInputDialog();
            } else {
                Toast.makeText(getContext(), "你拒绝了该权限，无法保存地图！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showInputDialog() {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(getContext());
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(getContext());
        inputDialog.setTitle("请为地图命名").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),
                                editText.getText().toString(),
                                Toast.LENGTH_SHORT).show();
                        String MD5 = BitmapUtils.saveImage(editText.getText().toString(), RosData.dataBaseMaxMapID+1, mapView.mBitmap);
                        if(MD5.equals(""))
                            return;
                        DBInsertMap(editText.getText().toString(), MD5);
                        EventBus.getDefault().post(new ControlMapEvent("addMap",editText.getText().toString(),RosData.dataBaseMaxMapID+1));
                    }
                }).show();
    }

    private void DBInsertMap(String name, String md5) {
        ContentValues values = new ContentValues();
        values.put("name",name);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        values.put("time",simpleDateFormat.format(date));
        values.put("width",mapView.mBitmap.getWidth());
        values.put("height",mapView.mBitmap.getHeight());
        values.put("md5",md5);
        RCApplication.db.insert("map",null,values);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if("/map".equals(event.getMessage())) {
            if(RosData.rosBitmap != null){
                mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
            }else {
                Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
                mapView.setBitmap(map, MapView.updateMapID.RUNNING);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        PublishEvent.readyPublish = true;
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        PublishEvent.readyPublish = false;
        super.onStop();
    }

//    private void addNewMapList(String name) {
//        MapAdapter.MapList data = new MapAdapter.MapList(name);
//        mapLists.add(data);
//    }

    private void mapTitleListInit() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        swipeMenuListView.setMenuCreator(creator);
        mapAdapter = new MapAdapter(getContext());
        swipeMenuListView.setAdapter(mapAdapter);
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        // delete
                        if(BitmapUtils.deleteMap(mapAdapter.getItem(position).name, mapAdapter.getItem(position).id)) {
                            DBUtils.getInstance().deleteMap(mapAdapter.getItem(position).id);
                            DBUtils.getInstance().deletePathOfMapID(mapAdapter.getItem(position).id);
                            mapAdapter.removeItem(position);
                        }
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mapAdapter.getCount() != 0) {
//                    currentPosition = position;
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath()+
                            "/RobotLocalMap"+
                            "/"+mapAdapter.getItem(position).name+"_1"+".png");
                    mapView.setBitmap(bitmap, MapView.updateMapID.RUNNING);
                }
            }
        });
    }

    // 将dp转换为px
    private int dp2px(int value) {
        // 第一个参数为我们待转的数据的单位，此处为 dp（dip）
        // 第二个参数为我们待转的数据的值的大小
        // 第三个参数为此次转换使用的显示量度（Metrics），它提供屏幕显示密度（density）和缩放信息
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }
    //另一种将dp转换为px的方法
    private int dp2px(float value){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(value*scale + 0.5f);
    }
}