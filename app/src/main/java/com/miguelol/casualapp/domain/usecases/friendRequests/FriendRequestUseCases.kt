package com.miguelol.casualapp.domain.usecases.friendRequests

data class FriendRequestUseCases(
    val getFriendRequests: GetFriendRequests,
    val createRequest: CreateRequest,
    val declineRequest: DeclineRequest,
    val acceptRequest: AcceptRequest,
    val getFriendState: GetFriendState
)






