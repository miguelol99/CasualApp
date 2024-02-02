package com.miguelol.casualapp.domain.usecases.friends

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.FriendsRepository
import com.miguelol.casualapp.utils.Constants
import javax.inject.Inject

class DeleteFriend @Inject constructor(private val friendsRepo: FriendsRepository) {
    suspend operator fun invoke(myUid: String, friendUid: String): Response<Unit> =
        when (val resp = friendsRepo.deleteFriend(myUid, friendUid)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
}