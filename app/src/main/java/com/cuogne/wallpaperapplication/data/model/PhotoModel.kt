package com.cuogne.wallpaperapplication.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoModel(
    val id: String,
    val width: Int,
    val height: Int,

    @SerializedName("alt_description")
    val description: String?,
    val color: String,
    val urls: PhotoUrl?
): Parcelable {
    @Parcelize
    data class PhotoUrl(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String
    ): Parcelable
}