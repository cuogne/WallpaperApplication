package com.cuogne.wallpaperapplication.ui.detail

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.transition.Fade
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import coil3.load
import coil3.request.placeholder
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.net.toUri
import com.cuogne.wallpaperapplication.ui.auth.AuthLoginGoogleFragment
import com.google.firebase.auth.FirebaseAuth

class DetailImageActivity : AppCompatActivity() {

    private lateinit var detailPhoto: ImageView
    private lateinit var descriptionImage: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnFullScreen: ImageButton
    private lateinit var btnShareImage: ImageButton
    private lateinit var btnSaveImage: ImageButton
    private lateinit var btnAddFavoriteImage: ImageButton
    private lateinit var viewModel: DetailImageViewModel
    private lateinit var fade: Fade
    private var currentPhotoUrl: String? = null
    private var currentPhotoId: String = ""
    private lateinit var auth: FirebaseAuth


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
        btnSaveImage = findViewById(R.id.button_save_image)
        btnFullScreen = findViewById(R.id.button_full_screen)
        btnShareImage = findViewById(R.id.button_share)
        btnAddFavoriteImage = findViewById(R.id.button_add_favorite)

        auth = FirebaseAuth.getInstance()
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
        viewModel.isFavorite.observe(this){isFav ->
            val icon = if (isFav){
                R.drawable.add_favorite
            }else{
                R.drawable.not_favorite
            }
            btnAddFavoriteImage.setImageResource(icon)
        }

        // thong bao khi click vao button them anh
        viewModel.favoriteMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearFavoriteMessage()
            }
        }

        viewModel.setPhoto(photoFromIntent)
        viewModel.checkFavoriteStatus(photoFromIntent?.id ?: "")

        btnBack.setOnClickListener {
            supportFinishAfterTransition()
        }

        btnFullScreen.setOnClickListener {
            val currentDrawable = detailPhoto.drawable
            if (currentDrawable != null) {
                val dialog = FullScreenImageDialog(this, currentDrawable)
                dialog.show()
            }
        }

        btnShareImage.setOnClickListener {
            shareImage()
        }

        btnSaveImage.setOnClickListener {
            val photo = getPhotoClicked()
            val imageUrl = photo?.urls?.regular
            val photoId = photo?.id ?: ""
            onSaveImageClicked(imageUrl, photoId)
        }

        btnAddFavoriteImage.setOnClickListener {
            handleFavoriteClick()
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

    private fun handleFavoriteClick() {
        val currentUser = auth.currentUser

        // chua login -> hien fragment login
        if (currentUser == null) {
            val loginFragment = AuthLoginGoogleFragment.newInstance()
            loginFragment.onLoginSuccessCallback = {
                viewModel.toggleFavoriteStatus()
            }
            loginFragment.show(
                supportFragmentManager,
                AuthLoginGoogleFragment.TAG
            )
        } else {
            viewModel.toggleFavoriteStatus()
        }
    }

    private fun fadeTransition(): Fade{
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade

        return fade
    }

    private fun shareImage() {
        val drawable = detailPhoto.drawable ?: return
        val bitmap = drawable.toBitmap()

        try {
            val cachePath = File(applicationContext.cacheDir, "images")
            cachePath.mkdirs()

            // luu file anh tam vao cache
            val file = File(cachePath, "image.png")
            val fileOutputStream = FileOutputStream(file)

            // ghi bitmap vao file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()

            // lay uri
            val imageUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                file
            )

            // dung intent + ACTION_SEND + createChooser
            if (imageUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun onSaveImageClicked(imageUrl: String?, photoId: String) {
        if (imageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Ảnh không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        currentPhotoUrl = imageUrl
        currentPhotoId = photoId

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // API < 29
            if (hasWritePermission()) {
                downloadImage(this, imageUrl, photoId)
            } else {
                requestWritePermission()
            }
        } else {
            // API >= 29
            downloadImage(this, imageUrl, photoId)
        }

        Toast.makeText(this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show()
    }

    // cho api > 29, dung downloadManager
    private fun downloadImage(context: Context, imageUrl: String, fileName: String){
        val request = DownloadManager.Request(imageUrl.toUri())
            .setTitle("Download photo")
            .setDescription("Saving image to gallery")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                "Wallpaper/$fileName.jpg"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager =
            context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)

        Toast.makeText(context, "Đang tải ảnh", Toast.LENGTH_SHORT).show()
    }


    // cho api < 29, xin quyen write external storage de luu anh
    private fun hasWritePermission(): Boolean {
        // check quyền write external storage
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1001
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                currentPhotoUrl?.let {
                    downloadImage(this, it, currentPhotoId)
                }
            } else {
                Toast.makeText(
                    this,
                    "Cần quyền lưu ảnh để tải ảnh",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}