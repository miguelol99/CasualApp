package com.miguelol.casualapp.domain.usecases


import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.FriendsRepository
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class FriendUseCases(
    val getFriends: GetFriends,
    val getFriend: GetFriend,
    val deleteFriend: DeleteFriend,
    val addFriend: AddFriend
)

class GetFriends @Inject constructor(private val friendsRepo: FriendsRepository) {
    operator fun invoke(uid: String): Flow<Response<List<UserPreview>>> =
        friendsRepo.getFriends(uid).map { resp ->
            when (resp) {
                is Error -> Error(Exception(DATABASE_ERROR))
                is Success -> resp
            }
        }
}

class GetFriend @Inject constructor(private val friendsRepo: FriendsRepository) {
    operator fun invoke(uid1: String, uid2: String): Flow<Response<UserPreview?>> =
        friendsRepo.getFriend(uid1, uid2).map { resp ->
            when (resp) {
                is Error -> Error(Exception(DATABASE_ERROR))
                is Success -> resp
            }
        }
}

class AddFriend @Inject constructor(
    private val friendsRepo: FriendsRepository,
    private val userUseCases: UserUseCases
) {
    suspend operator fun invoke(myUid: String, friendUid: String): Response<Unit> {

        val myUser: UserPreview
        val friendUser: UserPreview

        when(val resp = userUseCases.getUser(myUid).first()) {
            is Success -> myUser = resp.data.toPreview()
            is Error -> return resp
        }

        when(val resp = userUseCases.getUser(friendUid).first()) {
            is Success -> friendUser = resp.data.toPreview()
            is Error -> return resp
        }

        return when(val resp = friendsRepo.addFriend(myUser, friendUser)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}

class DeleteFriend @Inject constructor(private val friendsRepo: FriendsRepository) {
    suspend operator fun invoke(myUid: String, friendUid: String): Response<Unit> =
        when (val resp = friendsRepo.deleteFriend(myUid, friendUid)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
}