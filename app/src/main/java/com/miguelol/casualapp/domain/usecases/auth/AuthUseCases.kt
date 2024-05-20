package com.miguelol.casualapp.domain.usecases.auth

data class AuthUseCases(
    val getCurrentUser: GetCurrentUser,
    val logIn: LogIn,
    val signUp: SignUp,
    val signOut: SignOut
)


