package com.cuogne.wallpaperapplication.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.ui.adapter.PhotoAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerViewPhoto: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.photos.observe(this){ listPhoto ->
            recyclerViewPhoto = findViewById(R.id.recyclerViewPhoto)

            val adapter = PhotoAdapter(listPhoto)
            val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerViewPhoto.layoutManager = layout
            layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            recyclerViewPhoto.adapter = adapter
        }

        viewModel.getPhotos()
    }
}