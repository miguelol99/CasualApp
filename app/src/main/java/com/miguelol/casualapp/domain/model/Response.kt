package com.miguelol.casualapp.domain.model

typealias Error = Response.Error
typealias Success<T> = Response.Success<T>

sealed class Response<out T> {
    data class Error(val e: Exception) : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
}

sealed class Either<out L, out R>
class Left<L>(val value: L) : Either<L, Nothing>()