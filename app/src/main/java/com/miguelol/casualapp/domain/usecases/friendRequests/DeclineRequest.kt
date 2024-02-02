package com.miguelol.casualapp.domain.usecases.friendRequests

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import javax.inject.Inject

class DeclineRequest @Inject constructor(private val requestRepo: FriendRequestRepository) {
    suspend operator fun invoke(fromUid: String, toUid: String): Response<Unit> =
        when (val resp = requestRepo.deleteFriendRequest(fromUid, toUid)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
}