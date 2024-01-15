package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Plan(
    var id: String = "",
    var type: PlanType = PlanType.PUBLIC,
    var host: UserPreview = UserPreview(),
    var title: String = "",
    var datetime: Timestamp? = null,
    var location: String = "",
    var image: String = "",
    var description: String = "",
    var participants: List<String> = emptyList(),
    var friendsOfHost: List<String> = emptyList(),
    @ServerTimestamp var timestamp: Timestamp? = null,
)

enum class PlanType {
    PUBLIC, PRIVATE, SECRET
}
