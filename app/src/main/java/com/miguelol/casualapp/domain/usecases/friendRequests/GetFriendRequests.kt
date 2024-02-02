package com.miguelol.casualapp.domain.usecases.friendRequests

import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFriendRequests @Inject constructor(private val requestRepo: FriendRequestRepository) {
    operator fun invoke(uid: String): Flow<Response<List<FriendRequest>>> =
        requestRepo.getFriendRequests(uid).map { resp ->
            when(resp){
                is Error -> Error(Exception(Constants.DATABASE_ERROR))
                is Success -> resp
            }
        }
}