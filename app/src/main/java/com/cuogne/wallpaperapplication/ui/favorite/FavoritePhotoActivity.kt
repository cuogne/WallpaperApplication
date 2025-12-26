package com.cuogne.wallpaperapplication.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cuogne.wallpaperapplication.R
import com.google.firebase.auth.FirebaseAuth
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cuogne.wallpaperapplication.ui.adapter.PhotoAdapter
import com.cuogne.wallpaperapplication.ui.detail.DetailImageActivity
import com.cuogne.wallpaperapplication.ui.main.MainActivity
import android.widget.Toast
import kotlinx.coroutines.launch

class FavoritePhotoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var btnLogout: ImageButton
    private lateinit var btnBackInProfile: ImageButton
    private lateinit var helloUser: TextView
    private lateinit var recyclerViewFavoritePhoto: RecyclerView
    private lateinit var favoritePhotoAdapter: PhotoAdapter
    private lateinit var viewModel: FavoritePhotoViewModel
    private var isFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        btnLogout = findViewById(R.id.btnLogout)
        btnBackInProfile = findViewById(R.id.btnBackInProfile)
        helloUser = findViewById(R.id.helloUser)
        recyclerViewFavoritePhoto = findViewById(R.id.recyclerViewFavoritePhoto)
        viewModel = ViewModelProvider(this)[FavoritePhotoViewModel::class.java]

        val currentUser = auth.currentUser
        helloUser.text = "Hello, ${currentUser?.displayName}"

        btnLogout.setOnClickListener {
            logout()
        }

        btnBackInProfile.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        observeViewModel()
        viewModel.loadFavoritePhoto()
    }

    override fun onResume() {
        super.onResume()
        // reload de dam bao du lieu
        viewModel.loadFavoritePhoto()
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
            } catch (e: ClearCredentialException) {
                Log.e("FavoritePhotoActivity", "Clear credential failed", e)
            } finally {
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        favoritePhotoAdapter = PhotoAdapter { photo, _ ->
            val intent = Intent(this, DetailImageActivity::class.java)
            intent.putExtra("photo", photo)
            startActivity(intent)
        }
        recyclerViewFavoritePhoto.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerViewFavoritePhoto.adapter = favoritePhotoAdapter
    }

    private fun observeViewModel() {
        viewModel.favoritePhoto.observe(this) { favPhoto ->
            if (favPhoto.isEmpty() && isFirstLoad) {
                Toast.makeText(this, "Chưa có ảnh yêu thích nào", Toast.LENGTH_SHORT).show()
                isFirstLoad = false
            }
            favoritePhotoAdapter.submitList(favPhoto)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            Log.d("FavoritePhotoActivity", "Loading state: $isLoading")
        }
    }
}
