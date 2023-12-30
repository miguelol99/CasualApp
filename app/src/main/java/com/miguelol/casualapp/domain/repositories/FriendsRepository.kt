package com.miguelol.casualapp.domain.repositories

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.UserPreview
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {

    fun getFriends(uid: String): Flow<Response<List<UserPreview>>>
    fun getFriend(uid1: String, uid2: String): Flow<Response<UserPreview?>>
    suspend fun addFriend(user1: UserPreview, user2: UserPreview): Response<Unit>
    suspend fun deleteFriend(uid1: String, uid2: String): Response<Unit>

}