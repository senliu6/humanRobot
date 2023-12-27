package com.shciri.rosapp.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.Toaster
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.dmros.data.LanguageType
import com.shciri.rosapp.dmros.data.Settings
import com.shciri.rosapp.utils.LanguageUtil.attachBaseContext
import com.shciri.rosapp.utils.SharedPreferencesUtil.Companion.getValue

/**
 * 功能：
 * @author ：liudz
 * 日期：2023年12月19日
 */
open class BaseActivity : AppCompatActivity() {


    /**
     * 此方法先于 onCreate()方法执行
     *
     * @param newBase 参数
     */
    override fun attachBaseContext(newBase: Context?) {
        //获取我们存储的语言环境 比如 "en","zh",等等
        val language = getValue(
            RCApplication.getContext(),
            Settings.LANGUAGE, LanguageType.CHINESE.getLanguage(), String::class.java
        )
        super.attachBaseContext(attachBaseContext(newBase!!, language))
    }

    fun toastShort(id: Int) {
        Toaster.showShort(resources.getString(id))
    }

    fun toastLong(id: Int) {
        Toaster.showLong(resources.getString(id))
    }

    fun toast(id: Int) {
        Toaster.show(resources.getString(id))
    }

}