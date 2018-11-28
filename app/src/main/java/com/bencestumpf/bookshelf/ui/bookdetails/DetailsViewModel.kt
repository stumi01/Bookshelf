package com.bencestumpf.bookshelf.ui.bookdetails

import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.usecases.GetBookDetails
import com.bencestumpf.bookshelf.ui.common.LCEModel
import com.bencestumpf.bookshelf.ui.common.LCEStatus
import com.bencestumpf.bookshelf.ui.common.LCEViewModel

class DetailsViewModel(private val getBookDetails: GetBookDetails) : LCEViewModel<BookDetailsPM>() {

    fun load(bookId: Long) {
        setLoading()
        getBookDetails.withParams(bookId).execute(this::onDataArrived)
    }

    private fun onDataArrived(book: Book) {
        content.value = LCEModel(book.map(), status = LCEStatus.SUCCESS)
    }

}

private fun Book.map(): BookDetailsPM = BookDetailsPM(
    this.id, this.title, this.coverUrl!!,
    this.author!!, this.price!!
)



