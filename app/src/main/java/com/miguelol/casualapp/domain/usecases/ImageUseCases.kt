package com.miguelol.casualapp.domain.usecases.images

import android.net.Uri
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.ImageRepository
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import java.lang.Exception
import javax.inject.Inject

data class ImageUseCases(
    val saveImage: SaveImage
)

class SaveImage @Inject constructor(
    private val imageRepository: ImageRepository
) {
   suspend operator fun invoke(uri: Uri, name: String): Response<Uri> =
       when (val resp = imageRepository.saveImage(uri, name)){
           is Success -> resp
           is Error -> Error(Exception(DATABASE_ERROR))
       }
}