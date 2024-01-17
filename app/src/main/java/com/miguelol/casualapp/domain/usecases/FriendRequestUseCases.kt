package com.miguelol.casualapp.domain.usecases

import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class FriendRequestUseCases(
    val getFriendRequests: GetFriendRequests,
    val acceptRequest: AcceptRequest,
    val declineRequest: DeclineRequest,
    val createRequest: CreateRequest,
    val getFriendState: GetFriendState
)

class GetFriendRequests @Inject constructor(private val requestRepo: FriendRequestRepository) {
    operator fun invoke(uid: String): Flow<Response<List<FriendRequest>>> =
        requestRepo.getFriendRequests(uid).map { resp ->
            when(resp){
                is Error -> Error(Exception(DATABASE_ERROR))
                is Success -> resp
            }
        }
}

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
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}

class DeclineRequest @Inject constructor(private val requestRepo: FriendRequestRepository) {
    suspend operator fun invoke(fromUid: String, toUid: String): Response<Unit> =
        when (val resp = requestRepo.deleteFriendRequest(fromUid, toUid)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
}

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
                is Error -> Error(Exception(DATABASE_ERROR))
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
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}

class GetFriendState @Inject constructor(
    private val requestRepo: FriendRequestRepository,
    private val friendUseCases: FriendUseCases
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(myUid: String, otherUid: String): Flow<Response<RequestState>> {

        //checks if they are already friends
        return friendUseCases.getFriend(myUid, otherUid).flatMapLatest { resp ->
            when {
                resp is Success && resp.data != null ->
                    flowOf( Success(RequestState.ACCEPTED) )

                //checks if there is a pending request
                resp is Success && resp.data == null ->
                    requestRepo.getFriendRequest(myUid, otherUid).map { req ->
                        when {

                            req is Success && req.data != null ->
                                Success(RequestState.PENDING)

                            req is Success && req.data == null ->
                                Success(RequestState.NOT_SENT)

                            else -> Error((req as Error).e)
                        }
                    }

                else -> flowOf( Error((resp as Error).e))
            }
        }
    }
}


