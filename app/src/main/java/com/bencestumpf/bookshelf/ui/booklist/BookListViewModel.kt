package com.bencestumpf.bookshelf.ui.booklist

import androidx.lifecycle.MutableLiveData
import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.usecases.GetBooks
import com.bencestumpf.bookshelf.ui.booklist.items.BookPM
import com.bencestumpf.bookshelf.ui.common.LCEModel
import com.bencestumpf.bookshelf.ui.common.LCEStatus
import com.bencestumpf.bookshelf.ui.common.LCEViewModel


class BookListViewModel(private val getBooks: GetBooks) : LCEViewModel<List<BookPM>>() {

    var navigation: MutableLiveData<Screen> = MutableLiveData()
    var nextPage = 0

    override fun onCleared() {
        navigation = MutableLiveData()
        nextPage = 0
        super.onCleared()
    }

    fun loadData() {
        setLoading()
        getBooks.withParams(nextPage).execute(this::onDataArrived)
    }

    private fun onDataArrived(books: List<Book>) {
        if (books.isEmpty()) {
            content.value = LCEModel(error = PagingEndReached(), status = LCEStatus.ERROR)
        } else {
            nextPage++
            content.value = LCEModel(
                books.map {
                    BookPM(
                        it.id,
                        it.title,
                        this@BookListViewModel::onBookClick
                    )
                },
                status = LCEStatus.SUCCESS
            )
        }
    }

    private fun onBookClick(bookId: Long) {
        navigation.value = BookDetailsDetailsScreen(bookId)
    }

    fun onAddBookClick() {
        navigation.value = AddBookScreen()
    }

}

class PagingEndReached : Throwable()

class BookDetailsDetailsScreen(val bookId: Long) : Screen

class AddBookScreen : Screen

interface Screen


