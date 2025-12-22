package com.cuogne.wallpaperapplication.ui.detail

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import coil3.load
import coil3.request.placeholder
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.data.model.PhotoModel

class DetailImageActivity : AppCompatActivity() {

    private lateinit var detailPhoto: ImageView
    private lateinit var descriptionImage: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var viewModel: DetailImageViewModel
    private lateinit var fade: Fade

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)

        // init fade
        supportPostponeEnterTransition()
        fade = fadeTransition()

        detailPhoto = findViewById(R.id.detailPhoto)
        descriptionImage = findViewById(R.id.descriptionImage)
        btnBack = findViewById(R.id.btnBack)

        viewModel = ViewModelProvider(this)[DetailImageViewModel::class.java]
        val photoFromIntent = getPhotoClicked()
        detailPhoto.transitionName = photoFromIntent?.id

        // observe photo
        viewModel.photo.observe(this){ photo ->
            photo?.let {
                descriptionImage.text = it.description
                detailPhoto.load(it.urls?.regular){
                    placeholder(it.color.toColorInt().toDrawable())
                    listener(
                        onSuccess = { _, _ ->
                            supportStartPostponedEnterTransition()
                        },
                        onError = { _, _ ->
                            supportStartPostponedEnterTransition()
                        }
                    )
                }
            }
        }

        viewModel.setPhoto(photoFromIntent)

        btnBack.setOnClickListener {
            supportFinishAfterTransition()
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

    private fun fadeTransition(): Fade{
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade

        return fade
    }
}