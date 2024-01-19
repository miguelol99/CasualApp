package com.miguelol.casualapp.data.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Message
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import com.miguelol.casualapp.utils.Constants.CHAT
import com.miguelol.casualapp.utils.Constants.FRIENDS_OF_HOST
import com.miguelol.casualapp.utils.Constants.HOST
import com.miguelol.casualapp.utils.Constants.PARTICIPANTS
import com.miguelol.casualapp.utils.Constants.PLANS
import com.miguelol.casualapp.utils.Constants.TIMESTAMP
import com.miguelol.casualapp.utils.Constants.TYPE
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlanRepositoryImpl  @Inject constructor(
    private val db: FirebaseFirestore
) : PlanRepository  {

    private val plansRef = db.collection(PLANS)

    override fun getVisiblePlans(uid: String): Flow<Response<List<Plan>>> =
        plansRef.where(
            Filter.or(
                Filter.equalTo(TYPE, PlanType.PUBLIC),
                Filter.and(
                    Filter.equalTo(TYPE, PlanType.PRIVATE),
                    Filter.arrayContains(FRIENDS_OF_HOST, uid),
                )
            )
        ).orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<Plan>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override fun getPlan(planId: String): Flow<Response<Plan?>> =
        plansRef.document(planId).snapshots()
            .map<DocumentSnapshot, Response<Plan?>> { Response.Success(it.toObject()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override fun getParticipants(planId: String): Flow<Response<List<UserPreview>>> =
        plansRef.document(planId).collection(PARTICIPANTS).snapshots()
            .map<QuerySnapshot, Response<List<UserPreview>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override fun getPrivatePlans(uid: String): Flow<Response<List<Plan>>> =
        plansRef.whereEqualTo(TYPE, PlanType.PRIVATE).whereArrayContains(FRIENDS_OF_HOST, uid)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<Plan>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override fun getMyPlans(uid: String): Flow<Response<List<Plan>>> =
        plansRef.where(
            Filter.or(
                Filter.equalTo("$HOST.$UID", uid),
                Filter.arrayContains(PARTICIPANTS, uid)
            )
        ).orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<Plan>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override fun getChat(planId: String): Flow<Response<List<Message>>> =
        plansRef.document(planId).collection(CHAT)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<Message>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }


    override suspend fun createPlan(plan: Plan): Response<Boolean> {
        return try {
            val docRef = db.collection(PLANS).document()
            val planWithId = plan.copy(id = docRef.id)
            val host = plan.host
            db.runBatch { batch ->
                batch.set(docRef, planWithId)
                batch.set(docRef.collection(PARTICIPANTS).document(host.uid), host.toMap())
            }.await()
            docRef.set(planWithId).await()
            Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }
    }

    override suspend fun deletePlan(planId: String): Response<Unit> {
        return try {
            coroutineScope {

                val planRef = plansRef.document(planId)
                val participantsRef = planRef.collection(PARTICIPANTS)
                val chatRef = planRef.collection(CHAT)

                val participantsDef = async { participantsRef.get().await() }
                val chatDef = async { chatRef.get().await() }

                val (participants, chat) = awaitAll(participantsDef, chatDef)

                db.runTransaction { trans ->
                    participants.forEach { trans.delete(it.reference) }
                    chat.forEach { trans.delete(it.reference) }
                    trans.delete(planRef)
                }.await()
            }
            Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }
    }

    override suspend fun deleteParticipant(planId: String, uid: String): Response<Unit> {
        return try {
            val planRef = plansRef.document(planId)
            db.runBatch { batch ->
                batch.delete(planRef.collection(PARTICIPANTS).document(uid))
                batch.update(planRef, PARTICIPANTS, FieldValue.arrayRemove(uid))
            }.await()
            Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }
    }

    override suspend fun addParticipant(planId: String, user: UserPreview): Response<Unit> {
        return try {
            val planRef = plansRef.document(planId)
            db.runBatch { batch ->
                batch.set(planRef.collection(PARTICIPANTS).document(user.uid), user)
                batch.update(planRef, PARTICIPANTS, FieldValue.arrayUnion(user.uid))
            }.await()
            Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }
    }

    override suspend fun sendMessage(planId: String, message: Message): Response<Unit> {
        return try {
            val docRef = plansRef.document(planId).collection(CHAT).document()
            docRef.set(message.copy(id = docRef.id)).await()
            Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }
    }
}