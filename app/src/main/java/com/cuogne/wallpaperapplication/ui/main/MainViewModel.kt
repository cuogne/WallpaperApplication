package com.cuogne.wallpaperapplication.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import com.cuogne.wallpaperapplication.data.repository.PhotoRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = PhotoRepository()
    val photos = MutableLiveData<List<PhotoModel>>()

    fun getPhotos() {
        viewModelScope.launch {
            try {
                val response = repository.getRandomPhotos(1, "gypzxj7sBVYwfLZE47tQBamJ4Np_7_bGkqbm3TJ2sq4")
                for (photo in response){
                    Log.d("MainActivity", "onCreate: ${photo.id}, ${photo.width}, ${photo.height}, ${photo.description}, ${photo.urls}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}