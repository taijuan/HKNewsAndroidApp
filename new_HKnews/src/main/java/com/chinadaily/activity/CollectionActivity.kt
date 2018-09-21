package com.chinadaily.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.chinadaily.R
import com.chinadaily.adapter.CollectionAdapter
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.data.NewsBean
import kotlinx.android.synthetic.main.activity_collection_list.*
import kotlinx.android.synthetic.main.base_title_layout.*
import org.litepal.LitePal

class CollectionActivity : BaseAppActivity(), View.OnClickListener {

    private val adapter by lazy { CollectionAdapter(lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_list)
        baseBack.setOnClickListener(this)
        baseTitle.setText(R.string.my_favorite)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val data = adapter.getData()[position]
                LitePal.deleteAllAsync(NewsBean::class.java, "dataId = ${data.dataId}").listen { queryData() }
            }
        }).attachToRecyclerView(recyclerView)
        queryData()
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.baseBack -> finish()
        }
    }

    private fun queryData() {
        val data = LitePal.findAll(NewsBean::class.java)
        data.reverse()
        if (data.isNotEmpty()) {
            loadingProgressBar.visibility = View.GONE
            emptyView.visibility = View.GONE
        } else {
            loadingProgressBar.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
        adapter.refreshData(data)
    }
}
