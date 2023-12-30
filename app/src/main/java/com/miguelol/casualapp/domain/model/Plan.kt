package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp

data class Plan(
    val planId: String = "",
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val datetime: Timestamp? = null,
    val location: String = "",
    val host: UserPreview = UserPreview(),
    val participantCount: Int = 0,
    val image: String = ""
)

