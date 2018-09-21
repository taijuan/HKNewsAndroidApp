package com.chinadaily.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chinadaily.R
import com.chinadaily.base.BaseApp
import com.chinadaily.base.LazyLoadBaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : LazyLoadBaseFragment() {
    var isNews = true
    val tabs by lazy {
        if (isNews) {
            arrayOf(
                    arrayOf("HOME", "home"),
                    arrayOf("HONG KONG", "hong_kong"),
                    arrayOf("NATION", "nation"),
                    arrayOf("ASIA", "asia"),
                    arrayOf("WORLD", "world"),
                    arrayOf("BUSINESS", "business"),
                    arrayOf("DATA", "data"),
                    arrayOf("SPORTS", "sports")
            )
        } else {
            arrayOf(
                    arrayOf("LIFE & ART", "life_art"),
                    arrayOf("LEADERS", "leaders"),
                    arrayOf("OFFBEAT HK", "offbeat_hk"),
                    arrayOf("IN-DEPTH CHINA", "in_depth_china"),
                    arrayOf("EYE ON ASIA", "eye_on_asia"),
                    arrayOf("QUIRKY", "quirky"),
                    arrayOf("PHOTO", "photo")
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onFragmentFirstVisible() {
        tabLayout.setTabTextColors(ContextCompat.getColor(BaseApp.getInstance(), R.color.main_bottom_text_normal), ContextCompat.getColor(BaseApp.getInstance(), R.color.mainpage_color))
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val tabName = tabs[position][1]
                return when (tabName) {
                    "home" -> HomeNewsFragment().apply { arguments = Bundle().apply { putString("name", tabName) } }
                    else -> NewsFragment().apply { arguments = Bundle().apply { putString("name", tabName) } }
                }
            }

            override fun getCount(): Int {
                return tabs.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabs[position][0]
            }
        }
        viewPager.offscreenPageLimit = tabs.size
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.let {
                it.setContentDescription("${it.text} on ${getString(if (isNews) R.string.page1 else R.string.page2)}")
            }
        }
    }

}