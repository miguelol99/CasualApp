package com.miguelol.casualapp.domain.repositories

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.User
import kotlinx.coroutines.flow.Flow

typealias UsersResponse = Response<List<User>>

interface UserRepository {

   fun getUser(uid: String): Flow<Response<User?>>
   fun getUserByUsername(username: String): Flow<Response<User?>>
   fun getUsersThatMatch(searchTerm: String): Flow<Response<List<User>>>
   suspend fun updateUser(user: User): Response<Unit>
}