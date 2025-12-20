package com.cuogne.wallpaperapplication.ui.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt

class PhotoAdapter(
    private val onItemClick: ((PhotoModel) -> Unit)? = null
) : ListAdapter<PhotoModel, PhotoAdapter.ViewHolder>(DIFF) {
    companion object {
        val DIFF = object: DiffUtil.ItemCallback<PhotoModel>() {
            override fun areItemsTheSame(
                oldItem: PhotoModel,
                newItem: PhotoModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PhotoModel,
                newItem: PhotoModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val selectedPhoto = getItem(position)

        holder.photo.load(selectedPhoto.urls?.small){
            placeholder(selectedPhoto.color.toColorInt().toDrawable())
        } // use coil

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(selectedPhoto)
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.hashCode().toLong()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val photo: ImageView = itemView.findViewById(R.id.photo)
    }
}