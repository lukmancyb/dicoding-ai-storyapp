package com.dicoding.ai.mystoryapp.data.model

import com.google.gson.annotations.SerializedName

data class StoryResponse<T>(
    @SerializedName("error")
    val error : Boolean,
    @SerializedName("message")
    val message : String,
    @SerializedName("listStory")
    val result: T? = null
)