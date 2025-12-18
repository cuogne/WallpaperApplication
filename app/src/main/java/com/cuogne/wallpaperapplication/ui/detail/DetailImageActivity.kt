package com.cuogne.wallpaperapplication.ui.detail

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.data.model.PhotoModel

class DetailImageActivity : AppCompatActivity() {

    private lateinit var detailPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)

        detailPhoto = findViewById(R.id.detailPhoto)
        val photo = getPhotoClicked()

        if (photo != null){
            detailPhoto.load(photo.urls?.regular)
        }
    }

    private fun getPhotoClicked(): PhotoModel?{
        val photo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra("photo", PhotoModel::class.java)
        }
        else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("photo")
        }
        return photo
    }
}