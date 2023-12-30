package com.miguelol.casualapp.data.repositories

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.toObject
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.repositories.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
    ) : AuthRepository {

    override val currentUser: FirebaseUser? get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Response<FirebaseUser?> {
        return try {
            val result: AuthResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Response.Success(result.user)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e)
        }
    }
}