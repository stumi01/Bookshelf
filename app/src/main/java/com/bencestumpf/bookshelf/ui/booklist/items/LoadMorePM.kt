package com.bencestumpf.bookshelf.ui.booklist.items

import android.view.View
import com.bencestumpf.bookshelf.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class LoadMorePM : AbstractItem<LoadMorePM, LoadMoreViewHolder>() {
    override fun getType(): Int = R.id.load_more_row_id

    override fun getViewHolder(v: View): LoadMoreViewHolder = LoadMoreViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.row_load_more

}

class LoadMoreViewHolder(view: View) : FastAdapter.ViewHolder<LoadMorePM>(view) {
    override fun unbindView(item: LoadMorePM) {
    }

    override fun bindView(item: LoadMorePM, payloads: MutableList<Any>) {
    }

}
