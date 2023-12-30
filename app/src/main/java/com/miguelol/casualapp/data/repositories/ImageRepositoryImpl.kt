package com.miguelol.casualapp.data.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.repositories.ImageRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
): ImageRepository {

    private val storageRef = storage.reference

    override suspend fun saveImage(uri: Uri, name: String): Response<Uri> {
        return try {
            val taskSnapshot = storageRef.child(name).putFile(uri).await()
            val downloadUrl = taskSnapshot.storage.downloadUrl.await()
            Response.Success(downloadUrl)
        } catch(e:Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }


}