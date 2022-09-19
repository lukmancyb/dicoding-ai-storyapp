package com.dicoding.ai.mystoryapp.ui.home

import androidx.lifecycle.*
import com.dicoding.ai.mystoryapp.data.model.StoryModel
import com.dicoding.ai.mystoryapp.data.model.UserModel
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repo: MainRepository) : ViewModel() {


    private val loadTrigger = MutableLiveData(Unit)

    fun refresh() {
        loadTrigger.value = Unit
    }

    val listStory: LiveData<ApiResponse<List<StoryModel>>> = loadTrigger.switchMap {
        getStories()
    }


    fun getUser(): LiveData<UserModel> {
        return repo.getUser().asLiveData()
    }

    fun removeUser() = viewModelScope.launch { repo.logoutUser() }

    fun getStories(): LiveData<ApiResponse<List<StoryModel>>> {
        return Transformations.switchMap(
            getUser()
        ) {
            repo.getStories("Bearer ${it.token}").asLiveData()
        }
    }



}