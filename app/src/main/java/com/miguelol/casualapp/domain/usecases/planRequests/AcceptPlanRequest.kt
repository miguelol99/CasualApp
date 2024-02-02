package com.miguelol.casualapp.domain.usecases.planRequests

import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AcceptPlanRequest @Inject constructor(
    private val requestRepo: PlanRequestRepository,
    private val planUseCases: PlanUseCases
) {
    suspend operator fun invoke(toUid: String, requestId: String): Response<Unit> {

        val request: PlanRequest
        when(val resp = requestRepo.getPlanRequest(toUid = toUid, requestId = requestId).first()){
            is Error ->  return Error(Exception(Constants.DATABASE_ERROR))
            is Success -> if (resp.data != null) request = resp.data else return Error(Exception("Request not found"))
        }

        //ads the users as friends
        val planId = request.plan.id
        when (val resp = planUseCases.addParticipant(planId = planId, uid = request.fromUser.uid)) {
            is Error -> return resp
            is Success -> Unit
        }

        //delete the request
        return when (val resp = requestRepo.deletePlanRequest(toUid, requestId)) {
            is Error -> Error(Exception(Constants.DATABASE_ERROR))
            is Success -> resp
        }
    }
}