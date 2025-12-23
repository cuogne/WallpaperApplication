package com.cuogne.wallpaperapplication.ui.detail

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.cuogne.wallpaperapplication.R

class FullScreenImageDialog(
    context: Context,
    private val imageDrawable: Drawable?
) : Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_fullscreen_image)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val fullscreenImageView = findViewById<ImageView>(R.id.fullscreen_image_view)
        fullscreenImageView.setImageDrawable(imageDrawable)

        fullscreenImageView.setOnClickListener {
            dismiss()
        }
    }
}
