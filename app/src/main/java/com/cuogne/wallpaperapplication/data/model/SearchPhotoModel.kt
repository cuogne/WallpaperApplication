package com.cuogne.wallpaperapplication.data.model

import com.google.gson.annotations.SerializedName

data class SearchPhotoModel(
    val total: Int,

    @SerializedName("total_pages")
    val totalPages: Int,
    val results: List<PhotoModel>
)
