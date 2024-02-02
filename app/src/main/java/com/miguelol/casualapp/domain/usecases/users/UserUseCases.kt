package com.miguelol.casualapp.domain.usecases.users

data class UserUseCases(
    val getUser: GetUser,
    val isUsernameTaken: IsUsernameTaken,
    val updateUser: UpdateUser,
    val searchUsers: SearchUsers
)





