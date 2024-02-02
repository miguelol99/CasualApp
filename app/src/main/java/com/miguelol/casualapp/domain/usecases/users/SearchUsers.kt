package com.miguelol.casualapp.domain.usecases.users

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchUsers @Inject constructor(private val userRepo: UserRepository) {
    operator fun invoke(term: String): Flow<Response<List<User>>> {

        if (term.isBlank()) return flow { emit(Success(emptyList())) }

        return userRepo.getUsersThatMatch(term).map { resp ->
            when (resp) {
                is Success -> resp
                is Error -> Error(Exception(Constants.DATABASE_ERROR))
            }
        }
    }
}