package com.shciri.rosapp.ui.datamanagement;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.base.BaseFragment;
import com.shciri.rosapp.databinding.FragmentDataManagePathInfoBinding;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.control.ManageTaskDB;
import com.shciri.rosapp.ui.dialog.InputDialog;
import com.shciri.rosapp.ui.myview.MapView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import src.com.jilk.ros.message.PoseStamped;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataManagePathInfoFragment extends BaseFragment {

    private OnBackPressedCallback mBackPressedCallback;
    private List<PathListTitle> pathListData;
    private AppAdapter pathListAdapter;
    private ArrayList<String> pointString;
    private Gson gson = new Gson();
    java.lang.reflect.Type type = new TypeToken<ArrayList<PointF>>() {
    }.getType();
    private int currentPosition;
    private int removePosition;
    private FragmentDataManagePathInfoBinding binding;

    private InputDialog pathInputDialog, taskInputDialog, deleteInputDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        PublishEvent.readyPublish = true;
        binding = FragmentDataManagePathInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pathTitleListInit();

        if (RosData.rosBitmap != null) {
            binding.mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        } else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.daimon_map);
            binding.mapView.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        queryManualPath();
        //点击返回
        binding.returnLl.setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());
        //点击手动添加
        binding.addPathBt.setOnClickListener(v -> {
            binding.mapView.setShowDBPath(false);
            binding.mapView.startPathState();
            binding.recordBt.setText("落点");
            binding.generateTaskBt.setVisibility(View.INVISIBLE);
            binding.recordBt.setVisibility(View.VISIBLE);
            binding.saveBt.setVisibility(View.VISIBLE);
            binding.resetBt.setVisibility(View.VISIBLE);
        });
        //点击自动覆盖
        binding.addRectBt.setOnClickListener(v -> {
            binding.mapView.setShowDBPath(false);
            binding.mapView.startRectState();
            binding.recordBt.setText("预览");
            binding.generateTaskBt.setVisibility(View.INVISIBLE);
            binding.recordBt.setVisibility(View.VISIBLE);
            binding.saveBt.setVisibility(View.VISIBLE);
            binding.resetBt.setVisibility(View.VISIBLE);
        });

        //点击落点
        binding.recordBt.setOnClickListener(v -> {
            if (binding.mapView.isAddPathState()) {
                binding.mapView.addPathPoint();
            } else if (binding.mapView.isRectState()) {
                binding.mapView.addRect();
                if (RosTopic.coveragePointsTopic != null) {
                    new Thread(() -> RosTopic.publishCoveragePointsTopic(RosData.coveragePoints)).start();
                }
            }
        });
        //点击保存
        binding.saveBt.setOnClickListener(v -> {
            if (binding.mapView.isShowCoveragePath && binding.mapView.coveragePath.isEmpty()) {
                toast(R.string.please_preview);
            } else {
                showInputDialog();
            }
        });
        //点击生成任务
        binding.generateTaskBt.setOnClickListener(v -> showInputDialogForGenerateTask());

        if (pathListAdapter.getCount() == 0) {
            binding.generateTaskBt.setVisibility(View.INVISIBLE);
        }
        //点击重置
        binding.resetBt.setOnClickListener(v -> binding.mapView.reset());

