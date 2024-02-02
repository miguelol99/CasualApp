package com.miguelol.casualapp.domain.usecases.friendRequests

import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.domain.usecases.friends.FriendUseCases
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateRequest @Inject constructor(
    private val requestRepo: FriendRequestRepository,
    private val friendUseCases: FriendUseCases,
    private val userUseCases: UserUseCases
) {
    suspend operator fun invoke(fromUid: String, toUid: String): Response<Unit> {

        //Checks if there is already a request from the other user
        var thereIsAnotherRequest = false
        when (val resp = requestRepo.getFriendRequest(toUid, fromUid).first()) {
            is Response.Success -> if (resp.data != null) thereIsAnotherRequest = true
            is Response.Error -> return resp
        }

        //if there is, it adds them as friends automatically and delete both requests
        if (thereIsAnotherRequest) {

            //add the users as friends
            when (val resp = friendUseCases.addFriend(fromUid, toUid)) {
                is Response.Error -> return resp
                is Response.Success -> Unit
            }

            //deletes both requests and RETURNS
            return when (val resp = requestRepo.deleteFriendRequest(fromUid, toUid)) {
                is Error -> Error(Exception(Constants.DATABASE_ERROR))
                is Success -> resp
            }
        }

        //gets our information for filling the request
        val fromUser: UserPreview
        when (val resp = userUseCases.getUser(fromUid).first()) {
            is Response.Success -> fromUser = resp.data.toPreview()
            is Response.Error -> return resp
        }

        //creates a friend request
        return when (val resp = requestRepo.createFriendRequest(toUid, FriendRequest(fromUser))){
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}