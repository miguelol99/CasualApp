package com.miguelol.casualapp.domain.usecases.users

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsUsernameTaken @Inject constructor(private val userRepo: UserRepository) {
    suspend operator fun invoke(username: String): Response<Boolean> =
        when (val resp = userRepo.getUserByUsername(username).first()) {
            is Success -> Success(resp.data != null)
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
        }
}