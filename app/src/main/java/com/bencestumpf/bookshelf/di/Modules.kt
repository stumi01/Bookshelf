package com.bencestumpf.bookshelf.di

import com.bencestumpf.bookshelf.data.mock.MockBookDataStore
import com.bencestumpf.bookshelf.data.net.services.BookAPIService
import com.bencestumpf.bookshelf.domain.repositories.BookRepository
import com.bencestumpf.bookshelf.domain.usecases.AddBook
import com.bencestumpf.bookshelf.domain.usecases.GetBookDetails
import com.bencestumpf.bookshelf.domain.usecases.GetBooks
import com.bencestumpf.bookshelf.ui.addbook.AddBookViewModel
import com.bencestumpf.bookshelf.ui.bookdetails.DetailsViewModel
import com.bencestumpf.bookshelf.ui.booklist.BookListViewModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    viewModel<BookListViewModel>()

    viewModel<DetailsViewModel>()

    viewModel<AddBookViewModel>()

}

val domainModule = module {

    single { BookRepository(get()) }

    single { GetBooks(get()) }

    single { GetBookDetails(get()) }

    single { AddBook(get()) }
}

val dataModule = module {
    single<BookRepository.Remote> { MockBookDataStore() }
    //single<BookRepository.Remote> { BookDataStore() } TODO enable this if the backend ready
}

val retrofitModule = module {
    val SERVER_URL = "https://example.com"

    single { GsonBuilder().create() }

    single {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder().addInterceptor(logInterceptor).build()
    }

    single {
        Retrofit.Builder().baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(get()).build()
    }

    single<BookAPIService> {
        val retrofit: Retrofit = get()
        retrofit.create(BookAPIService::class.java)
    }

}

