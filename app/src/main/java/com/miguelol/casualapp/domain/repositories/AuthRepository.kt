package com.miguelol.casualapp.domain.repositories

import com.google.firebase.auth.FirebaseUser
import com.miguelol.casualapp.domain.model.Response

interface AuthRepository {

    val currentUser: FirebaseUser?
    val signOut: Unit
    suspend fun login(email:String, password:String): Response<FirebaseUser?>

}