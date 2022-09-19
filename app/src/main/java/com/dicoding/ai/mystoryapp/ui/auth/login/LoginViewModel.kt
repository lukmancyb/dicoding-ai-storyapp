package com.dicoding.ai.mystoryapp.ui.auth.login

import androidx.lifecycle.*
import com.dicoding.ai.mystoryapp.data.model.UserModel
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: MainRepository) : ViewModel() {


    private val _loginResponse = MutableLiveData<ApiResponse<UserModel>>()
    val loginResponse: LiveData<ApiResponse<UserModel>>
        get() = _loginResponse


    fun getUser(): LiveData<UserModel> {
        return repo.getUser().asLiveData()
    }


    fun login(username: String, password: String) {
        viewModelScope.launch {
            repo.userLogin(username, password).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        repo.saveUser(response.data as UserModel)
                    }
                    else -> {}
                }
                _loginResponse.postValue(response)
            }

        }
    }

}