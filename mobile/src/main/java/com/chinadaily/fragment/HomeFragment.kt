package com.chinadaily.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chinadaily.R
import com.chinadaily.base.BaseApp
import com.chinadaily.base.LazyLoadBaseFragment
import com.chinadaily.base.setTabBarTop
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : LazyLoadBaseFragment() {
    var isNews = true
    override fun getContentViewRes() = R.layout.fragment_home
    override fun onFragmentFirstVisible() {
        tabBar.setTabBarTop()
        val tabs = if (isNews) {
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
        tabLayout.setTabTextColors(ContextCompat.getColor(BaseApp.getInstance(), R.color.main_bottom_text_normal), ContextCompat.getColor(BaseApp.getInstance(), R.color.mainpage_color))
        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return when (val tabName = tabs[position][1]) {
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