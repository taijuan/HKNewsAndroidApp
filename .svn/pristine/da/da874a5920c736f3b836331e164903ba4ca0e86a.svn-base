package com.chinadaily.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.chinadaily.R
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.update.checkVersion
import com.chinadaily.utils.DataCleanManager
import com.lzy.okserver.OkDownload
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.base_title_layout.*


class SettingActivity : BaseAppActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        try {
            val size = DataCleanManager.getTotalCacheSize(this@SettingActivity)
            cache_size.text = size
        } catch (e: Exception) {
            e.printStackTrace()
        }
        baseTitle.setText(R.string.my_setting)
        baseBack.setOnClickListener(this)
        clear_cache_layout.setOnClickListener(this)
        version_update_layout.setOnClickListener(this)
        about_us_layout.setOnClickListener(this)
        accessibility_statement.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            baseBack -> finish()
            about_us_layout -> intentTo(AboutUsActivity::class.java)
            clear_cache_layout -> clearCache()
            version_update_layout -> this.checkVersion()
            accessibility_statement -> {
                startActivity(Intent(this, WebActivity::class.java).apply {
                    putExtra("data", "file:///android_asset/accessibility_statement.html")
                    putExtra("title", getString(R.string.accessibility_statement))
                })
            }
        }

    }

    private fun clearCache() {
        OkDownload.getInstance().removeAll(true)
        DataCleanManager.cleanCache(this@SettingActivity)
        cache_size.text = "0k"
        Toast.makeText(this@SettingActivity, "Cache cleared ", Toast.LENGTH_SHORT).show()
    }

    private fun intentTo(activity: Class<*>) {
        val intent = Intent(this@SettingActivity, activity)
        startActivity(intent)
    }
}
