package com.shciri.rosapp.ui.datamanagement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.BitmapUtils;
import com.shciri.rosapp.dmros.tool.ControlMapEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.control.ManageTaskDB;
import com.shciri.rosapp.ui.myview.MapView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import src.com.jilk.ros.message.PoseStamped;
import src.com.jilk.ros.rosbridge.implementation.JSON;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataManagePathInfoFragment extends Fragment {

    private OnBackPressedCallback mBackPressedCallback;
    private MapView mapView;
    private SwipeMenuListView swipeMenuListView;
    private List<PathListTitle> pathListData;
    private AppAdapter pathListAdapter;
    private ArrayList<String> pointString;
    private Gson gson = new Gson();
    java.lang.reflect.Type type = new TypeToken<ArrayList<PointF>>(){}.getType();

    private TextView recordBtn;
    private TextView saveBtn;
    private TextView resetBtn;
    private TextView generateBtn;

    private int currentPosition;
    private int removePosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        PublishEvent.readyPublish = true;
        return inflater.inflate(R.layout.fragment_data_manage_path_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeMenuListView = view.findViewById(R.id.map_manage_swipeList);
        pathTitleListInit();


        mapView = view.findViewById(R.id.mapView);
        if(RosData.rosBitmap != null){
            mapView.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING);
        }else {
            Bitmap map = BitmapFactory.decodeResource(getResources(), R.drawable.map_example);
            mapView.setBitmap(map, MapView.updateMapID.RUNNING);
        }

        queryManualPath();

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        view.findViewById(R.id.add_path_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setShowDBPath(false);
                mapView.startPathState();
                recordBtn.setText("落点");
                generateBtn.setVisibility(View.INVISIBLE);
                recordBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                resetBtn.setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.add_rect_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setShowDBPath(false);
                mapView.startRectState();
                recordBtn.setText("预览");
                generateBtn.setVisibility(View.INVISIBLE);
                recordBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                resetBtn.setVisibility(View.VISIBLE);
            }
        });

        recordBtn = view.findViewById(R.id.record_bt);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapView.isAddPathState()) {
                    mapView.addPathPoint();
                }else if(mapView.isRectState()){
                    mapView.addRect();
                    if(RosTopic.coveragePointsTopic != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RosTopic.coveragePointsTopic.publish(RosData.coveragePoints);
                            }
                        }).start();
                    }
                }
            }
        });

        saveBtn = view.findViewById(R.id.save_bt);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapView.isShowCoveragePath && mapView.coveragePath.isEmpty())
                    Toast.makeText(getContext(), "请先预览确认再保存",Toast.LENGTH_SHORT).show();
                else
                    showInputDialog();
            }
        });

        generateBtn = view.findViewById(R.id.generate_task_bt);
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogForGenerateTask();
            }
        });

        if(pathListAdapter.getCount() == 0)
            generateBtn.setVisibility(View.INVISIBLE);

        resetBtn = view.findViewById(R.id.reset_bt);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.reset();
            }
        });
    }

    private void showInputDialogForGenerateTask() {
        EditText editText = new EditText(getContext());
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(getContext());
        inputDialog.setTitle("请输入任务名").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pathID = gson.toJson(pathListAdapter.getItem(currentPosition).ID);
                        insertTask(editText.getText().toString(), pathID);
                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        inputDialog.show();
    }

    private void showInputDialog() {
        EditText editText = new EditText(getContext());
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(getContext());
        inputDialog.setTitle("请输入路径名").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int tmpID = 0;
                        if(mapView.isShowCoveragePath) {
                            String point = gson.toJson(mapView.coveragePath);
                            tmpID = (int)insertManualPath(editText.getText().toString(), point);
                            pointString.add(point);
                        }else if(mapView.isAddPathState()) {
                            String point = gson.toJson(mapView.pathPointList);
                            tmpID = (int)insertManualPath(editText.getText().toString(), point);
                            pointString.add(point);
                        }
                        PathListTitle data = new PathListTitle(editText.getText().toString(), tmpID);
                        pathListData.add(data);
                        mapView.exitAllState();
                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        inputDialog.show();
    }

    private long insertManualPath(String name, String point) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("map_id", RosData.currentMapID);
        values.put("point",point);
        return RCApplication.db.insert("manual_path", null, values);
    }

    private void queryManualPath(){
        //查询全部数据
        Cursor cursor = RCApplication.db.query("manual_path",null, "map_id=?", new String[]{Integer.toString(RosData.currentMapID)}, null, null, null);

        pointString = new ArrayList<>();
        if(cursor.getCount() > 0)
        {
            while(cursor.moveToNext()){
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String point = cursor.getString(cursor.getColumnIndex("point"));
                pointString.add(point);
                pathListData.add(new PathListTitle(name, id));
            }
            mapView.DBPathPointList = gson.fromJson(pointString.get(0), type);
            mapView.setShowDBPath(true);
        }
        cursor.close();
    }

    private void insertTask(String name, String pathID) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("map_id", RosData.currentMapID);
        values.put("path_id",pathID);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        values.put("date_created",simpleDateFormat.format(date));
        RCApplication.db.insert("task", null, values);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PublishEvent event) {
        if("/coverage_path".equals(event.getMessage())) {
            mapView.clearCoveragePath();
            for(PoseStamped point : RosData.coveragePath.poses) {
                plotPath((int)(point.pose.position.x/0.05f), (int)(point.pose.position.y/0.05f));
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PublishEvent.readyPublish = false;
    }


    private void plotPath(int x, int y) {
        int tX = RosData.MapData.poseX + x;
        int tY = RosData.MapData.poseY + y;

        mapView.setCoveragePath(tX, RosData.map.info.height - tY, true);
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
        swipeMenuListView.setMenuCreator(creator);
        pathListAdapter = new AppAdapter();
        swipeMenuListView.setAdapter(pathListAdapter);
        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(pathListAdapter.getCount() != 0) {
                   generateBtn.setVisibility(View.VISIBLE);
                   currentPosition = position;
                   mapView.DBPathPointList = gson.fromJson(pointString.get(position), type);
                   mapView.setShowDBPath(true);
                   recordBtn.findViewById(R.id.record_bt).setVisibility(View.INVISIBLE);
                   saveBtn.findViewById(R.id.save_bt).setVisibility(View.INVISIBLE);
                   resetBtn.findViewById(R.id.reset_bt).setVisibility(View.INVISIBLE);
               }
           }
        });

        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // rename
                        break;
                    case 1:
                        PathListTitle pathListTitle = pathListData.get(position);
                        if(pathListTitle.ID == 0) {
                            RCApplication.db.delete("manual_path","name=?", new String[]{pathListTitle.pathName});
                        }else {
                            RCApplication.db.delete("manual_path","id=?", new String[]{Integer.toString(pathListTitle.ID)});
                        }
                        removePosition = position;
                        pathListData.remove(position);
                        pathListAdapter.notifyDataSetChanged();
                        showDeleteDialog(pathListTitle);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void showDeleteDialog(PathListTitle pathListTitle) {
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getContext());
        normalDialog.setIcon(R.drawable.logo);
        normalDialog.setTitle("删除路径");
        normalDialog.setMessage("删除路径数据的同时也会将使用此路径的任务删除");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (ManageTaskDB.TaskList x: ManageTaskDB.taskLists){
                            if(x.pathID == pathListTitle.ID) {
                                RCApplication.db.delete("task","path_id=?", new String[]{Integer.toString(pathListTitle.ID)});
                            }
                        }
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return pathListData.size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if(pathListData.size() == 0) {
                mapView.DBPathPointList.clear();
                mapView.reset();
            } else {
                if(removePosition == 0) {
                    mapView.DBPathPointList = gson.fromJson(pointString.get(removePosition + 1), type);
                    mapView.setShowDBPath(true);
                    currentPosition = removePosition;
                    mapView.reset();
                }else{
                    mapView.DBPathPointList = gson.fromJson(pointString.get(removePosition - 1), type);
                    mapView.setShowDBPath(true);
                    currentPosition = removePosition - 1;
                    mapView.reset();
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
            }else{
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

        public PathListTitle(String name, int id){
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
    private int dp2px(float value){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(value*scale + 0.5f);
    }
}