package com.chinadaily.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

abstract class LazyLoadBaseFragment : Fragment() {

    private var mIsFirstVisible = true
    private var isViewCreated = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        onFragmentVisible()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        onFragmentVisible()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        onFragmentVisible()
    }

    private fun onFragmentVisible() {
        if (!isHidden && userVisibleHint && isViewCreated && mIsFirstVisible) {
            onFragmentFirstVisible()
            mIsFirstVisible = false
        }
    }

    protected abstract fun onFragmentFirstVisible()

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        mIsFirstVisible = true
    }
}