package com.dicoding.ai.mystoryapp.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repo: MainRepository) : ViewModel() {

    private val _registerResponse = MutableLiveData<ApiResponse<String>>()
    val registerResponse: LiveData<ApiResponse<String>>
        get() = _registerResponse


    fun register(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            repo.userRegister(name, email, password).collect {
                _registerResponse.postValue(it)
            }


        }

    }


}