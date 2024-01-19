package com.miguelol.casualapp.domain.repositories

import com.miguelol.casualapp.domain.model.Message
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.UserPreview
import kotlinx.coroutines.flow.Flow

typealias PlansPreview = List<Plan>?
typealias PlansPreviewResponse = Response<PlansPreview>

interface PlanRepository {

    fun getVisiblePlans(uid: String): Flow<Response<List<Plan>>>
    fun getPlan(planId: String): Flow<Response<Plan?>>
    fun getParticipants(planId: String): Flow<Response<List<UserPreview>>>
    fun getPrivatePlans(uid: String): Flow<Response<List<Plan>>>
    fun getMyPlans(uid: String): Flow<Response<List<Plan>>>
    fun getChat(planId: String): Flow<Response<List<Message>>>
    suspend fun createPlan(plan: Plan): Response<Boolean>
    suspend fun deletePlan(planId: String): Response<Unit>
    suspend fun deleteParticipant(planId: String, uid: String): Response<Unit>
    suspend fun addParticipant(planId: String, user: UserPreview): Response<Unit>
    suspend fun sendMessage(planId: String, message: Message): Response<Unit>
}