package com.bencestumpf.bookshelf.ui.booklist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bencestumpf.bookshelf.R
import com.bencestumpf.bookshelf.ui.addbook.AddBookActivity
import com.bencestumpf.bookshelf.ui.bookdetails.DetailsActivity
import com.bencestumpf.bookshelf.ui.bookdetails.DetailsActivity.Companion.EXTRA_BOOK_ID
import com.bencestumpf.bookshelf.ui.booklist.items.BookPM
import com.bencestumpf.bookshelf.ui.common.LCEStatus
import com.bencestumpf.bookshelf.ui.utils.bindView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter_extensions.items.ProgressItem
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener
import org.koin.androidx.viewmodel.ext.android.viewModelByClass


class BookListActivity : AppCompatActivity() {

    private val viewModel by viewModelByClass(BookListViewModel::class)

    private val errorView: View by bindView(R.id.error_view)

    private val contentView: View by bindView(R.id.content_view)
    private val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.swipeRefreshLayout)
    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    private val addBook: FloatingActionButton by bindView(R.id.add_book_floating)

    private val itemAdapter = ItemAdapter<BookPM>()
    private val footerAdapter = ItemAdapter<ProgressItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booklist)
        setupRecycler()
        observeContent()
        observeNavigation()
        setupAddButton()

        viewModel.loadData()
    }

    private fun setupAddButton() {
        addBook.setOnClickListener { viewModel.onAddBookClick() }
    }

    private fun setupRecycler() {
        val fastAdapter = fastAdapter(itemAdapter, footerAdapter)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = fastAdapter
        recyclerView.isNestedScrollingEnabled = false
        swipeRefreshLayout.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        recyclerView.clearOnScrollListeners()
        val endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                footerAdapter.clear()
                footerAdapter.add(ProgressItem().withEnabled(false))
                viewModel.loadData()
            }
        }
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener)
    }

    private fun observeNavigation() {
        viewModel.navigation.observe(this, Observer {
            when (it) {
                is BookDetailsDetailsScreen -> navigateToBookDetails(it.bookId)
                is AddBookScreen -> navigateToAddBook()
            }
        })
    }

    private fun navigateToBookDetails(bookID: Long?) {
        startActivity(Intent(this, DetailsActivity::class.java).apply {
            putExtra(EXTRA_BOOK_ID, bookID)
        })
    }

    private fun navigateToAddBook() {
        startActivity(Intent(this, AddBookActivity::class.java))
    }

    private fun observeContent() {
        swipeRefreshLayout.isRefreshing = false
        viewModel.content.observe(this, Observer {
            when (it.status) {
                LCEStatus.LOADING -> showLoading()
                LCEStatus.ERROR -> showError(it.error)
                LCEStatus.SUCCESS -> contentObserved(it.content!!)
            }
        })
    }

    private fun showLoading() {
        errorView.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = true
        contentView.visibility = View.VISIBLE
    }

    private fun showError(error: Throwable?) {
        when (error) {
            is PagingEndReached -> disableLoadingFooter()
            else -> showGenericError()
        }
    }

    private fun showGenericError() {
        swipeRefreshLayout.isRefreshing = false
        contentView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        findViewById<Button>(R.id.retry).setOnClickListener { viewModel.loadData() }
    }

    private fun disableLoadingFooter() {
        swipeRefreshLayout.isRefreshing = false
        recyclerView.clearOnScrollListeners()
        footerAdapter.clear()
        Toast.makeText(this, R.string.paging_end_reached, Toast.LENGTH_LONG).show()
    }

    private fun contentObserved(content: List<BookPM>) {
        swipeRefreshLayout.isRefreshing = false
        itemAdapter.add(content)
    }
}

fun fastAdapter(vararg adapter: IAdapter<*>) =
    FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(adapter.toList())