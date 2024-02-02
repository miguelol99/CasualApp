package com.miguelol.casualapp.domain.usecases.plans

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.utils.Constants
import javax.inject.Inject

class DeleteParticipant @Inject constructor(
    private val planRepository: PlanRepository
) {
    suspend operator fun invoke(planId: String, uid: String): Response<Unit> =
        when (val resp = planRepository.deleteParticipant(planId, uid)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
}