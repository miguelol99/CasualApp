package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp

data class Message(
    val timestamp: Timestamp,
    val uid: String,
    val name: String,
    val username: String,
    val message: String,
)