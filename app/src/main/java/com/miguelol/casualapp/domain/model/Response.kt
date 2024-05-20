package com.miguelol.casualapp.domain.model

typealias Error = Response.Error
typealias Success<T> = Response.Success<T>

sealed class Response<out T> {
    data class Error(val e: Exception) : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
}