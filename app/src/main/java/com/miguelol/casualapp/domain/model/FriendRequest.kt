package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class FriendRequest(
    var fromUser: UserPreview = UserPreview(),
    @ServerTimestamp val timestamp: Timestamp? = null
)