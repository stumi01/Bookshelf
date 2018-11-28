package com.bencestumpf.bookshelf.ui.common

data class LCEModel<T>(
    var content: T? = null,
    var error: Throwable? = null,
    var status: LCEStatus? = null
)

enum class LCEStatus {
    LOADING, ERROR, SUCCESS
}
