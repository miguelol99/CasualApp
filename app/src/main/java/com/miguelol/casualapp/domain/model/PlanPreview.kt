package com.miguelol.casualapp.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class PlanPreview(
    var id: String = "",
    var type: String = "",
    var title: String = "",
    var datetime: Timestamp? = null,
    var location: String = "",
    var host: String = "",
    var image: String = "",
    var participants: List<String> = emptyList(),
    @ServerTimestamp var timestamp: Timestamp? = null,
)
