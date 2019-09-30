package com.chinadaily.base

import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.chinadaily.R
import com.chinadaily.utils.topHeight
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_base.*


abstract class BaseAppActivity : AppCompatActivity() {

    override fun onResume() {
        MobclickAgent.onResume(this)
        super.onResume()
    }

    override fun onPause() {
        MobclickAgent.onPause(this)
        super.onPause()
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(R.layout.activity_base)
        contentView.removeAllViews()
        val view = View.inflate(this, layoutResID, null)
        contentView.addView(view)
    }

}

fun View.setTabBarTop() {
    if (layoutParams.height > 0) {
        layoutParams.height = layoutParams.height + topHeight()
        Log.e("zuiweng", topHeight().toString())
        Log.e("zuiweng", layoutParams.height.toString())
    }
    setPadding(paddingLeft, paddingTop + topHeight(), paddingRight, paddingBottom)
    layoutParams = layoutParams
}

fun View.addPaddingTop() {
    setPadding(paddingLeft, paddingTop + topHeight(), paddingRight, paddingBottom)
}