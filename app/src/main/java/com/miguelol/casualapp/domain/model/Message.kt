package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp

data class Message(
    var id: String = "",
    var fromUser: UserPreview = UserPreview(),
    val timestamp: Timestamp? = null,
    val message: String = "",
)