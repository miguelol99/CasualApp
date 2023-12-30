package com.miguelol.casualapp.domain.model

data class UserPreview(
    var uid: String = "",
    var name: String = "",
    var username: String = "",
    var image: String = "",
) {
    fun matchesQuery(text: String): Boolean {
        return name.contains(text, true) || username.contains(text, true)
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "username" to username,
        "image" to image
    )
}
