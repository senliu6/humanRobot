package com.shciri.rosapp.ui.dialog;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.hjq.toast.Toaster;

/**
 *    time   : 2023/10/24
 *    desc   : DialogFragment 基类
 * @author liudz
 */
public final class BuildDialog {

    public static class Builder<B extends BuildDialog.Builder>
            extends BaseDialogFragment.Builder<B> {

        public Builder(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public B setContentView(@NonNull View view) {
            // 使用 ButterKnife 注解
            return super.setContentView(view);
        }

        /**
         * 显示吐司
         */
        public void toast(CharSequence text) {
            Toaster.show(text);
        }

        public void toast(@StringRes int id) {
            Toaster.show(id);
        }

        public void toast(Object object) {
            Toaster.show(object);
        }
    }
}