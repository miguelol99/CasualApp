package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class FriendRequest(
    var fromUser: UserPreview = UserPreview(),
    val state: RequestState = RequestState.PENDING,
    @ServerTimestamp val timestamp: Timestamp? = null
)

enum class RequestState {
    PENDING, ACCEPTED, DECLINED;
}