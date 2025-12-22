package com.cuogne.wallpaperapplication.data.repository

import com.cuogne.wallpaperapplication.data.api.RetrofitClient
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import com.cuogne.wallpaperapplication.data.model.SearchPhotoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class PhotoRepository {
    private val api = RetrofitClient.instance

    suspend fun getRandomPhotos(page: Int, client_id: String): List<PhotoModel> {
        return withContext(Dispatchers.IO){
            try {
                api.getRandomPhoto(page, client_id)
            }
            catch (e: Exception){
                e.printStackTrace()
                emptyList() // tra ve empty list de ko crash app
            }
        }
    }

    suspend fun getSearchPhotos(query: String, page: Int, client_id: String): SearchPhotoModel{
        return withContext(Dispatchers.IO){
            try {
                api.getSearchPhoto(query, page, client_id)
            }
            catch (e: Exception){
                e.printStackTrace()
                SearchPhotoModel(0, 0, emptyList()) // tra ve empty list de ko crash app

            }
        }
    }
}