package com.chinadaily.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chinadaily.R
import com.chinadaily.activity.CollectionActivity
import com.chinadaily.activity.FeedBackActivity
import com.chinadaily.activity.SettingActivity
import com.chinadaily.activity.WebActivity
import com.chinadaily.adapter.TF2
import com.chinadaily.base.LazyLoadBaseFragment
import com.chinadaily.http.HttpConstants
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * Created by Staff on 2017/2/15.
 */

class MineFragment : LazyLoadBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onFragmentFirstVisible() {
        favorite.typeface = TF2
        facebook.typeface = TF2
        twitter.typeface = TF2
        feedback.typeface = TF2
        setting.typeface = TF2
        favorite.setOnClickListener {
            startActivity(Intent(activity, CollectionActivity::class.java))
        }
        facebook.setOnClickListener {
            startActivity(Intent(activity, WebActivity::class.java).apply {
                putExtra("data", HttpConstants.FACEBOOK)
                putExtra("title", "Facebook")
            })
        }
        twitter.setOnClickListener {
            startActivity(Intent(activity, WebActivity::class.java).apply {
                putExtra("data", HttpConstants.TWITTER)
                putExtra("title", "Twitter")
            })
        }
        feedback.setOnClickListener {
            startActivity(Intent(activity, FeedBackActivity::class.java))
        }
        setting.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }
    }
}
