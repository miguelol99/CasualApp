package com.miguelol.casualapp.data.repositories

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.utils.Constants
import com.miguelol.casualapp.utils.Constants.FRIEND_REQUESTS
import com.miguelol.casualapp.utils.Constants.TIMESTAMP
import com.miguelol.casualapp.utils.Constants.USERS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendRequestRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore
) : FriendRequestRepository {

    private val usersRef = database.collection(USERS)

    override fun getFriendRequests(uid: String): Flow<Response<List<FriendRequest>>> {
        return usersRef.document(uid).collection(FRIEND_REQUESTS)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .snapshots()
            .map<QuerySnapshot, Response<List<FriendRequest>>> { Success(it.toObjects()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }
    }

    override fun getFriendRequest(fromUid: String, toUid: String): Flow<Response<FriendRequest?>> =
        usersRef.document(toUid).collection(FRIEND_REQUESTS).document(fromUid)
            .snapshots()
            .map<DocumentSnapshot, Response<FriendRequest?>> { Success(it.toObject()) }
            .catch { it.printStackTrace(); emit(Error(Exception(it))) }

    override suspend fun createFriendRequest(
        toUid: String,
        request: FriendRequest
    ): Response<Unit> =
        try {
            usersRef.document(toUid)
                .collection(Constants.FRIEND_REQUESTS)
                .document(request.fromUser.uid)
                .set(request)
                .await()
            Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }

    override suspend fun deleteFriendRequest(fromUid: String, toUid: String): Response<Unit> =
        try {
            val requestRef1 = usersRef.document(toUid).collection(FRIEND_REQUESTS).document(fromUid)
            val requestRef2 = usersRef.document(fromUid).collection(FRIEND_REQUESTS).document(toUid)

            database.runBatch { batch ->
                batch.delete(requestRef1)
                batch.delete(requestRef2)
            }.await()
            Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Error(e)
        }
}