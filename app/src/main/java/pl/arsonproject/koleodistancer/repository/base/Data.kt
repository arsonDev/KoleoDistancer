package pl.arsonproject.koleodistancer.repository.base

import java.lang.Exception

sealed class Data<out T:Any> {
    data class Success<out T : Any>(val data : T) : Data<T>()
    data class Error(val exception : Exception) : Data<Nothing>()
}