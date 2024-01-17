package com.miguelol.casualapp.domain.repositories

import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface PlanRequestRepository {

    fun getPlanRequests(uid: String): Flow<Response<List<PlanRequest>>>
    fun getPlanRequest(toUid: String, requestId: String): Flow<Response<PlanRequest?>>
    suspend fun createPlanRequest(toUid: String, request: PlanRequest): Response<Unit>
    suspend fun deletePlanRequest(toUid: String, requestId: String): Response<Unit>
}