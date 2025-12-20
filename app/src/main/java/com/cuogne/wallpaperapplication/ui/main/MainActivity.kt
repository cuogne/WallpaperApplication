package com.cuogne.wallpaperapplication.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.ui.adapter.PhotoAdapter
import com.cuogne.wallpaperapplication.ui.detail.DetailImageActivity
import com.cuogne.wallpaperapplication.utils.randomNumberPages

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerViewPhoto: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var titleApp: TextView
    private var startPage = randomNumberPages()
    private val pagesToLoad = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleApp = findViewById(R.id.titleApplication)
        recyclerViewPhoto = findViewById(R.id.recyclerViewPhoto)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        titleApp.setOnClickListener {
            // cuon len dau khi click vao title app
            recyclerViewPhoto.smoothScrollToPosition(0)
        }

        setupRecyclerView()

        observeViewModel()

        swipeRefreshLayout.setOnRefreshListener {
            startPage = randomNumberPages()
            val pages = (startPage until startPage + pagesToLoad).toList()
            viewModel.getPhotos(pages, isRefresh = true)
        }

        val initialPages = (startPage until startPage + pagesToLoad).toList()
        viewModel.getPhotos(initialPages, isRefresh = true)
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter{
            val intent = Intent(this, DetailImageActivity::class.java)
            intent.putExtra("photo", it)
            startActivity(intent)
        }

        val layout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layout.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerViewPhoto.setHasFixedSize(true)
        recyclerViewPhoto.layoutManager = layout
        recyclerViewPhoto.adapter = photoAdapter

        // disable animation
        (recyclerViewPhoto.itemAnimator as? SimpleItemAnimator) ?.supportsChangeAnimations = false
        recyclerViewPhoto.itemAnimator = null

        // scroll listener
        recyclerViewPhoto.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) { // dy > 0 la dang scroll xuong
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val totalItemCount = layoutManager.itemCount

                    val lastVisibleItems = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

                    if (viewModel.isLoading.value == false && totalItemCount <= lastVisibleItem + 5) {
                        loadMorePhotos()
                    }
                }
            }
        })
    }

    private fun loadMorePhotos() {
        startPage += pagesToLoad
        val newPages = (startPage until startPage + pagesToLoad).toList()
        viewModel.getPhotos(newPages, isRefresh = false) // isRefresh = false
    }

    private fun observeViewModel() {
        viewModel.photos.observe(this) { listPhoto ->
            photoAdapter.submitList(listPhoto.toList())
        }

        viewModel.isLoading.observe(this) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }
    }
}