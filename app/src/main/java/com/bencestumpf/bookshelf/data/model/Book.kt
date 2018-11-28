package com.bencestumpf.bookshelf.data.model

data class Book(
    val id: Long, val title: String, val coverUrl: String? = null, val author: String? = null,
    val price: Float? = 0f
)