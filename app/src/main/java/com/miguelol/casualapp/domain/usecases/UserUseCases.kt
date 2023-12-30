package com.miguelol.casualapp.domain.usecases

import androidx.core.net.toUri
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.domain.usecases.images.ImageUseCases
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import com.miguelol.casualapp.utils.Constants.USERNAME_TAKEN
import com.miguelol.casualapp.utils.Constants.USER_NOT_FOUND
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserUseCases(
    val getUser: GetUser,
    val isUsernameTaken: IsUsernameTaken,
    val updateUser: UpdateUser,
    val searchUsers: SearchUsers
)

class GetUser @Inject constructor(private val userRepo: UserRepository) {
    operator fun invoke(uid: String): Flow<Response<User>> =
        userRepo.getUser(uid).map{ resp ->
            when {
                resp is Success && resp.data != null -> Success(resp.data)
                resp is Success -> Error(Exception(USER_NOT_FOUND))
                else -> Error(Exception(DATABASE_ERROR))
            }
        }
}

class UpdateUser @Inject constructor(
    private val userRepo: UserRepository,
    private val imageUseCases: ImageUseCases
) {
    suspend operator fun invoke(user: User, isNewImage: Boolean, originalUsername: String): Response<Unit> {

        //checks if the username is already taken
        if (originalUsername != user.username)
            when (val resp = userRepo.getUserByUsername(user.username).first()) {
                is Success -> if (resp.data != null) return Error(Exception(USERNAME_TAKEN))
                is Error -> return Error(Exception(DATABASE_ERROR))
            }

        if (isNewImage)
            when(val resp = imageUseCases.saveImage(user.image.toUri(), user.uid)) {
                is Success -> user.image = resp.data.toString()
                is Error -> return resp
            }

        return when (val resp = userRepo.updateUser(user)) {
            is Success -> resp
            is Error -> Error(Exception(DATABASE_ERROR))
        }
    }
}

class IsUsernameTaken @Inject constructor(private val userRepo: UserRepository) {
    suspend operator fun invoke(username: String): Response<Boolean> =
        when (val resp = userRepo.getUserByUsername(username).first()) {
            is Success -> Success(resp.data != null)
            is Error -> Error(Exception(DATABASE_ERROR))
        }
}

class SearchUsers @Inject constructor(private val userRepo: UserRepository) {
    operator fun invoke(term: String): Flow<Response<List<User>>> {

        if (term.isBlank()) return flow { emit(Success(emptyList())) }

        return userRepo.getUsersThatMatch(term).map { resp ->
            when (resp) {
                is Success -> resp
                is Error -> Error(Exception(DATABASE_ERROR))
            }
        }
    }
}



