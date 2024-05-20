package com.miguelol.casualapp.domain.usecases.planRequests

import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreatePlanRequest @Inject constructor(
    private val requestRepo: PlanRequestRepository,
    private val userUseCases: UserUseCases,
    private val planUseCases: PlanUseCases
) {
    suspend operator fun invoke(fromUid: String, toUid: String, planId: String): Response<Unit> {

        //gets our information for filling the request
        val fromUser: UserPreview
        when (val resp = userUseCases.getUser(fromUid).first()) {
            is Error -> return resp
            is Success -> fromUser = resp.data.toPreview()
        }

        val plan: PlanPreview
        when(val resp = planUseCases.getPlan(planId).first()){
            is Error -> return Error(Exception(Constants.DATABASE_ERROR))
            is Success -> plan = resp.data!!.toPreview()
        }

        val requestId = "$fromUid$planId"
        val request = PlanRequest(requestId, fromUser, plan)
        return when (val resp = requestRepo.createPlanRequest(toUid, request)){
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}