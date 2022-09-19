package com.dicoding.ai.mystoryapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import com.dicoding.ai.mystoryapp.ui.auth.login.LoginViewModel
import com.dicoding.ai.mystoryapp.ui.auth.signup.SignupViewModel
import com.dicoding.ai.mystoryapp.ui.home.MainViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repo : MainRepository) :
    ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repo) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java)-> {
                SignupViewModel(repo) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java)-> {
                LoginViewModel(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }

    }
}