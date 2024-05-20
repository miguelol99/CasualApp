package com.miguelol.casualapp.data.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.utils.Constants
import com.miguelol.casualapp.utils.Constants.PLAN_REQUESTS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlanRequestRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : PlanRequestRepository {

    private val usersRef = database.collection(Constants.USERS)

    override fun getPlanRequests(uid: String): Flow<Response<List<PlanRequest>>> =
        usersRef.document(uid).collection(PLAN_REQUESTS)
            .orderBy(Constants.TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<PlanRequest>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    val transform: (QuerySnapshot) -> Response<List<PlanRequest>> = {Success(it.toObjects())}

    override fun getPlanRequest(toUid: String, requestId: String): Flow<Response<PlanRequest?>> =
        usersRef.document(toUid).collection(PLAN_REQUESTS).document(requestId)
            .snapshots()
            .map<DocumentSnapshot, Response<PlanRequest?>> { Success(it.toObject()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override suspend fun createPlanRequest(toUid: String, request: PlanRequest): Response<Unit> {
        return try {
            emptyFlow<Integer>().map {  }
            usersRef.document(toUid).collection(PLAN_REQUESTS).document(request.id).set(request).await()
            Success(Unit)
        } catch (e: Exception){
            e.printStackTrace()
            Error(e)
        }
    }

    override suspend fun deletePlanRequest(toUid: String, requestId: String): Response<Unit> {
        return try {
            usersRef.document(toUid).collection(PLAN_REQUESTS).document(requestId).delete().await()
            Success(Unit)
        } catch (e: Exception){
            e.printStackTrace()
            Error(e)
        }
    }
}