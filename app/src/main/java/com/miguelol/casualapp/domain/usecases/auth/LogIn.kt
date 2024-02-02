package com.miguelol.casualapp.domain.usecases.auth

import com.google.firebase.auth.FirebaseUser
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.repositories.AuthRepository
import javax.inject.Inject

class LogIn @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email:String, password: String): Response<FirebaseUser?> =
        authRepository.login(email, password)
}