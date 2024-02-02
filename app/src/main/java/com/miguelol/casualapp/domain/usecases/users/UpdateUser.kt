package com.miguelol.casualapp.domain.usecases.users

import androidx.core.net.toUri
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.domain.usecases.images.ImageUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateUser @Inject constructor(
    private val userRepo: UserRepository,
    private val imageUseCases: ImageUseCases
) {
    suspend operator fun invoke(user: User, isNewImage: Boolean, originalUsername: String): Response<Unit> {

        //checks if the username is already taken
        if (originalUsername != user.username)
            when (val resp = userRepo.getUserByUsername(user.username).first()) {
                is Success -> if (resp.data != null) return Error(Exception(Constants.USERNAME_TAKEN))
                is Error -> return Error(Exception(Constants.DATABASE_ERROR))
            }

        if (isNewImage)
            when(val resp = imageUseCases.saveImage(user.image.toUri(), user.uid)) {
                is Success -> user.image = resp.data.toString()
                is Error -> return resp
            }

        return when (val resp = userRepo.updateUser(user)) {
            is Success -> resp
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
        }
    }
}