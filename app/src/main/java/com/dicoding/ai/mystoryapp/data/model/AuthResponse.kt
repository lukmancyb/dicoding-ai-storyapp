package com.dicoding.ai.mystoryapp.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse<T>(
    @SerializedName("error")
    val error : Boolean,
    @SerializedName("message")
    val message : String,
    @SerializedName("loginResult")
    val result: T? = null
)