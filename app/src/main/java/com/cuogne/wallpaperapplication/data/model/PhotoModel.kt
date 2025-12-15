package com.cuogne.wallpaperapplication.data.model

import com.google.gson.annotations.SerializedName

data class PhotoModel(
    val id: String,
    val width: Int,
    val height: Int,

    @SerializedName("alt_description")
    val description: String?,

    val urls: PhotoUrl?
){
    data class PhotoUrl(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    )
}