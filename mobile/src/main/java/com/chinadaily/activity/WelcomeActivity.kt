package com.chinadaily.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.chinadaily.R
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.utils.SPUtils
import com.chinadaily.utils.initDisplayCutout


class WelcomeActivity : BaseAppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        window.decorView.initDisplayCutout {
            if (SPUtils.getBoolean("isFirstIn", true)) {
                window.decorView.postDelayed({ goHome() }, 2000)
            } else {
                SPUtils.put("isFirstIn", false)
                window.decorView.postDelayed({ goGuide() }, 2000)
            }
        }

    }

    private fun goHome() {
        startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("chinadailyhk://newsDetail?newsId= 222")
        })
        finish()
    }

    private fun goGuide() {
        startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("chinadailyhk://newsDetail?newsId= 222")
        })
        finish()
    }

    override fun onBackPressed() {

    }
}
