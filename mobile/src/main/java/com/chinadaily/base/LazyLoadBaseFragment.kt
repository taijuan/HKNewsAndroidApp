package com.chinadaily.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * @see androidx.fragment.app.FragmentPagerAdapter
 * @see androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
 * adapter必须设置FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
 * behavior = BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
 */
abstract class LazyLoadBaseFragment : androidx.fragment.app.Fragment() {

    private var mIsFirstVisible = true
    private val observer: LifecycleObserver by lazy {
        object : LifecycleObserver {
            @Keep
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                onFragmentVisible()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getContentViewRes(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(observer)
    }

    private fun onFragmentVisible() {
        if (mIsFirstVisible) {
            onFragmentFirstVisible()
            mIsFirstVisible = false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(observer)
        mIsFirstVisible = true
    }

    /**
     * 布局layout id
     */
    protected abstract fun getContentViewRes(): Int

    /**
     * 第一次显示
     */
    protected abstract fun onFragmentFirstVisible()
}