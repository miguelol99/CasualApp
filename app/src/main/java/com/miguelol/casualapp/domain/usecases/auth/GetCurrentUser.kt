package com.miguelol.casualapp.domain.usecases.auth

import com.google.firebase.auth.FirebaseUser
import com.miguelol.casualapp.domain.repositories.AuthRepository
import javax.inject.Inject

class GetCurrentUser @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): FirebaseUser? = authRepository.currentUser
}