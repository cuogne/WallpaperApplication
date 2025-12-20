package com.cuogne.wallpaperapplication.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil3.load
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.data.model.PhotoModel

class DetailImageActivity : AppCompatActivity() {

    private lateinit var detailPhoto: ImageView
    private lateinit var descriptionImage: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var viewModel: DetailImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)

        detailPhoto = findViewById(R.id.detailPhoto)
        descriptionImage = findViewById(R.id.descriptionImage)
        btnBack = findViewById(R.id.btnBack)

        viewModel = ViewModelProvider(this)[DetailImageViewModel::class.java]

        // an di cho den khi load xong
        detailPhoto.visibility = View.INVISIBLE
        descriptionImage.visibility = View.INVISIBLE

        // observe photo
        viewModel.photo.observe(this){ photo ->
            photo?.let {
                descriptionImage.text = it.description
                detailPhoto.load(it.urls?.regular){
                    listener(
                        onSuccess = {_, _, ->
                            detailPhoto.visibility = View.VISIBLE
                            descriptionImage.visibility = View.VISIBLE
                        }
                    )
                }
            }
        }

        viewModel.setPhoto(getPhotoClicked())

        btnBack.setOnClickListener {
            finish()
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