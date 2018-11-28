package com.bencestumpf.bookshelf.domain.usecases

import com.bencestumpf.bookshelf.data.model.Book
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import io.reactivex.Single

class GetBookDetails(private val bookRepository: BookRepository) : ParameterizedUsecase<Long, Book>() {
    override fun getSubscribable(): Single<Book> {
        return bookRepository.getBook(parameter!!)
    }

}
