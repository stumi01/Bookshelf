package com.bencestumpf.bookshelf.ui.bookdetails

data class BookDetailsPM(
    val id: Long,
    val title: String,
    val coverUrl: String,
    val author: String,
    val price: Float
)