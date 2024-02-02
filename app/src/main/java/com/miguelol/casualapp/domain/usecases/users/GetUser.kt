package com.miguelol.casualapp.domain.usecases.users

import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUser @Inject constructor(private val userRepo: UserRepository) {
    operator fun invoke(uid: String): Flow<Response<User>> =
        userRepo.getUser(uid).map{ resp ->
            when {
                resp is Success && resp.data != null -> Success(resp.data)
                resp is Success -> Error(Exception(Constants.USER_NOT_FOUND))
                else -> Error(Exception(Constants.DATABASE_ERROR))
            }
        }
}