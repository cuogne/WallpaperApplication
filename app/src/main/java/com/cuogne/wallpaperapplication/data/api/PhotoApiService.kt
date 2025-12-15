package com.cuogne.wallpaperapplication.data.api

import com.cuogne.wallpaperapplication.data.model.PhotoModel
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoApiService {
    // https://api.unsplash.com/photos?page={page}&client_id=YOUR_ACCESS_KEY
    @GET("photos")
    suspend fun getRandomPhoto(
        @Query("page") page: Int,
        @Query("client_id") client_id: String
    ): List<PhotoModel>
}