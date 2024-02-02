package com.miguelol.casualapp.domain.usecases.planRequests

import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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
                                is Error -> Error(Exception(Constants.DATABASE_ERROR))
                            }
                        }
                    }
                }
                is Error -> flowOf(resp)
            }
        }
    }
}