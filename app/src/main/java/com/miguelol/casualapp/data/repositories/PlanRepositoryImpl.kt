package com.miguelol.casualapp.data.repositories

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import com.miguelol.casualapp.utils.Constants.FRIENDS_OF_HOST
import com.miguelol.casualapp.utils.Constants.HOST
import com.miguelol.casualapp.utils.Constants.PLANS
import com.miguelol.casualapp.utils.Constants.PRIVATE
import com.miguelol.casualapp.utils.Constants.PUBLIC
import com.miguelol.casualapp.utils.Constants.TIMESTAMP
import com.miguelol.casualapp.utils.Constants.TYPE
import com.miguelol.casualapp.utils.Constants.USERNAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlanRepositoryImpl  @Inject constructor(
    private val db: FirebaseFirestore
) : PlanRepository  {

    private val plansRef = db.collection(PLANS)

    override fun getPublicPlans(uid: String): Flow<Response<List<Plan>>> =
        plansRef.where(
            Filter.or(
                Filter.equalTo(TYPE, PlanType.PUBLIC),
                Filter.and(
                    Filter.equalTo(TYPE, PlanType.PRIVATE),
                    Filter.arrayContains(FRIENDS_OF_HOST, uid)
                )
            )
        ).orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<Plan>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override fun getPrivatePlans(uid: String): Flow<Response<List<Plan>>> =
        plansRef.whereEqualTo(TYPE, PlanType.PRIVATE).whereArrayContains(FRIENDS_OF_HOST, uid)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<Plan>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override fun getPlansCreatedBy(uid: String): Flow<Response<List<Plan>>> =
        db.collection(PLANS).whereEqualTo("$HOST.$UID", uid)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<Plan>>> { Response.Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override suspend fun createPlan(plan: Plan): Response<Boolean> {
        return try {
            val docRef = db.collection(PLANS).document()
            val planWithId = plan.copy(id = docRef.id)
            docRef.set(planWithId).await()
            Response.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }
}