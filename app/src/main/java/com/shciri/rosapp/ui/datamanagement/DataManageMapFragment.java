package com.shciri.rosapp.ui.datamanagement;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.databinding.FragmentDataManageMapBinding;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.ReceiveHandler;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.view.MapView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import src.com.jilk.ros.message.StateMachineRequest;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataManageMapFragment extends Fragment {

    private OnBackPressedCallback mBackPressedCallback;
    ArrayList<Path> mVirtualWallPaths = new ArrayList<>();
    private MapAdapter mapAdapter;
    private FragmentDataManageMapBinding binding;

    /**
     * 操作模式 1：修图 2：虚拟墙
     */
    private int controlMode = 0;
    //修图
    private int MODE_ERASE_MAP = 1;
    //虚拟墙
    private int MODE_VIRTUAL_WALL = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDataManageMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (RosData.rosBitmap != null) {
            binding.mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        } else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
            binding.mapView.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        initView();

//        mapTitleListInit();

    }

    private void initView() {
        //点击返回
        binding.returnLl.setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());

        //点击开始虚拟墙
        binding.startVirtualWallTv.setOnClickListener(v -> {
            binding.mapView.startVirtualWallState(mVirtualWallPaths);
            controlMode = MODE_VIRTUAL_WALL;
            binding.btnBack.setText(R.string.add_point);
        });

        //点击退出
        binding.btnQuit.setOnClickListener(v -> {
           if (controlMode == MODE_VIRTUAL_WALL) {
                binding.mapView.exitVirtualWallState();
            } else {
                Toaster.showShort(getString(R.string.not_select_control));
            }
            controlMode = 0;
        });
        //点击保存
        binding.btnSave.setOnClickListener(v -> {
           if (controlMode == MODE_VIRTUAL_WALL) {
                //虚拟墙
//                binding.mapView.saveVirtualWallPathPoints();
                mVirtualWallPaths.addAll(binding.mapView.getVirtualWallPaths());
            } else {
                Toaster.showShort(getString(R.string.not_select_control));
            }
        });
        //点击撤销或添加点
        binding.btnBack.setOnClickListener(v -> {
           if (controlMode == 2) {
                binding.mapView.addVirtualWallPathPoint();
            } else {
                Toaster.showShort(getString(R.string.not_select_control));
            }
        });

        //点击扫描建图
        binding.scanNewMap.setOnClickListener(v -> {
            RosTopic rosTopic = new RosTopic();
            ReceiveHandler receiveHandler = new ReceiveHandler();
            rosTopic.subscribeMapTopic(receiveHandler.getMapTopicHandler(), ((RCApplication) requireActivity().getApplication()).getRosClient());
            StateMachineRequest stateMachineRequest = new StateMachineRequest();
            stateMachineRequest.map_control = 3;
            RosTopic.publishStateMachineRequest(stateMachineRequest);
            MapView.scanning = true;
            Bundle bundle = new Bundle();
            bundle.putInt("state", 1);
            Navigation.findNavController(v).navigate(R.id.collectionFragment, bundle);
        });

        if (MapAdapter.mapLists.size() > 0) {
            setMapInfomation(0);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if ("/map".equals(event.getMessage())) {
            if (RosData.rosBitmap != null) {
                binding.mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
            } else {
                Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
                binding.mapView.setBitmap(map, MapView.updateMapID.RUNNING);
            }
        }
    }

    ;

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

//    private void mapTitleListInit() {
//
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//            @Override
//            public void create(SwipeMenu menu) {
//                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(getContext());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                        0xCE)));
//                // set item width
//                openItem.setWidth(dp2px(90));
//                // set item title
//                openItem.setTitle("Open");
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);
//
//                // create "delete" item
//                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                        0x3F, 0x25)));
//                // set item width
//                deleteItem.setWidth(dp2px(90));
//                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//            }
//        };
//        binding.mapManageSwipeList.setMenuCreator(creator);
//
//        mapAdapter = new MapAdapter(getContext());
//        binding.mapManageSwipeList.setAdapter(mapAdapter);
//        binding.mapManageSwipeList.setOnMenuItemClickListener((position, menu, index) -> {
//            switch (index) {
//                case 0:
//                    // open
//                    break;
//                case 1:
//                    // delete 删除单个地图
//                    if (BitmapUtils.deleteMap(mapAdapter.getItem(position).name, mapAdapter.getItem(position).id)) {
//                        DBUtils.getInstance().deleteMap(mapAdapter.getItem(position).id);
//                        DBUtils.getInstance().deletePathOfMapID(mapAdapter.getItem(position).id);
//                        mapAdapter.removeItem(position);
//                        mapAdapter.notifyDataSetChanged();
//                    }
//                    RequestMapControlParameter requestMapControlParameter = new RequestMapControlParameter();
//                    requestMapControlParameter.map_id = mapAdapter.getItem(position).name;
//                    RosTopic.publishControlParameterTopic(requestMapControlParameter);
//
//                    StateMachineRequest request = new StateMachineRequest();
//                    request.map_control = 6;
//                    RosTopic.publishStateMachineRequest(request);
//
//                    break;
//                default:
//            }
//            // false : close the menu; true : not close the menu
//            return false;
//        });
//
//        binding.mapManageSwipeList.setOnItemClickListener((parent, view, position, id) -> {
//            if (mapAdapter.getCount() != 0) {
////                    切换地图
//                Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory("Pictures").getAbsolutePath() +
//                        "/RobotLocalMap" +
//                        "/" + mapAdapter.getItem(position).name + "_1" + ".png");
//                binding.mapView.setBitmap(bitmap, MapView.updateMapID.RUNNING);
//
//                RequestMapControlParameter parameter = new RequestMapControlParameter();
//                parameter.map_id = mapAdapter.getItem(position).name;
//                RosTopic.publishControlParameterTopic(parameter);
//
//                StateMachineRequest stateMachineRequest = new StateMachineRequest();
//                stateMachineRequest.map_control = 2;
//                RosTopic.publishStateMachineRequest(stateMachineRequest);
//                setMapInfomation(position);
//            }
//        });
//    }

    // 将dp转换为px
    private int dp2px(int value) {
        // 第一个参数为我们待转的数据的单位，此处为 dp（dip）
        // 第二个参数为我们待转的数据的值的大小
        // 第三个参数为此次转换使用的显示量度（Metrics），它提供屏幕显示密度（density）和缩放信息
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getResources().getDisplayMetrics());
    }

    //另一种将dp转换为px的方法
    private int dp2px(float value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    @SuppressLint("SetTextI18n")
    private void setMapInfomation(int position) {
        binding.textView26.setText(MapAdapter.mapLists.get(position).name);
        binding.mapCreateTimeTv.setText(MapAdapter.mapLists.get(position).name);
        binding.mapSizeTv.setText(MapAdapter.mapLists.get(position).width + "*" + MapAdapter.mapLists.get(0).width);
    }
}