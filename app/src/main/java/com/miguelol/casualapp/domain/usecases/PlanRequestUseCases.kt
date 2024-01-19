package com.miguelol.casualapp.domain.usecases

import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.utils.Constants.DATABASE_ERROR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class PlanRequestUseCases(
    val getRequests: GetPlanRequests,
    val createRequest: CreatePlanRequest,
    val declineRequest: DeclinePlanRequest,
    val acceptRequest: AcceptPlanRequest,
    val getRequestState: GetRequestState
)

class GetPlanRequests @Inject constructor(private val requestRepo: PlanRequestRepository) {
    operator fun invoke(uid: String): Flow<Response<List<PlanRequest>>> =
        requestRepo.getPlanRequests(uid).map { resp ->
            when(resp){
                is Error -> Error(Exception(DATABASE_ERROR))
                is Success -> resp
            }
        }
}

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
            is Error -> return Error(Exception(DATABASE_ERROR))
            is Success -> plan = resp.data!!.toPreview()
        }

        val requestId = "$fromUid$planId"
        val request = PlanRequest(requestId, fromUser, plan)
        return when (val resp = requestRepo.createPlanRequest(toUid, request)){
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}

class DeclinePlanRequest @Inject constructor(private val requestRepo: PlanRequestRepository) {
    suspend operator fun invoke(toUid: String, fromUid: String, planId: String): Response<Unit> {
        val requestId = "$fromUid$planId"
        return when (val resp = requestRepo.deletePlanRequest(toUid, requestId)) {
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}

class AcceptPlanRequest @Inject constructor(
    private val requestRepo: PlanRequestRepository,
    private val planUseCases: PlanUseCases
) {
    suspend operator fun invoke(toUid: String, requestId: String): Response<Unit> {

        val request: PlanRequest
        when(val resp = requestRepo.getPlanRequest(toUid = toUid, requestId = requestId).first()){
            is Error ->  return Error(Exception(DATABASE_ERROR))
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
            is Error -> Error(Exception(DATABASE_ERROR))
            is Success -> resp
        }
    }
}

class GetRequestState @Inject constructor(
    private val requestRepo: PlanRequestRepository,
    private val planUseCases: PlanUseCases,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(fromUid: String, toUid: String, planId: String): Flow<Response<RequestState>> {
        val requestId = "$fromUid$planId"
        return planUseCases.getParticipants(planId).flatMapLatest { resp ->
            when (resp) {
                is Success -> {
                    if (resp.data.any { it.uid == fromUid }) {
                        flowOf(Success(RequestState.ACCEPTED))
                    } else {
                        requestRepo.getPlanRequest(toUid, requestId).map {
                            when(it) {
                                is Success -> when(it.data) {
                                    null -> Success(RequestState.NOT_SENT)
                                    else -> Success(RequestState.PENDING)
                                }
                                is Error -> Error(Exception(DATABASE_ERROR))
                            }
                        }
                    }
                }
                is Error -> flowOf(resp)
            }
        }
    }
}