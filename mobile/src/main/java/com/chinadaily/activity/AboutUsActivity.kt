package com.chinadaily.activity

import android.os.Bundle
import com.chinadaily.R
import com.chinadaily.adapter.TF2
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.base.setTabBarTop
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.base_title_layout.*

/**
 * Created by Staff on 2017/3/20.
 */

class AboutUsActivity : BaseAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        tabBar.setTabBarTop()
        baseBack.setOnClickListener { finish() }
        baseTitle.text = "About us"
        tv3_content2.typeface = TF2
        tv3_content1.typeface = TF2
        tv2_content4.typeface = TF2
        tv2_content3.typeface = TF2
        tv2_content2.typeface = TF2
        tv2_content1.typeface = TF2
        tv1_content4.typeface = TF2
        tv1_content3.typeface = TF2
        tv1_content2.typeface = TF2
        tv1_content1.typeface = TF2
        tv3_top.typeface = TF2
        tv2_top.typeface = TF2
        tv1_top.typeface = TF2
    }


}
