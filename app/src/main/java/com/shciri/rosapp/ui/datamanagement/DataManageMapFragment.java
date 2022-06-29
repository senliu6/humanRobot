package com.shciri.rosapp.ui.datamanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.shciri.rosapp.R;
import com.shciri.rosapp.mydata.TaskHistoryAdapter;
import com.shciri.rosapp.ui.myview.MapView;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DataManageMapFragment extends Fragment {

    private OnBackPressedCallback mBackPressedCallback;
    private MapView mMapView;
    ArrayList<Path> mVirtualWallPaths = new ArrayList<>();
    SwipeMenuListView swipeMenuListView;
    private List<MapListTitle> mapListData;
    private AppAdapter mAdapter;

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

        view.findViewById(R.id.add_new_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void addNewMapList(String name) {
        MapListTitle data = new MapListTitle(name);
        mapListData.add(data);
    }

    private void mapTitleListInit() {
        mapListData = new ArrayList<MapListTitle>();
        MapListTitle data = new MapListTitle("DAImon");
        mapListData.add(data);

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
        mAdapter = new AppAdapter();
        swipeMenuListView.setAdapter(mAdapter);
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mapListData.size();
        }

        @Override
        public MapListTitle getItem(int position) {
            return mapListData.get(position);
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
            ViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_list_map_title, null);
                vh = new ViewHolder();
                vh.iv_icon = convertView.findViewById(R.id.iv_icon);
                vh.tv_name = convertView.findViewById(R.id.tv_name);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }
            MapListTitle item = getItem(position);
            vh.tv_name.setText(item.mapName);
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
        }
    }

    public class MapListTitle {
        public String mapName;

        public MapListTitle(String name){
            mapName = name;
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