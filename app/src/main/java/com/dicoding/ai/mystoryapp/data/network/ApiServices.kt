package com.dicoding.ai.mystoryapp.data.network

import com.dicoding.ai.mystoryapp.data.model.AuthResponse
import com.dicoding.ai.mystoryapp.data.model.StoryModel
import com.dicoding.ai.mystoryapp.data.model.StoryResponse
import com.dicoding.ai.mystoryapp.data.model.UserModel
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiServices {

    @FormUrlEncoded
    @POST("v1/login")
    suspend fun login(
        @Field("email")
        email: String,
        @Field("password")
        password: String
    ): AuthResponse<UserModel>

    @FormUrlEncoded
    @POST("v1/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): AuthResponse<Nothing>

    @GET("v1/stories")
    suspend fun getStories(
        @Header("Authorization") token : String
    ) : StoryResponse<List<StoryModel>>

    @Multipart
    @POST("v1/stories")
    suspend fun postStory(
        @Header("Authorization") token : String,
        @Part file : MultipartBody.Part,
        @Part("description") desc : RequestBody

    ) : StoryResponse<String>

    companion object {
        fun getApiServices(): ApiServices {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiServices::class.java)
        }
    }
}

