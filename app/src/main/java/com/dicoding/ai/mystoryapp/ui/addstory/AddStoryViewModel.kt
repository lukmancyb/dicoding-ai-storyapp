package com.dicoding.ai.mystoryapp.ui.addstory

import androidx.lifecycle.*
import com.dicoding.ai.mystoryapp.data.network.ApiResponse
import com.dicoding.ai.mystoryapp.data.repository.MainRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repo : MainRepository) : ViewModel() {


    private val _storyPostResponse = MutableLiveData<ApiResponse<String>>()
    val storyPostResponse: LiveData<ApiResponse<String>>
        get() = _storyPostResponse

    fun postStory(file : MultipartBody.Part, desc : RequestBody){
        viewModelScope.launch {
            repo.getUser().collect{
                repo.postStory(file, desc, "Bearer ${it.token}").collect{ res ->
                    _storyPostResponse.postValue(res)
                }
            }
        }
    }


}