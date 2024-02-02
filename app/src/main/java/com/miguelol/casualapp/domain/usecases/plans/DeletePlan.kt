package com.miguelol.casualapp.domain.usecases.plans

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.utils.Constants
import javax.inject.Inject

class DeletePlan @Inject constructor(
    private val planRepository: PlanRepository,
) {
    suspend operator fun invoke(planId: String): Response<Unit> {
        return when(val resp = planRepository.deletePlan(planId)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}