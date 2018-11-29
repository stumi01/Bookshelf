package com.bencestumpf.bookshelf.domain.repositories

import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.helper.OpenClass
import io.reactivex.Single

@OpenClass
class BookRepository(private val remote: Remote) {

    fun getBooks(page: Int): Single<List<Book>> {
        return remote.getBooks(page)
    }

    fun getBook(bookId: Long): Single<Book> {
        return remote.getBook(bookId)
    }

    fun addBook(title: String, author: String, price: Float, coverUrl: String): Single<Book> {
        return remote.addBook(title, author, price, coverUrl)
    }

    interface Remote {
        fun getBooks(page: Int): Single<List<Book>>
        fun getBook(bookId: Long): Single<Book>
        fun addBook(title: String, author: String, price: Float, coverUrl: String): Single<Book>
    }

}
