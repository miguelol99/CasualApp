package com.miguelol.casualapp.domain.repositories

import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface FriendRequestRepository {

    fun getFriendRequests(uid: String): Flow<Response<List<FriendRequest>>>
    fun getFriendRequest(fromUid: String, toUid: String): Flow<Response<FriendRequest?>>
    suspend fun createFriendRequest(toUid: String, request: FriendRequest): Response<Unit>
    suspend fun deleteFriendRequest(fromUid: String, toUid: String): Response<Unit>

}