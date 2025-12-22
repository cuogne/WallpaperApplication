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
    val API_KEY = BuildConfig.unsplash_api_key_access

    fun getPhotos(
        page: Int,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val newPhotos: ArrayList<PhotoModel> = arrayListOf()
                newPhotos += repository.getRandomPhotos(page, API_KEY)

//                val deferredPhoto = pages.map { page ->
//                    async {
//                        repository.getRandomPhotos(page, key)
//                    }
//                }
//                val photos = deferredPhoto.awaitAll().flatten()
//                newPhotos.addAll(photos)

                if (isRefresh) {
                    // tai lai
                    _photos.value = newPhotos
                } else {
                    // them moi khi user keo xuong
                    val current = _photos.value ?: arrayListOf()
                    val filter = newPhotos.filter { photo ->
                        current.none { it.id == photo.id }
                    }
                    current.addAll(filter)
                    _photos.value = current
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun searchPhotos(
        query: String,
        page: Int,
        isNewSearch: Boolean
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = repository.getSearchPhotos(query, page, API_KEY)

                val newPhotos = response.results

                if (isNewSearch) {
                    _photos.value = ArrayList(newPhotos)
                } else {
                    val current = _photos.value ?: arrayListOf()
                    val filter = newPhotos.filter { photo ->
                        current.none { it.id == photo.id }
                    }
                    current.addAll(filter)
                    _photos.value = current
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}