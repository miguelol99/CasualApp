package com.miguelol.casualapp.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.utils.Constants.PARTICIPANTS
import com.miguelol.casualapp.utils.Constants.PLANS
import com.miguelol.casualapp.utils.Constants.TIMESTAMP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlanRepositoryImpl  @Inject constructor(
    private val db: FirebaseFirestore
) : PlanRepository  {

    override fun getVisiblePlans(uid: String): Flow<Response<List<PlanPreview>>> =
        db.collection(PLANS).orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<PlanPreview>>> { Response.Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override fun getUserPlans(uid: String): Flow<Response<List<PlanPreview>>> =
        db.collection(PLANS).whereArrayContains(PARTICIPANTS, uid)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<PlanPreview>>> { Response.Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override suspend fun createPlan(plan: PlanPreview): Response<Boolean> {
        return try {
            val docRef = db.collection(PLANS).document()
            val planWithId = plan.copy(id = docRef.toString())
            docRef.set(planWithId).await()
            Response.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }
}