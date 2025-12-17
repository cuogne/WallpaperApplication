package com.cuogne.wallpaperapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.cuogne.wallpaperapplication.R
import com.cuogne.wallpaperapplication.data.model.PhotoModel

class PhotoAdapter(
    private var listPhotos: List<PhotoModel>,
    private val onItemClick: ((PhotoModel) -> Unit)? = null
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
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
        val selectedPhoto = listPhotos[position]

        holder.photo.load(selectedPhoto.urls?.small) // use coil

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(selectedPhoto)
        }
    }

    override fun getItemCount(): Int {
        return listPhotos.size
    }

    fun updatePhotos(newPhotos: List<PhotoModel>) {
        this.listPhotos = newPhotos
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val photo: ImageView = itemView.findViewById(R.id.photo)
    }
}