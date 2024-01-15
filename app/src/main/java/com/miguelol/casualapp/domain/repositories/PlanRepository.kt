package com.miguelol.casualapp.domain.repositories

import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

typealias PlansPreview = List<Plan>?
typealias PlansPreviewResponse = Response<PlansPreview>

interface PlanRepository {

    fun getPublicPlans(uid: String): Flow<Response<List<Plan>>>

    fun getPrivatePlans(uid: String): Flow<Response<List<Plan>>>
    fun getPlansCreatedBy(uid: String): Flow<Response<List<Plan>>>
    suspend fun createPlan(plan: Plan): Response<Boolean>
}