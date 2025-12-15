package com.cuogne.wallpaperapplication.data.repository

import com.cuogne.wallpaperapplication.data.api.RetrofitClient
import com.cuogne.wallpaperapplication.data.model.PhotoModel


class PhotoRepository {
    private val api = RetrofitClient.instance

    suspend fun getRandomPhotos(page: Int, client_id: String): List<PhotoModel> {
        return api.getRandomPhoto(page, client_id)
    }
}