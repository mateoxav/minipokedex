package com.accesodatos.minipokedex.core.util

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val message: String, val throwable: Throwable? = null) : DataResult<Nothing>
}
