package com.chinadaily.wigdet

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by Administrator on 2018/1/17.
 */
class W16H9ImageView : AppCompatImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth * 9 / 16)
    }
}
