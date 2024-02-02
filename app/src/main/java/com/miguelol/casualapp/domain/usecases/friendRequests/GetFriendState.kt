package com.miguelol.casualapp.domain.usecases.friendRequests

import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.domain.usecases.friends.FriendUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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