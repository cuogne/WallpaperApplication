package com.cuogne.wallpaperapplication.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuogne.wallpaperapplication.BuildConfig
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import com.cuogne.wallpaperapplication.data.repository.PhotoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = PhotoRepository()
    val photos = MutableLiveData<List<PhotoModel>>()

    fun getPhotos(startPage: Int, endPage: Int) {
        viewModelScope.launch {
            try {
                val unsplash_api_key_access = BuildConfig.unsplash_api_key_access

                val deferredPhoto = (startPage..endPage).map { id ->
                    async {
                        repository.getRandomPhotos(id, unsplash_api_key_access)
                    }
                }
                val response = deferredPhoto.awaitAll()
                photos.value = response.flatten()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}