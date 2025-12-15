package com.cuogne.wallpaperapplication.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuogne.wallpaperapplication.BuildConfig
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import com.cuogne.wallpaperapplication.data.repository.PhotoRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = PhotoRepository()
    val photos = MutableLiveData<List<PhotoModel>>()

    fun getPhotos() {
        viewModelScope.launch {
            try {
                val unsplash_api_key_access = BuildConfig.unsplash_api_key_access

                val response = repository.getRandomPhotos(1, unsplash_api_key_access)
                photos.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}