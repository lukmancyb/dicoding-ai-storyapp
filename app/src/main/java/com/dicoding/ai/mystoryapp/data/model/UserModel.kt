package com.dicoding.ai.mystoryapp.data.model

data class UserModel(
    val userId :String,
    val name : String,
    val token : String,
    val isLogin : Boolean = false
)