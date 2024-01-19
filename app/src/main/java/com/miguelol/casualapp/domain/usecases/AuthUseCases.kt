package com.miguelol.casualapp.domain.usecases.auth

import com.google.firebase.auth.FirebaseUser
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.AuthRepository
import javax.inject.Inject

data class AuthUseCases(
    val getCurrentUser: GetCurrentUser,
    val logIn: LogIn,
    val signOut: SignOut
)

class LogIn @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email:String, password: String): Response<FirebaseUser?> =
        authRepository.login(email, password)
}

class GetCurrentUser @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): FirebaseUser? = authRepository.currentUser
}

class SignOut @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Unit = authRepository.signOut
}
