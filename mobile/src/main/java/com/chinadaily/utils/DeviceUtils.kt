package com.chinadaily.utils

import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import com.chinadaily.base.BaseApp

/**
 * 获取设备的一些信息
 */


/**
 * dp 转化为 px
 *
 * @param dpValue dpValue
 * @return int
 */
fun dp2px(dpValue: Float): Int {
    val scale = BaseApp.getInstance().resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}


/**
 * px 转化为 dp
 *
 * @param pxValue pxValue
 */
fun px2dp(pxValue: Float): Int {
    val scale = BaseApp.getInstance().resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}


/**
 * 获取设备宽度（px）
 *
 * @return int
 */

fun screenWidth(): Int {
    return BaseApp.getInstance().resources.displayMetrics.widthPixels
}

fun topHeight(): Int {
    return if (displayHeight > 0) {
        displayHeight
    } else {
        statusHeight()
    }
}

fun statusHeight(): Int {
    return try {
        val resources = Resources.getSystem()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId == -1) {
            dp2px(24f)
        } else {
            val px = resources.getDimensionPixelSize(resourceId)
            if (px < 0) {
                dp2px(24f)
            } else {
                px
            }
        }
    } catch (e: Exception) {
        print(e)
        dp2px(24f)
    }
}

private var displayHeight: Int = -1
fun View.initDisplayCutout(onComplete: () -> Unit) {
    var isFirst = true
    when {
        RomUtils.isHuawei() -> displayHeight = getHuaWeiDisplayHeight()
        RomUtils.isVivo() -> displayHeight = getViVoDisplayHeight()
        RomUtils.isOppo() -> displayHeight = getOPPODisplayHeight()
        RomUtils.isXiaomi() -> displayHeight = getXiaomiDisplayHeight()
    }
    post {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            displayHeight = rootWindowInsets?.displayCutout?.safeInsetTop ?: 0
        }
        if (isFirst) {
            Log.e("zuiweng2", displayHeight.toString())
            Log.e("zuiweng2", statusHeight().toString())
            onComplete.invoke()
            isFirst = false
        }
    }
}

//获取刘海尺寸：width、height
//int[0]值为刘海宽度 int[1]值为刘海高度
fun View.getHuaWeiDisplayHeight(): Int {
    var ret = intArrayOf(0, 0)
    try {
        val cl = context.classLoader
        val clazz = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
        val get = clazz.getMethod("getNotchSize")
        ret = get.invoke(clazz) as IntArray
    } catch (e: Exception) {
        Log.e("Notch", "getNotchSizeAtHuawei ClassNotFoundException")
    } finally {
        return ret[1]
    }
}


fun View.getViVoDisplayHeight(): Int {
    var ret = false
    try {
        val cl = context.classLoader
        val clazz = cl.loadClass("android.util.FtFeature")
        val method = clazz.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
        ret = method.invoke(clazz, 0x00000020) as Boolean
    } catch (e: Exception) {
        Log.e("Notch", "hasNotchAtVoio ClassNotFoundException")
    }
    return if (ret) {
        dp2px(27f)
    } else {
        0
    }
}

fun View.getOPPODisplayHeight(): Int {
    return if (context.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")) {
        80
    } else {
        0
    }
}

fun View.getXiaomiDisplayHeight(): Int {
    val resourceId = resources.getIdentifier("notch_height", "dimen", "android")
    return if (resourceId > 0) {
        BaseApp.getInstance().resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

fun navBarHeight(): Int {
    return try {
        val resources = Resources.getSystem()
        resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))
    } catch (e: Exception) {
        0
    }
}

fun Window.setStatusBarLightMode(isLightMode: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val decorView = this.decorView
        val vis = if (isLightMode) {
            decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        decorView.systemUiVisibility = vis
    }
}
