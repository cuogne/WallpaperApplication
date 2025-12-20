package com.cuogne.wallpaperapplication.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cuogne.wallpaperapplication.data.model.PhotoModel

class DetailImageViewModel: ViewModel() {
    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?> = _photo

    fun setPhoto(photo: PhotoModel?) {
        _photo.value = photo
    }
}
