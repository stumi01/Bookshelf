package com.bencestumpf.bookshelf.domain.usecases

import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import io.reactivex.Single

class GetBooks(private val bookRepository: BookRepository) : ParameterizedUsecase<Int, List<Book>>() {

    override fun getSubscribable(): Single<List<Book>> {
        return bookRepository.getBooks(parameter ?: 0)
    }

}
