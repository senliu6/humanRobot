package com.shciri.rosapp.ui.set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.mydata.DBUtils;
import com.shciri.rosapp.server.ServerInfoTab;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemSetFragment extends Fragment {

    private List<SystemSetAdapter.SystemSetList> systemSetListList;
    private ListView listView;
    private SystemSetAdapter systemSetAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_system_set, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        systemSetListList = new ArrayList<>();
        systemSetListList.add(new SystemSetAdapter.SystemSetList("清除任务历史报告数据"));
        systemSetListList.add(new SystemSetAdapter.SystemSetList("WIFI连接"));
        systemSetListList.add(new SystemSetAdapter.SystemSetList("设置机器人IP"));
        systemSetAdapter = new SystemSetAdapter(getContext(), systemSetListList);
        listView = view.findViewById(R.id.system_set_lv);
        listView.setAdapter(systemSetAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        deleteTaskHistoryAll();
                        break;
                    case 1:
                        //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
                        //注意是这个：WifiManager.ACTION_PICK_WIFI_NETWORK
                        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
                        intent.putExtra("only_access_points", true);
                        intent.putExtra("extra_prefs_show_button_bar", true);
                        intent.putExtra("wifi_enable_next_on_connect", true);
                        startActivityForResult(intent, 1);
                        break;

                    case 2:
                        //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
                        //注意是这个：WifiManager.ACTION_PICK_WIFI_NETWORK
                        showInputDialog();
                        break;
                }
            }
        });
    }

    private void deleteTaskHistoryAll() {
        RCApplication.db.execSQL("delete from task_history");
    }
    private void showInputDialog() {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        EditText editText = new EditText(getContext());
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(getContext());
        inputDialog.setTitle("请输入机器人IP").setView(editText);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String rosIp = editText.getText().toString();
                Pattern pattern = Pattern.compile(regex);    // 编译正则表达式
                Matcher matcher = pattern.matcher(rosIp);
                boolean bool = matcher.matches();
                if(bool) {   // 如果验证通过
                    Log.i("正确",""+rosIp);
                    DBUtils.getInstance().DBInsertInfo(ServerInfoTab.id, rosIp);
                    Toast.makeText(getContext(), "IP设置成功 "+rosIp, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("错误",""+rosIp);
                    showErrorDialog();
                }
            }
        });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        inputDialog.show();
    }
    private void showErrorDialog() {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        EditText editText = new EditText(getContext());
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(getContext());
        inputDialog.setTitle("输入错误，请输入机器人IP").setView(editText);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String rosIp = editText.getText().toString();
                Pattern pattern = Pattern.compile(regex);    // 编译正则表达式
                Matcher matcher = pattern.matcher(rosIp);
                boolean bool = matcher.matches();
                if(bool) {   // 如果验证通过
                    Log.i("正确",""+rosIp);
                    DBUtils.getInstance().DBInsertInfo(ServerInfoTab.id, rosIp);
                    Toast.makeText(getContext(), "IP设置成功 "+rosIp, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("错误",""+rosIp);
                    showErrorDialog();
                }
            }
        });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        inputDialog.show();
    }
}
