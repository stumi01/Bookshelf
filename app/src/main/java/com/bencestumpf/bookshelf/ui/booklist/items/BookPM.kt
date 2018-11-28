package com.bencestumpf.bookshelf.ui.booklist.items

import android.view.View
import android.widget.TextView
import com.bencestumpf.bookshelf.R
import com.bencestumpf.bookshelf.ui.utils.bindView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class BookPM(val id: Long, val title: String, val onBookClick: (Long) -> Unit) :
    AbstractItem<BookPM, BookViewHolder>() {

    override fun getType(): Int = R.id.book_row_id

    override fun getViewHolder(v: View): BookViewHolder =
        BookViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.row_book
}

class BookViewHolder(view: View) : FastAdapter.ViewHolder<BookPM>(view) {

    private val title: TextView by bindView(R.id.book_title)

    override fun unbindView(item: BookPM) {
        title.text = ""
    }

    override fun bindView(item: BookPM, payloads: MutableList<Any>) {
        title.text = item.title
        itemView.setOnClickListener { item.onBookClick.invoke(item.id) }
    }

}
