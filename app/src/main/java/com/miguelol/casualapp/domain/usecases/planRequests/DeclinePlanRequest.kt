package com.miguelol.casualapp.domain.usecases.planRequests

import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.utils.Constants
import javax.inject.Inject

class DeclinePlanRequest @Inject constructor(private val requestRepo: PlanRequestRepository) {
    suspend operator fun invoke(toUid: String, fromUid: String, planId: String): Response<Unit> {
        val requestId = "$fromUid$planId"
        return when (val resp = requestRepo.deletePlanRequest(toUid, requestId)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}