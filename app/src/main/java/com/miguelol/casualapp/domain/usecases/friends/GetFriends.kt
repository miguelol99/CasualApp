package com.miguelol.casualapp.domain.usecases.friends

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.FriendsRepository
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFriends @Inject constructor(private val friendsRepo: FriendsRepository) {
    operator fun invoke(uid: String): Flow<Response<List<UserPreview>>> =
        friendsRepo.getFriends(uid).map { resp ->
            when (resp) {
                is Error -> Error(Exception(Constants.DATABASE_ERROR))
                is Success -> resp
            }
        }
}