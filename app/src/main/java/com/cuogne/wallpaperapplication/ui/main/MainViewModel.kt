package com.cuogne.wallpaperapplication.ui.main

import androidx.lifecycle.LiveData
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
    private val _photos = MutableLiveData<ArrayList<PhotoModel>>()
    val photos: LiveData<ArrayList<PhotoModel>> get() = _photos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getPhotos(pages: List<Int>, isRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val unsplash_api_key_access = BuildConfig.unsplash_api_key_access
                _isLoading.value = true

                // luu anh call tu api ve
                val deferredPhoto = pages.map { page ->
                    async {
                        repository.getRandomPhotos(page, unsplash_api_key_access)
                    }
                }
                val response = deferredPhoto.awaitAll()

                val newPhotos = response.flatten()

                if (isRefresh) {
                    _photos.value = ArrayList(newPhotos)
                } else {
                    val currentPhotos = _photos.value ?: ArrayList()
                    currentPhotos.addAll(newPhotos) // them vao list photo truoc do
                    _photos.value = currentPhotos
                }
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}