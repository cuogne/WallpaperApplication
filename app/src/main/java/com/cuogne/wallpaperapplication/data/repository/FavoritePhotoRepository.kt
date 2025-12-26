package com.cuogne.wallpaperapplication.data.repository

import android.util.Log
import com.cuogne.wallpaperapplication.data.model.PhotoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritePhotoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId: String? get() = auth.currentUser?.uid

    suspend fun getAllFavoritePhotos(): Result<List<PhotoModel>>{
        val currentUserId = userId ?: return Result.failure(Exception("User not authenticated"))
        return try {
            val querySnapshot = db.collection("users")
                .document(currentUserId)
                .collection("favorites")
                .get()
                .await()

            val favorites = querySnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data

                    val urlsData = data?.get("urls") as? Map<*, *>
                    val urls = urlsData?.let {
                        PhotoModel.PhotoUrl(
                            raw = it["raw"] as? String ?: "",
                            full = it["full"] as? String ?: "",
                            regular = it["regular"] as? String ?: "",
                            small = it["small"] as? String ?: "",
                            thumb = it["thumb"] as? String ?: ""
                        )
                    }

                    PhotoModel(
                        id = data?.get("id") as? String ?: document.id,
                        width = (data?.get("width") as? Long)?.toInt() ?: 0,
                        height = (data?.get("height") as? Long)?.toInt() ?: 0,
                        description = data?.get("alt_description") as? String ?: data?.get("description") as? String,
                        color = data?.get("color") as? String ?: "#000000",
                        urls = urls
                    )
                } catch (e: Exception) {
                    Log.e("FavoriteRepository", "Error parsing document ${document.id}", e)
                    e.printStackTrace()
                    null
                }
            }
            Log.d("FavoriteRepository", "Successfully loaded ${favorites.size} favorites")
            Result.success(favorites)
        }
        catch (e: Exception){
            Log.e("FavoriteRepository", "Error loading favorites", e)
            Result.failure(e)
        }
    }

    // click vao nut yeu thich
    suspend fun addFavorite(photo: PhotoModel): Result<Unit> {
        val currentUserId = userId ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))

        return try {
            db.collection("users").document(currentUserId)
                .collection("favorites").document(photo.id)
                .set(photo)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Lỗi khi thêm ảnh yêu thích", e)
            Result.failure(e)
        }
    }

    // xoa anh yeu thich (click vao nut yeu thich 1 lan nua)
    suspend fun removeFavorite(photoId: String): Result<Unit> {
        val currentUserId = userId ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))

        return try {
            db.collection("users").document(currentUserId)
                .collection("favorites").document(photoId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Lỗi khi xóa ảnh yêu thích", e)
            Result.failure(e)
        }
    }

    // kiem tra 1 buc anh co trong danh sach yeu thich hay khong
    suspend fun isFavorite(photoId: String): Result<Boolean> {
        val currentUserId = userId ?: return Result.success(false)

        return try {
            val document = db.collection("users").document(currentUserId)
                .collection("favorites").document(photoId)
                .get()
                .await()
            Result.success(document.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}