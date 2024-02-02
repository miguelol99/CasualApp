package com.miguelol.casualapp.domain.usecases.friendRequests

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.domain.usecases.friends.FriendUseCases
import com.miguelol.casualapp.utils.Constants
import javax.inject.Inject

class AcceptRequest @Inject constructor(
    private val requestRepo: FriendRequestRepository,
    private val friendUseCases: FriendUseCases
) {
    suspend operator fun invoke(fromUid: String, toUid: String): Response<Unit> {

        //ads the users as friends
        when (val resp = friendUseCases.addFriend(fromUid, toUid)) {
            is Error -> return resp
            is Success -> Unit
        }

        //delete the request
        return when (val resp = requestRepo.deleteFriendRequest(fromUid, toUid)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}