package com.shciri.rosapp.myfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.databinding.FragmentNetworkBinding;
import com.shciri.rosapp.dmros.data.UserList;

/**
 * @author asus
 */
public class NetworkFragment extends Fragment {

    private FragmentNetworkBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNetworkBinding.inflate(inflater, container, false);
        initView();
        initData();
        return binding.getRoot();

    }

    private void initData() {
    }

    private void initView() {
        //点击测试按钮
        binding.tvTest.setOnClickListener(v -> {
            if (UserList.INSTANCE.getArray()[0].equals(RCApplication.Operator)) {
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_clipControlFragment);
            } else {
                Toaster.showShort(getString(R.string.no_permission));
            }

        });
        //点击返回
        binding.returnLl.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        //点击设置IP
        binding.tvNetwork.setOnClickListener(v -> {
            if (UserList.INSTANCE.getArray()[0].equals(RCApplication.Operator)) {
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_clipFragment);
            } else {
                Toaster.showShort(getString(R.string.no_permission));
            }
        });

    }

}
