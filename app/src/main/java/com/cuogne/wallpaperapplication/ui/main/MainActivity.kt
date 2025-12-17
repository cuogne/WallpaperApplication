package com.cuogne.wallpaperapplication.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.ui.adapter.PhotoAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerViewPhoto: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewPhoto = findViewById(R.id.recyclerViewPhoto)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()

        observeViewModel()

        swipeRefreshLayout.setOnRefreshListener {
            val startPage = randomNumberPages()
            val endPage = startPage + 5
            viewModel.getPhotos(startPage, endPage)
        }

        swipeRefreshLayout.isRefreshing = true
        viewModel.getPhotos(1, 5)
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(emptyList())
        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerViewPhoto.layoutManager = layout
        recyclerViewPhoto.adapter = photoAdapter
    }

    private fun observeViewModel() {
        viewModel.photos.observe(this) { listPhoto ->
            photoAdapter.updatePhotos(listPhoto)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun randomNumberPages(): Int{
        return (1..20).random()
    }
}