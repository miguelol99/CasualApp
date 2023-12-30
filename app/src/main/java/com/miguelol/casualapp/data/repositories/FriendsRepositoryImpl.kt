package com.miguelol.casualapp.data.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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
import com.miguelol.casualapp.utils.Constants
import com.miguelol.casualapp.utils.Constants.FRIENDS
import com.miguelol.casualapp.utils.Constants.USERS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : FriendsRepository {

    private val  usersRef = database.collection(USERS)

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

        val userRef1 = usersRef.document(user1.uid)
        val userRef2 = usersRef.document(user2.uid)
        val friendRef1 = userRef1.collection(FRIENDS).document(user2.uid)
        val friendRef2 = userRef2.collection(FRIENDS).document(user1.uid)

        return try {
            database.runBatch { batch ->
                batch.set(friendRef1, user2)
                batch.set(friendRef2, user1)
                batch.update(userRef1, Constants.FRIEND_COUNT, FieldValue.increment(1))
                batch.update(userRef2, Constants.FRIEND_COUNT, FieldValue.increment(1))
            }.await()
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }

    override suspend fun deleteFriend(uid1: String, uid2: String): Response<Unit> {

        val userRef1 = usersRef.document(uid1)
        val userRef2 = usersRef.document(uid2)
        val ref1 = userRef1.collection(FRIENDS).document(uid2)
        val ref2 = userRef2.collection(FRIENDS).document(uid1)

        return try {
            database.runBatch { batch ->
                batch.delete(ref1)
                batch.delete(ref2)
                batch.update(userRef1, Constants.FRIEND_COUNT, FieldValue.increment(-1))
                batch.update(userRef2, Constants.FRIEND_COUNT, FieldValue.increment(-1))
            }.await()
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }
}