package com.bencestumpf.bookshelf.ui.addbook

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.usecases.AddBook
import com.bencestumpf.bookshelf.domain.usecases.AddBookParam
import com.bencestumpf.bookshelf.ui.common.LCEModel
import com.bencestumpf.bookshelf.ui.common.LCEStatus
import com.bencestumpf.bookshelf.ui.common.LCEViewModel

class AddBookViewModel(private val addBook: AddBook) : LCEViewModel<Long>() {
    var coverUrl: MutableLiveData<LCEModel<String>> = MutableLiveData()

    override fun onCleared() {
        coverUrl = MutableLiveData()
        super.onCleared()
    }

    fun coverUrlArrived(text: CharSequence?) {
        if (Patterns.WEB_URL.matcher(text).matches()) {
            coverUrl.value = LCEModel(text.toString(), status = LCEStatus.SUCCESS)
        } else {
            coverUrl.value = LCEModel(status = LCEStatus.ERROR)
        }
    }

    fun onUploadClick(title: CharSequence, author: CharSequence, price: CharSequence, coverUrl: CharSequence) {
        content.value = LCEModel(status = LCEStatus.LOADING)

        val titleValid = title.toString().isNotBlank()
        val authorValid = author.toString().isNotBlank()
        val priceValid = price.toString().isNotBlank()
        val coverValid = Patterns.WEB_URL.matcher(coverUrl).matches()

        if (titleValid && authorValid && priceValid && coverValid) {
            addBook.withParams(
                AddBookParam(title.toString(), author.toString(), price.toString().toFloat(), coverUrl.toString())
            ).execute(this::onUploadSuccess, this::onUploadError)
        } else {
            content.value = LCEModel(
                error = AddBookValidationError(!titleValid, !authorValid, !priceValid, !coverValid),
                status = LCEStatus.ERROR
            )
        }
    }

    private fun onUploadSuccess(book: Book) {
        content.value = LCEModel(content = book.id, status = LCEStatus.SUCCESS)
    }

    private fun onUploadError(throwable: Throwable) {
        Log.e(AddBookViewModel::class.java.simpleName, "Upload error", throwable)
        content.value = LCEModel(status = LCEStatus.ERROR, error = NetworkError(throwable))
    }

}

class NetworkError(cause: Throwable) : Throwable(cause)

class AddBookValidationError(
    val titleError: Boolean, val authorError: Boolean, val priceError: Boolean,
    val coverUrl: Boolean
) : Throwable()