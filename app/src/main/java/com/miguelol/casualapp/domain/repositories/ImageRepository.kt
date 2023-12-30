package com.miguelol.casualapp.domain.repositories

import android.net.Uri
import com.miguelol.casualapp.domain.model.Response

interface ImageRepository {

    suspend fun saveImage(uri: Uri, name: String): Response<Uri>

}