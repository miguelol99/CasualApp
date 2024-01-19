package com.miguelol.casualapp.data.repositories


import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import com.miguelol.casualapp.utils.Constants.CHAT
import com.miguelol.casualapp.utils.Constants.FRIENDS
import com.miguelol.casualapp.utils.Constants.FRIEND_REQUESTS
import com.miguelol.casualapp.utils.Constants.FROM_USER
import com.miguelol.casualapp.utils.Constants.HOST
import com.miguelol.casualapp.utils.Constants.PARTICIPANTS
import com.miguelol.casualapp.utils.Constants.PLANS
import com.miguelol.casualapp.utils.Constants.PLAN_REQUESTS
import com.miguelol.casualapp.utils.Constants.USERNAME
import com.miguelol.casualapp.utils.Constants.USERS
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : UserRepository {

    private val usersRef = database.collection(USERS)
    private val friendsRef = database.collectionGroup(FRIENDS)
    private val friendRequestsRef = database.collectionGroup(FRIEND_REQUESTS)
    private val planRequestsRef = database.collectionGroup(PLAN_REQUESTS)
    private val plansRef = database.collection(PLANS)
    private val participantsRef = database.collectionGroup(PARTICIPANTS)
    private val chatRef = database.collectionGroup(CHAT)

    override fun getUser(uid: String): Flow<Response<User?>> =
        usersRef.document(uid).snapshots()
            .map<DocumentSnapshot, Response<User?>> { Response.Success(it.toObject<User>()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override fun getUserByUsername(username: String): Flow<Response<User?>> =
            usersRef.whereEqualTo(USERNAME, username).snapshots()
            .map<QuerySnapshot, Response<User?>> {
                Response.Success(it.toObjects<User>().firstOrNull())
            }.catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override fun getUsersThatMatch(searchTerm: String): Flow<Response<List<User>>> =
        usersRef.whereGreaterThanOrEqualTo(USERNAME, searchTerm)
            .whereLessThanOrEqualTo(USERNAME, "$searchTerm~") //adds ~ because is a high point code
            .snapshots()
            .map<QuerySnapshot, Response<List<User>>> { Response.Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override suspend fun updateUser(user: User): Response<Unit> {
        val uid = user.uid
        val preview = user.toPreview()
        return try {

            coroutineScope {
                val friendsDef = async { friendsRef.whereEqualTo(UID, uid).get().await() }
                val friendRequestsDef = async { friendRequestsRef.whereEqualTo("$FROM_USER.$UID", uid).get().await() }
                val planRequestsDef = async { planRequestsRef.whereEqualTo("$FROM_USER.$UID", uid).get().await() }

                val plansDef = async { plansRef.whereEqualTo("$HOST.$UID", uid).get().await() }
                val participantsDef = async { participantsRef.whereEqualTo(UID, uid).get().await() }
                val chatDef = async { chatRef.whereEqualTo(UID, uid).get().await() }

                val friends = friendsDef.await()
                val friendRequests = friendRequestsDef.await()
                val planRequests = planRequestsDef.await()

                val plans = plansDef.await()
                val participants = participantsDef.await()
                val chat = chatDef.await()

                val userRef = usersRef.document(uid)

                database.runTransaction { trans ->
                    friends.forEach { trans.update(it.reference, preview.toMap()) }
                    friendRequests.forEach { trans.update(it.reference, FROM_USER, preview.toMap()) }
                    planRequests.forEach { trans.update(it.reference, FROM_USER, preview.toMap()) }

                    plans.forEach { trans.update(it.reference, HOST,  preview.toMap()) }
                    participants.forEach { trans.update(it.reference, preview.toMap()) }
                    chat.forEach { trans.update(it.reference, FROM_USER, preview.toMap()) }

                    trans.update(userRef, user.toMap())
                }.await()
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }
}