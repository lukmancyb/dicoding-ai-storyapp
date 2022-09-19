package com.dicoding.ai.mystoryapp.data.repository

import com.dicoding.ai.mystoryapp.data.UserPreference
import com.dicoding.ai.mystoryapp.data.model.StoryModel
import com.dicoding.ai.mystoryapp.data.model.UserModel
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.network.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class MainRepository(
    private val retrofitServices: ApiServices,
    private val preference: UserPreference
) {

    fun getUser(): Flow<UserModel> = preference.getUser()
    suspend fun logoutUser() = preference.logout()

    suspend fun saveUser(userModel: UserModel) = preference.saveUser(userModel)

    suspend fun userLogin(
        email: String,
        password: String
    ): Flow<ApiResponse<UserModel>> {
        return flow {
            try {
                val res = retrofitServices.login(email, password)
                if (res.error) {
                    emit(ApiResponse.Empty)
                } else {
                    emit(ApiResponse.Success(res.result))
                }
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        emit(
                            ApiResponse.Error(
                                t.message.toString(),
                                false,
                                t.code(),
                                t.response()?.errorBody()?.string()
                            )
                        )
                    }
                    else -> {
                        emit(ApiResponse.Error("Network Eror", true, null, null))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

     fun getStories(token : String) : Flow<ApiResponse<List<StoryModel>>>{
        return flow {
            try {
                val res = retrofitServices.getStories(token)
                if (res.result.isNullOrEmpty()) {
                    emit(ApiResponse.Empty)
                } else {
                    emit(ApiResponse.Success(res.result))
                }
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        emit(
                            ApiResponse.Error(
                                t.message.toString(),
                                false,
                                t.code(),
                                t.response()?.errorBody()?.string()
                            )
                        )
                    }
                    else -> {
                        emit(ApiResponse.Error("Network Eror", true, null, null))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ) : Flow<ApiResponse<String>> {
        return flow {
            try {
                val res = retrofitServices.register(name, email, password)
                if (res.error) {
                    emit(ApiResponse.Empty)
                } else {
                    emit(ApiResponse.Success(res.message))
                }
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        emit(
                            ApiResponse.Error(
                                t.message.toString(),
                                false,
                                t.code(),
                                t.response()?.errorBody()?.string()
                            )
                        )
                    }
                    else -> {
                        emit(ApiResponse.Error("Network Eror", true, null, null))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}