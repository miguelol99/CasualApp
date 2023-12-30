package com.miguelol.casualapp.domain.repositories

import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

typealias PlansPreview = List<PlanPreview>?
typealias PlansPreviewResponse = Response<PlansPreview>

interface PlanRepository {

    fun getVisiblePlans(uid: String): Flow<Response<List<PlanPreview>>>
    fun getUserPlans(uid: String): Flow<Response<List<PlanPreview>>>
    suspend fun createPlan(plan: PlanPreview): Response<Boolean>
}