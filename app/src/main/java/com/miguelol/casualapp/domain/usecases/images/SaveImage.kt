package com.miguelol.casualapp.domain.usecases.images

import android.net.Uri
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.repositories.ImageRepository
import com.miguelol.casualapp.utils.Constants
import java.lang.Exception
import javax.inject.Inject

class SaveImage @Inject constructor(private val imageRepository: ImageRepository) {

    suspend operator fun invoke(uri: Uri, name: String): Response<Uri> =
        when (val resp = imageRepository.saveImage(uri, name)){
            is Success -> resp
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
        }
}