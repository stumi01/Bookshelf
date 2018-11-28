package com.bencestumpf.bookshelf.domain.usecases

import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import io.reactivex.Single

class AddBook(private val bookRepository: BookRepository) : ParameterizedUsecase<AddBookParam, Book>() {
    override fun getSubscribable(): Single<Book> {
        return bookRepository.addBook(
            parameter!!.title,
            parameter!!.author,
            parameter!!.price,
            parameter!!.coverUrl
        )
    }

}

class AddBookParam(val title: String, val author: String, val price: Float, val coverUrl: String)
