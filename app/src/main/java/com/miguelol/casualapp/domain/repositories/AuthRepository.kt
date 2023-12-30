package com.miguelol.casualapp.domain.repositories

import com.google.firebase.auth.FirebaseUser
import com.miguelol.casualapp.domain.model.Response

interface AuthRepository {

    val currentUser: FirebaseUser?
    suspend fun login(email:String, password:String): Response<FirebaseUser?>

}