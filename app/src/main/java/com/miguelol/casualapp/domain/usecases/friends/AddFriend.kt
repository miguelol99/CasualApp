package com.miguelol.casualapp.domain.usecases.friends

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.FriendsRepository
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

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
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}