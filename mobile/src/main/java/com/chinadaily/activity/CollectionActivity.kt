package com.chinadaily.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.chinadaily.R
import com.chinadaily.adapter.CollectionAdapter
import com.chinadaily.base.BaseAppActivity
import com.chinadaily.base.setTabBarTop
import com.chinadaily.data.NewsBean
import kotlinx.android.synthetic.main.activity_collection_list.*
import kotlinx.android.synthetic.main.base_title_layout.*
import org.litepal.Operator

class CollectionActivity : BaseAppActivity(), View.OnClickListener {

    private val adapter by lazy { CollectionAdapter(lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_list)
        tabBar.setTabBarTop()
        baseBack.setOnClickListener(this)
        baseTitle.setText(R.string.my_favorite)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = adapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
            override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val data = adapter.getData()[position]
                Operator.deleteAllAsync(NewsBean::class.java, "dataId = ${data.dataId}").listen { queryData() }
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
        val data = Operator.findAll(NewsBean::class.java)
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
