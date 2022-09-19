package com.dicoding.ai.mystoryapp.data.network

sealed class ApiResponse<out R> {
data class Success<out T>(val data : T?) : ApiResponse<T>()
    data class Error(
        val message : String,
        val isNetworkError : Boolean,
        val errorCode :Int?,
        val errorBody: String?
    ) : ApiResponse<Nothing>()
    object Empty : ApiResponse<Nothing>()
}