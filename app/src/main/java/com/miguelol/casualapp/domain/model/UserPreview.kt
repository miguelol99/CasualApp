package com.miguelol.casualapp.domain.model

data class UserPreview(
    var uid: String = "",
    var username: String = "",
    var name: String = "",
    var image: String = "",
) {
    fun matchesTerm(term: String): Boolean {
        return name.contains(term, true) || username.contains(term, true)
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "username" to username,
        "image" to image
    )
}
