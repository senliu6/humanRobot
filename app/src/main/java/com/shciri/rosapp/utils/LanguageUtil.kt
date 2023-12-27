package com.shciri.rosapp.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import com.shciri.rosapp.dmros.data.LanguageType
import java.util.Locale

/**
 * 功能：多语言切换功能
 * @author ：liudz
 * 日期：2023年12月19日
 */
object LanguageUtil {

    /**
     * @param context
     * @param newLanguage 想要切换的语言类型 比如 "en" ,"zh","zh-tw"
     */
    @Suppress("deprecation")
    fun changeAppLanguage(context: Context, newLanguage: String?) {
        //Logger.e("设置成："+newLanguage);
        if (TextUtils.isEmpty(newLanguage)) {
            return
        }
        val resources = context.resources
        val configuration = resources.configuration
        //获取想要切换的语言类型
        val locale: Locale = newLanguage?.let { getLocaleByLanguage(it) }!!
        configuration.setLocale(locale)
        // updateConfiguration
        val dm = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }

    fun getLocaleByLanguage(language: String): Locale? {
        var locale = Locale.SIMPLIFIED_CHINESE
        when (language) {
            LanguageType.CHINESE.getLanguage() -> {
                locale = Locale.SIMPLIFIED_CHINESE
            }
            LanguageType.ENGLISH.getLanguage() -> {
                locale = Locale.ENGLISH
            }
            LanguageType.FAN.getLanguage() -> {
                locale = Locale.TRADITIONAL_CHINESE
            }
        }
        return locale
    }

    fun attachBaseContext(context: Context, language: String): Context? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            context
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context? {
        val resources = context.resources
        val locale = getLocaleByLanguage(language)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLocales(LocaleList(locale))
        return context.createConfigurationContext(configuration)
    }
}