package com.shciri.rosapp.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.server.RosServiceCaller;

import java.util.concurrent.ExecutorService;

import src.com.jilk.ros.rosbridge.ROSBridgeClient;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2023年12月19日
 */
public class BaseFragment extends Fragment {
    private Context mcontext;
    public RosServiceCaller rosServiceCaller;

    public ExecutorService executorService;



    @Override
    public void onAttach(@NonNull Context context) {
        mcontext = context;
        super.onAttach(context);


        // 获取RosClient实例
        ROSBridgeClient rosClient = ((RCApplication) requireActivity().getApplication()).getRosClient();

        // 初始化RosServiceCaller
        if (rosClient != null) {
            rosServiceCaller = new RosServiceCaller(rosClient);
        } else {
            // 处理RosClient为空的情况，例如显示错误消息或执行其他操作
            Toaster.show("RosClient is null");
        }

        executorService = RCApplication.getExecutorService();
    }

    public void toast(int id) {
        Toaster.show(mcontext.getResources().getString(id));
    }

    public void back(View view){
        Navigation.findNavController(view).navigateUp();
    }
}
