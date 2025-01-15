package com.shciri.rosapp.ui.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.MainActivity;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.base.BaseFragment;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.LanguageType;
import com.shciri.rosapp.dmros.data.Settings;
import com.shciri.rosapp.dmros.tool.VersionUpdateManager;
import com.shciri.rosapp.ui.dialog.BaseAttrDialog;
import com.shciri.rosapp.ui.dialog.InputDialog;
import com.shciri.rosapp.ui.dialog.TestDialog;
import com.shciri.rosapp.utils.LanguageUtil;
import com.shciri.rosapp.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import src.com.jilk.ros.message.StateMachineRequest;

public class SystemSetFragment extends BaseFragment {

    private List<SystemSetAdapter.SystemSetList> systemSetListList;
    private ListView listView;
    private SystemSetAdapter systemSetAdapter;
    private VersionUpdateManager versionUpdateManager;


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

        /*初始化检查更新manager*/
        versionUpdateManager = new VersionUpdateManager(getContext(), new VersionUpdateManager.versionUpdateListener() {
            @Override
            public void haveNoInstallPermission(Intent intent, int requestCode) {
                getActivity().startActivityForResult(intent, requestCode);
            }

            @Override
            public void alreadyLastVersion() {
                Toaster.showShort("已是最新版本");
            }

            @Override
            public void clickCancel() {
                Log.d("VersionUpdateManager", "点击取消");
            }
        });
        versionUpdateManager.queryAPKInfo();
        assert getContext() != null;


        systemSetListList = new ArrayList<>();
        systemSetListList.add(new SystemSetAdapter.SystemSetList(getString(R.string.set_language)));
        systemSetAdapter = new SystemSetAdapter(getContext(), systemSetListList);
        listView = view.findViewById(R.id.system_set_lv);
        listView.setAdapter(systemSetAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0://多语言设置
                    String language = SharedPreferencesUtil.Companion.getValue(requireContext(), Settings.LANGUAGE, LanguageType.CHINESE.getLanguage(), String.class);
                    BaseAttrDialog testDialog = new TestDialog.Builder(getActivity())
                            .setLanguage(language)
                            .setGravity(Gravity.CENTER)
                            .setListener(language1 -> {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    LanguageUtil.INSTANCE.changeAppLanguage(requireActivity().getApplicationContext(), language1);
                                }
                                SharedPreferencesUtil.Companion.saveValue(requireActivity().getApplicationContext(), Settings.LANGUAGE, language1);
                                Intent intent1 = new Intent(getActivity(), MainActivity.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent1);

                            }).show();
                    break;
                default:
            }
        });
    }

    private ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // 处理返回结果
                    Intent data = result.getData();
                    // 处理Intent数据
                }
            });


    // 打开文件管理器选择文件
    private void openFileManager() {
        // 打开文件管理器选择文件
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType(“image/*”);//选择图片
        //intent.setType(“audio/*”); //选择音频
        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType(“video/*;image/*”);//同时选择视频和图片
        intent.setType("*/*");//无类型限制
        intent.putExtra("only_access_points", true);
        intent.putExtra("extra_prefs_show_button_bar", true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "需要选择文件"), 1);
    }

    private void deleteTaskHistoryAll() {
        RCApplication.db.execSQL("delete from task_history");
    }
}
