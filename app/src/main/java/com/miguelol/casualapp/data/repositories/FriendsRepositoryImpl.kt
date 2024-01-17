package com.miguelol.casualapp.data.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.repositories.FriendsRepository
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import com.miguelol.casualapp.utils.Constants.FRIENDS
import com.miguelol.casualapp.utils.Constants.FRIENDS_OF_HOST
import com.miguelol.casualapp.utils.Constants.FRIEND_COUNT
import com.miguelol.casualapp.utils.Constants.HOST
import com.miguelol.casualapp.utils.Constants.PLANS
import com.miguelol.casualapp.utils.Constants.USERS
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : FriendsRepository {

    private val usersRef = database.collection(USERS)
    private val plansRef = database.collection(PLANS)

    override fun getFriends(uid: String): Flow<Response<List<UserPreview>>> =
        usersRef.document(uid).collection(FRIENDS).snapshots()
            .map<QuerySnapshot, Response<List<UserPreview>>> { Response.Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }

    override fun getFriend(uid1: String, uid2: String): Flow<Response<UserPreview?>> {
        return usersRef.document(uid1).collection(FRIENDS).document(uid2)
            .snapshots()
            .map<DocumentSnapshot, Response<UserPreview?>> { Response.Success(it.toObject()) }
            .catch { it.printStackTrace(); emit(Response.Error(Exception(it))) }
    }

    override suspend fun addFriend(user1: UserPreview, user2: UserPreview): Response<Unit> {

        return try {
            coroutineScope {
                val userRef1 = usersRef.document(user1.uid)
                val userRef2 = usersRef.document(user2.uid)

                val friendRef1 = userRef1.collection(FRIENDS).document(user2.uid)
                val friendRef2 = userRef2.collection(FRIENDS).document(user1.uid)

                val plans1Def = async { plansRef.whereEqualTo("$HOST.$UID", user1.uid).get().await() }
                val plans2Def = async { plansRef.whereEqualTo("$HOST.$UID", user2.uid).get().await() }
                val plans1 = plans1Def.await()
                val plans2 = plans2Def.await()

                database.runBatch { batch ->
                    batch.update(userRef1, FRIEND_COUNT, FieldValue.increment(1))
                    batch.update(userRef2, FRIEND_COUNT, FieldValue.increment(1))
                    batch.set(friendRef1, user2)
                    batch.set(friendRef2, user1)
                    plans1.forEach { batch.update(it.reference, FRIENDS_OF_HOST, FieldValue.arrayUnion(user2.uid)) }
                    plans2.forEach { batch.update(it.reference, FRIENDS_OF_HOST, FieldValue.arrayUnion(user1.uid)) }
                }.await()
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }

    override suspend fun deleteFriend(uid1: String, uid2: String): Response<Unit> {

        return try {
            coroutineScope {
                val userRef1 = usersRef.document(uid1)
                val userRef2 = usersRef.document(uid2)

                usersRef.whereArrayContainsAny(uid1, listOf(""))

                val friendRef1 = userRef1.collection(FRIENDS).document(uid2)
                val friendRef2 = userRef2.collection(FRIENDS).document(uid1)

                val plans1Def = async { plansRef.whereEqualTo("$HOST.$UID", uid1).get().await() }
                val plans2Def = async { plansRef.whereEqualTo("$HOST.$UID", uid2).get().await() }
                val plans1 = plans1Def.await()
                val plans2 = plans2Def.await()

                database.runBatch { batch ->
                    batch.update(userRef1, FRIEND_COUNT, FieldValue.increment(-1))
                    batch.update(userRef2, FRIEND_COUNT, FieldValue.increment(-1))
                    batch.delete(friendRef1)
                    batch.delete(friendRef2)
                    plans1.forEach { batch.update(it.reference, FRIENDS_OF_HOST, FieldValue.arrayRemove(uid2)) }
                    plans2.forEach { batch.update(it.reference, FRIENDS_OF_HOST, FieldValue.arrayRemove(uid1)) }
                }.await()
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }
}