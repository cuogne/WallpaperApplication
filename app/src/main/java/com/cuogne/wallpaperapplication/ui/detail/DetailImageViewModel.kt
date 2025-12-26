package com.cuogne.wallpaperapplication.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import com.cuogne.wallpaperapplication.data.repository.FavoritePhotoRepository
import kotlinx.coroutines.launch

class DetailImageViewModel: ViewModel() {
    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?> = _photo

    private val favoritePhotoRepository = FavoritePhotoRepository()
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: MutableLiveData<Boolean> = _isFavorite

    private val _favoriteMessage = MutableLiveData<String?>()
    val favoriteMessage: LiveData<String?> = _favoriteMessage

    fun setPhoto(photo: PhotoModel?) {
        _photo.value = photo
    }

    fun checkFavoriteStatus(photoId: String) {
        viewModelScope.launch {
            val result = favoritePhotoRepository.isFavorite(photoId)
            result.onSuccess { _isFavorite.value = it }
        }
    }

    fun toggleFavoriteStatus() {
        viewModelScope.launch {
            val currentPhoto = photo.value ?: return@launch
            
            val statusResult = favoritePhotoRepository.isFavorite(currentPhoto.id)
            val currentlyFavorite = statusResult.getOrElse { false }

            val result = if (currentlyFavorite) {
                favoritePhotoRepository.removeFavorite(currentPhoto.id) // dang tim -> xoa
            } else {
                favoritePhotoRepository.addFavorite(currentPhoto) // dang chua tim -> them
            }

            result.onSuccess {
                _isFavorite.value = !currentlyFavorite
                val message = if (currentlyFavorite) {
                    "Đã xóa khỏi mục yêu thích"
                } else {
                    "Đã thêm vào mục yêu thích"
                }
                _favoriteMessage.value = message
            }
            result.onFailure { exception ->
                _favoriteMessage.value = "Lỗi: ${exception.message ?: "Không thể cập nhật mục yêu thích"}"
            }
        }
    }
    fun clearFavoriteMessage() {
        _favoriteMessage.value = null
    }
}
