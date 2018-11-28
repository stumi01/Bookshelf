package com.bencestumpf.bookshelf.data.net.services

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface BookAPIService {
    @Headers("Content-Type: application/json")
    @GET("/api/v1/items")
    fun getItems(@Query("offset") offset: Int, @Query("count") count: Int):
            Single<Response<List<BookResponseModel>>>

    @Headers("Content-Type: application/json")
    @GET("/api/v1/items/{id}")
    fun getItem(@Path("id") bookID: Long):
            Single<Response<BookResponseModel>>

    @Headers("Content-Type: application/json")
    @PUT("/api/v1/items")
    fun create(@Body book: BookCreateModel):
            Single<Response<BookResponseModel>>
}

/*
The link parameter for the list is redundant information since we know the schema how to fetch one book.
For now its fine to use the same object for both get methods. Retrofit can handle the optional fields
 */
data class BookResponseModel(
    val id: Long,
    val title: String,
    val coverUrl: String? = null,
    val author: String? = null,
    val price: Float? = 0f
)

data class BookCreateModel(
    val title: String,
    val coverUrl: String,
    val author: String,
    val price: Float
)
