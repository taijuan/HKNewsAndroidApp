package com.chinadaily.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.style.ReplacementSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chinadaily.R
import com.chinadaily.data.NewsDetail
import com.chinadaily.http.IMAGE_DNS
import com.chinadaily.pictureMap
import com.chinadaily.utils.*
import org.xml.sax.XMLReader

val defLoadingDrawable: Drawable?  by lazy {
    ContextCompat.getDrawable(getApp(), R.drawable.placeholder)
}

class NewsDetailAdapter(private val data: NewsDetail) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        0 -> HeaderHolder(parent)
        1 -> NewsDetailHolder(parent)
        else -> FooterHolder(parent)
    }

    override fun getItemCount() = 3

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsDetailHolder) {
            holder.setFromHtml(data)
        }
    }

    override fun getItemViewType(position: Int) = position
}

class NewsDetailHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_news_detail, parent, false).apply {
    layoutParams = ViewGroup.LayoutParams(-1, -2)
    if (isScreenRound()) {
        setPadding(headerOrFooterHeight(), 0, headerOrFooterHeight(), 0)
    } else {
        setPadding(dp2px(4f), 0, dp2px(4f), 0)
    }
}) {
    private lateinit var data: NewsDetail
    private val imageGetter: HttpImageGetter
    private val tagHandler: HttpImageTagHandler by lazy {
        HttpImageTagHandler()
    }

    init {
        imageGetter = HttpImageGetter(itemView as TextView) {
            itemView.post {
                setFromHtml(data)
            }
        }
    }

    fun setFromHtml(data: NewsDetail) {
        this.data = data
        (itemView as TextView).text = HtmlCompat.fromHtml(data.content, HtmlCompat.FROM_HTML_MODE_LEGACY, imageGetter, tagHandler)
    }
}

val width = screenWidth() - if (isScreenRound()) 2 * headerOrFooterHeight() else 2 * dp2px(4f)

class HttpImageGetter(private val textView: TextView, val onSuccess: () -> Unit) : Html.ImageGetter {

    override fun getDrawable(source: String?): Drawable? {
        val imageUrl = "$IMAGE_DNS$source"
        var drawable = pictureMap[imageUrl]
        if (drawable == null) {
            GlideApp.with(textView)
                    .load(imageUrl)
                    .override(width, Int.MAX_VALUE)
                    .transform(FitCenterAndRound())
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            pictureMap[imageUrl] = resource
                            onSuccess()
                        }
                    })
            drawable = defLoadingDrawable
        }
        if (drawable != null) {
            val height = drawable.intrinsicHeight * width / drawable.intrinsicWidth
            drawable.setBounds(0, 0, width, height)
        }
        return drawable
    }
}

class HttpImageTagHandler : Html.TagHandler {
    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        if (tag?.toLowerCase() == "img" && !opening && output != null) {
            endImgAddMargin(output)
        }
    }

    private fun endImgAddMargin(text: Editable) {
        val len = text.length
        text.append("\uFFFC")
        text.setSpan(SpaceSpan(), len, text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}


class SpaceSpan : ReplacementSpan() {

    override fun getSize(@NonNull paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm != null) {
            fm.top = -dp2px(8f) - paint.getFontMetricsInt(fm)
            fm.ascent = fm.top
            fm.bottom = 0
            fm.descent = fm.bottom
        }
        return 0
    }

    override fun draw(@NonNull canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, @NonNull paint: Paint) {

    }
}

