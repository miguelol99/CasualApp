package com.miguelol.casualapp.domain.usecases.auth

import com.miguelol.casualapp.domain.repositories.AuthRepository
import javax.inject.Inject

class SignOut @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Unit = authRepository.signOut
}