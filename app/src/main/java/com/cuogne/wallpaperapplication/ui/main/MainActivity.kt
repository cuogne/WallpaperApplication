package com.cuogne.wallpaperapplication.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.ui.adapter.PhotoAdapter
import com.cuogne.wallpaperapplication.ui.auth.AuthLoginGoogleFragment
import com.cuogne.wallpaperapplication.ui.detail.DetailImageActivity
import com.cuogne.wallpaperapplication.ui.favorite.FavoritePhotoActivity
import com.cuogne.wallpaperapplication.utils.randomNumberPages
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerViewPhoto: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var titleApp: TextView
    private lateinit var searchEditText: EditText
    private lateinit var btnAccount: ImageButton
    private var startPage = 1
    private var searchJob: Job? = null
    private var query: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleApp = findViewById(R.id.titleApplication)
        recyclerViewPhoto = findViewById(R.id.recyclerViewPhoto)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        searchEditText = findViewById(R.id.searchBar)
        btnAccount = findViewById(R.id.btnAccount)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // cuon len dau khi click vao title app
        titleApp.setOnClickListener {
            recyclerViewPhoto.smoothScrollToPosition(0)
        }

        setupRecyclerView()
        observeViewModel()

        // pull to refresh
        swipeRefreshLayout.setOnRefreshListener {
            startPage = randomNumberPages()
            viewModel.getPhotos(startPage, isRefresh = true)
        }

        startPage = randomNumberPages()
        viewModel.getPhotos(startPage, isRefresh = true)

        setupSearch()

        btnAccount.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // chua login
                AuthLoginGoogleFragment.newInstance().show(supportFragmentManager,
                    AuthLoginGoogleFragment.TAG)
            } else {
                // login roi
                val intent = Intent(this, FavoritePhotoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener { editable ->
            searchJob?.cancel()

            searchJob = lifecycleScope.launch {
                delay(400)

                query = editable.toString().trim()
                startPage = randomNumberPages()

                if (query.length >= 2) {
                    viewModel.searchPhotos(query, startPage, isNewSearch = true)
                } else {
                    viewModel.getPhotos(startPage, isRefresh = true)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter { photo, view ->
            val intent = Intent(this, DetailImageActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                view,
                view.transitionName
            )
            intent.putExtra("photo", photo)
            startActivity(intent, options.toBundle())
        }

        val layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        ).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }

        recyclerViewPhoto.apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
            adapter = photoAdapter

            (itemAnimator as? SimpleItemAnimator)
                ?.supportsChangeAnimations = false
            itemAnimator = null

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy <= 0) return

                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItems = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

                    if (viewModel.isLoading.value == false &&
                        totalItemCount <= lastVisibleItem + 5
                    ) {
                        if (query.length < 2) {
                            loadMorePhotos()
                        } else {
                            loadMorePhotosQuery(query)
                        }
                    }
                }
            })
        }
    }

    private fun loadMorePhotos() {
        startPage++
        viewModel.getPhotos(startPage, isRefresh = false)
    }

    private fun loadMorePhotosQuery(query: String) {
        startPage++
        viewModel.searchPhotos(query, startPage, isNewSearch = false)
    }

    private fun observeViewModel() {
        viewModel.photos.observe(this) { photos ->
            photoAdapter.submitList(photos.toList())
        }

        viewModel.isLoading.observe(this) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }
    }
}