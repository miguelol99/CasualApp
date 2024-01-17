package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class PlanRequest(
    val id: String = "",
    val fromUser: UserPreview = UserPreview(),
    val plan: PlanPreview = PlanPreview(),
    @ServerTimestamp val timestamp: Timestamp? = null
)
