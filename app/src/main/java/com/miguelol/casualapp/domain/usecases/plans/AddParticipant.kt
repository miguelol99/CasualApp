package com.miguelol.casualapp.domain.usecases.plans

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddParticipant @Inject constructor(
    private val planRepository: PlanRepository,
    private val userUseCases: UserUseCases
) {
    suspend operator fun invoke(planId: String, uid: String): Response<Unit> {

        val user: UserPreview
        when (val resp = userUseCases.getUser(uid).first()) {
            is Error -> return resp
            is Success -> user = resp.data.toPreview()
        }

        return when (val resp = planRepository.addParticipant(planId = planId, user = user)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }

    }
}