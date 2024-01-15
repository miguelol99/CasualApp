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
import com.miguelol.casualapp.utils.Constants.FRIENDS
import com.miguelol.casualapp.utils.Constants.FRIEND_REQUESTS
import com.miguelol.casualapp.utils.Constants.FROM_USER
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
    private val friendsColRef = database.collectionGroup(FRIENDS)
    private val requestsColRef = database.collectionGroup(FRIEND_REQUESTS)

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
                val friendsDef = async { friendsColRef.whereEqualTo(UID, uid).get().await() }
                val requestsDef = async { requestsColRef.whereEqualTo("$FROM_USER.$UID", uid).get().await() }

                val friends = friendsDef.await()
                val requests = requestsDef.await()
                val userRef = usersRef.document(uid)

                database.runBatch { batch ->
                    friends.forEach { batch.update(it.reference, preview.toMap()) }
                    requests.forEach { batch.update(it.reference, FROM_USER, preview.toMap()) }
                    batch.update(userRef, user.toMap())
                }.await()
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }
}