//        binding.chooseTaskMapNameTv.setText(RosData.);
    }

    private void showInputDialogForGenerateTask() {
        taskInputDialog = new InputDialog.Builder(getActivity())
                .setTitle("请输入任务名")
                .setOnConfirmClick(inputText -> {
                    String pathID = gson.toJson(pathListAdapter.getItem(currentPosition).ID);
                    insertTask(inputText, pathID);
                    taskInputDialog.dismiss();
                })
                .build();
        taskInputDialog.show();
    }

    private void showInputDialog() {
        pathInputDialog = new InputDialog.Builder(getActivity())
                .setTitle("请输入路径名")
                .setOnConfirmClick(inputText -> {
                    int tmpID = 0;
                    if (binding.mapView.isShowCoveragePath) {
                        String point = gson.toJson(binding.mapView.coveragePath);
                        tmpID = (int) insertManualPath(inputText, point);
                        pointString.add(point);
                    } else if (binding.mapView.isAddPathState()) {
                        String point = gson.toJson(binding.mapView.pathPointList);
                        tmpID = (int) insertManualPath(inputText, point);
                        pointString.add(point);
                    }
                    PathListTitle data = new PathListTitle(inputText, tmpID);
                    pathListData.add(data);
                    binding.mapView.exitAllState();
                    pathInputDialog.dismiss();
                })
                .build();
        pathInputDialog.show();
    }

    private long insertManualPath(String name, String point) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("map_id", RosData.currentMapID);
        values.put("point", point);
        return RCApplication.db.insert("manual_path", null, values);
    }

    private void queryManualPath() {
        //查询全部数据
        Cursor cursor = RCApplication.db.query("manual_path", null, "map_id=?", new String[]{Integer.toString(RosData.currentMapID)}, null, null, null);

        pointString = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String point = cursor.getString(cursor.getColumnIndex("point"));
                Log.d("CeshiTAG", "point" + point + "name" + name + "id" + id);
                pointString.add(point);
                pathListData.add(new PathListTitle(name, id));
            }
            binding.mapView.DBPathPointList = gson.fromJson(pointString.get(0), type);
            binding.mapView.setShowDBPath(true);
        }
        cursor.close();
    }

    private void insertTask(String name, String pathID) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("map_id", RosData.currentMapID);
        values.put("path_id", pathID);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        values.put("date_created", simpleDateFormat.format(date));
        RCApplication.db.insert("task", null, values);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if ("/coverage_path".equals(event.getMessage())) {
            binding.mapView.clearCoveragePath();
            for (PoseStamped point : RosData.coveragePath.poses) {
                plotPath((int) (point.pose.position.x / 0.05f), (int) (point.pose.position.y / 0.05f));
            }
        }
    }

    ;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PublishEvent.readyPublish = false;
    }


    private void plotPath(int x, int y) {
        int tX = RosData.MapData.poseX + x;
        int tY = RosData.MapData.poseY + y;

        binding.mapView.setCoveragePath(tX, RosData.map.info.height - tY, true);
    }

    private void pathTitleListInit() {
        pathListData = new ArrayList<PathListTitle>();
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
                openItem.setTitle("Rename");
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
        binding.mapManageSwipeList.setMenuCreator(creator);
        pathListAdapter = new AppAdapter();
        binding.mapManageSwipeList.setAdapter(pathListAdapter);
        binding.mapManageSwipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pathListAdapter.getCount() != 0) {
                    binding.generateTaskBt.setVisibility(View.VISIBLE);
                    currentPosition = position;
                    binding.mapView.DBPathPointList = gson.fromJson(pointString.get(position), type);
                    binding.mapView.setShowDBPath(true);
                    binding.recordBt.findViewById(R.id.record_bt).setVisibility(View.INVISIBLE);
                    binding.saveBt.findViewById(R.id.save_bt).setVisibility(View.INVISIBLE);
                    binding.resetBt.findViewById(R.id.reset_bt).setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.mapManageSwipeList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // rename
                        break;
                    case 1:
                        try {
                            PathListTitle pathListTitle = pathListData.get(position);
                            if (pathListTitle.ID == 0) {
                                RCApplication.db.delete("manual_path", "name=?", new String[]{pathListTitle.pathName});
                            } else {
                                RCApplication.db.delete("manual_path", "id=?", new String[]{Integer.toString(pathListTitle.ID)});
                            }
                            removePosition = position;
                            pathListData.remove(position);
                            pathListAdapter.notifyDataSetChanged();
                            showDeleteDialog(pathListTitle);
                        } catch (Exception e) {
                            Toaster.showShort(e.getMessage());
                        }
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void showDeleteDialog(PathListTitle pathListTitle) {
        deleteInputDialog = new InputDialog.Builder(getActivity())
                .setTitle("删除路径")
                .setContent("删除路径数据的同时也会将使用此路径的任务删除")
                .setEditShow(false)
                .setOnConfirmClick(inputText -> {
                    for (ManageTaskDB.TaskItemList x : ManageTaskDB.taskLists) {
                        if (x.pathID == pathListTitle.ID) {
                            RCApplication.db.delete("task", "path_id=?", new String[]{Integer.toString(pathListTitle.ID)});
                        }
                    }
                    deleteInputDialog.dismiss();
                })
                .build();
        deleteInputDialog.show();
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return pathListData.size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (pathListData.size() == 0) {
                binding.mapView.DBPathPointList.clear();
                binding.mapView.reset();
            } else {
                if (removePosition == 0) {
                    binding.mapView.DBPathPointList = gson.fromJson(pointString.get(removePosition + 1), type);
                    binding.mapView.setShowDBPath(true);
                    currentPosition = removePosition;
                    binding.mapView.reset();
                } else {
                    binding.mapView.DBPathPointList = gson.fromJson(pointString.get(removePosition - 1), type);
                    binding.mapView.setShowDBPath(true);
                    currentPosition = removePosition - 1;
                    binding.mapView.reset();
                }
            }
            pointString.remove(removePosition);
        }

        @Override
        public PathListTitle getItem(int position) {
            return pathListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppAdapter.ViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_list_map_title, null);
                vh = new AppAdapter.ViewHolder();
                vh.iv_icon = convertView.findViewById(R.id.iv_icon);
                vh.tv_name = convertView.findViewById(R.id.tv_name);
                convertView.setTag(vh);
            } else {
                vh = (AppAdapter.ViewHolder) convertView.getTag();
            }
            PathListTitle item = getItem(position);
            vh.tv_name.setText(item.pathName);
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
        }
    }

    private static class PathListTitle {
        public String pathName;
        public int ID;

        public PathListTitle(String name, int id) {
            pathName = name;
            ID = id;
        }
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
    private int dp2px(float value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }
}