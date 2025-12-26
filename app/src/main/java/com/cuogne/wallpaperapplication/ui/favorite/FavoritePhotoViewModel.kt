package com.cuogne.wallpaperapplication.ui.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import com.cuogne.wallpaperapplication.data.repository.FavoritePhotoRepository
import kotlinx.coroutines.launch

class FavoritePhotoViewModel: ViewModel() {
    private val repository = FavoritePhotoRepository()

    private val _favoritePhoto = MutableLiveData<List<PhotoModel>>()
    val favoritePhoto: MutableLiveData<List<PhotoModel>> = _favoritePhoto

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun loadFavoritePhoto(){
        viewModelScope.launch {
            _isLoading.value = true
            val response  = repository.getAllFavoritePhotos()
            response.onSuccess { favoritePhoto ->
                _favoritePhoto.value = favoritePhoto
                _isLoading.value = false
            }
            response.onFailure { exception ->
                _favoritePhoto.value = emptyList()
                _isLoading.value = false
            }
        }
    }
}
