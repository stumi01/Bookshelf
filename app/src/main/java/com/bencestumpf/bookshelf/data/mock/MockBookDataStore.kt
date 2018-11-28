package com.bencestumpf.bookshelf.data.mock

import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import io.reactivex.Single

class MockBookDataStore : BookRepository.Remote {

    private val cover = "http://psypressuk.com/wp-content/uploads/2009/08/hesse-196x300.jpg"
    private val books = ArrayList<Book>()

    init {
        for (i in 0..19) {
            books += createBook(i)
        }
    }

    private fun createBook(id: Int): Book =
        Book(id.toLong(), "Book Title $id", cover, "Author $id", 1.99f)


    override fun addBook(title: String, author: String, price: Float, coverUrl: String): Single<Book> {
        return Single.create {
            books += Book(books.size.toLong(), title, coverUrl, author, price)
            it.onSuccess(books.last())
        }
    }

    override fun getBook(bookId: Long): Single<Book> {
        return Single.create { emitter ->
            books.find { it.id == bookId }?.let {
                emitter.onSuccess(it)
            } ?: emitter.onError(NullPointerException())
        }
    }

    override fun getBooks(page: Int): Single<List<Book>> {
        return Single.create {
            it.onSuccess(books.asSequence().drop(page * PAGE_COUNT).take(PAGE_COUNT).toList())
        }
    }

    companion object {
        const val PAGE_COUNT = 10
    }
}