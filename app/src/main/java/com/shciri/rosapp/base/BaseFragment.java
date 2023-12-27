package com.shciri.rosapp.base;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hjq.toast.Toaster;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2023年12月19日
 */
public class BaseFragment extends Fragment {
    private Context mcontext;

    @Override
    public void onAttach(@NonNull Context context) {
        mcontext = context;
        super.onAttach(context);
    }

    public void toast(int id) {
        Toaster.show(mcontext.getResources().getString(id));
    }
}
