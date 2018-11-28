package com.bencestumpf.bookshelf.data.net

import android.accounts.NetworkErrorException
import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.data.net.services.BookAPIService
import com.bencestumpf.bookshelf.data.net.services.BookCreateModel
import com.bencestumpf.bookshelf.data.net.services.BookResponseModel
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import io.reactivex.Single
import retrofit2.Response

class BookDataStore(private val apiService: BookAPIService) : BookRepository.Remote {

    companion object {
        const val PAGE_COUNT = 10
    }

    override fun getBooks(page: Int): Single<List<Book>> {
        return apiService.getItems(page * PAGE_COUNT, PAGE_COUNT)
            .mapResponse { it.asSequence().map(BookResponseModel::mapToBook).toList() }
    }

    override fun getBook(bookId: Long): Single<Book> {
        return apiService.getItem(bookId)
            .mapResponse(BookResponseModel::mapToBook)
    }

    override fun addBook(title: String, author: String, price: Float, coverUrl: String): Single<Book> {
        return apiService.create(BookCreateModel(title, coverUrl, author, price))
            .mapResponse(BookResponseModel::mapToBook)
    }
}

private fun <T, R> Single<Response<T>>.mapResponse(transformer: (T) -> R): Single<R> {
    return this.map {
        if (it.isSuccessful) {
            return@map transformer.invoke(it.body()!!)
        }
        throw NetworkErrorException("Error happened: ${it.message()}")
    }
}

private fun BookResponseModel.mapToBook(): Book =
    Book(this.id, this.title, this.coverUrl, this.author, this.price)
