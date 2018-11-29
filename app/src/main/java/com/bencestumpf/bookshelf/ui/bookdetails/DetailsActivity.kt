package com.bencestumpf.bookshelf.ui.bookdetails

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bencestumpf.bookshelf.R
import com.bencestumpf.bookshelf.ui.common.LCEStatus
import com.bencestumpf.bookshelf.ui.utils.bindView
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModelByClass

class DetailsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BOOK_ID = "EXTRA_BOOK_ID"
    }

    private val viewModel by viewModelByClass(DetailsViewModel::class)

    private val errorView: View by bindView(R.id.error_view)
    private val loadingView: View by bindView(R.id.loading_view)
    private val contentView: View by bindView(R.id.content_view)

    private val title: TextView by bindView(R.id.book_title)
    private val author: TextView by bindView(R.id.book_author)
    private val price: TextView by bindView(R.id.book_price)
    private val cover: ImageView by bindView(R.id.book_cover)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookdetails)
        setupActionBar()
        observeContent()
        loadWithExtraParameter()
    }

    private fun setupActionBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_navigate_before_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun loadWithExtraParameter() {
        val bookId = intent.getLongExtra(EXTRA_BOOK_ID, -1)
        viewModel.load(bookId)
    }

    private fun observeContent() {
        viewModel.content.observe(this, Observer {
            when (it.status) {
                LCEStatus.LOADING -> showLoading()
                LCEStatus.ERROR -> showError()
                LCEStatus.SUCCESS -> contentObserved(it.content!!)
            }
        })
    }

    private fun contentObserved(content: BookDetailsPM) {
        supportActionBar?.title = content.title

        errorView.visibility = View.GONE
        loadingView.visibility = View.GONE
        contentView.visibility = View.VISIBLE
        title.text = content.title
        author.text = content.author
        price.text = getString(R.string.price_placeholder, content.price)
        Glide.with(this).load(content.coverUrl).into(cover)
    }

    private fun showError() {
        contentView.visibility = View.GONE
        loadingView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        findViewById<Button>(R.id.retry).setOnClickListener { loadWithExtraParameter() }

    }

    private fun showLoading() {
        contentView.visibility = View.GONE
        errorView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
    }


}
