package com.chinadaily.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.umeng.analytics.MobclickAgent


abstract class BaseAppActivity : AppCompatActivity() {

    public override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    public override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        statusBarDarkMode()
    }

    private fun statusBarDarkMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            this.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            this.window?.statusBarColor = Color.TRANSPARENT
        } else {
            try {
                miUiSetStatusBarLightMode(false)
                flyMeSetStatusBarLightMode(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Throws
    private fun flyMeSetStatusBarLightMode(dark: Boolean): Boolean {
        var result = false
        this.window?.let {
            val lp = window.attributes
            val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            window.attributes = lp
            result = true
        }
        return result
    }

    @SuppressLint("PrivateApi")
    @Throws
    private fun miUiSetStatusBarLightMode(dark: Boolean): Boolean {
        var result = false
        this.window?.let {
            val clazz = window.javaClass
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            if (dark) {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag)
            }
            result = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (dark) {
                    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        }
        return result
    }
}