package com.miguelol.casualapp.domain.usecases.plans

import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.Message
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val planRepository: PlanRepository,
    private val userUseCases: UserUseCases
) {
    suspend operator fun invoke(planId: String, text: String, fromUid: String): Response<Unit> {

        var message = Message(
            timestamp = Timestamp.now(),
            message = text
        )

        when (val resp = userUseCases.getUser(fromUid).first()) {
            is Error -> return resp
            is Success -> message = message.copy(fromUser = resp.data.toPreview())
        }

        return when(val resp = planRepository.sendMessage(planId, message)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}