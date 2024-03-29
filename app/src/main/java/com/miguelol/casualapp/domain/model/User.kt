package com.miguelol.casualapp.domain.model

data class User(
    var uid: String = "",
    var username: String = "",
    var name: String = "",
    var age: String = "",
    var description: String = "",
    var image: String = "",
    var friendCount: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "username" to username,
            "name" to name,
            "age" to age,
            "description" to description,
            "image" to image,
            "friendCount" to friendCount
        )
    }

    fun toPreview(): UserPreview {
        return UserPreview(
            uid = uid,
            name = name,
            username = username,
            image = image
        )
    }
}

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
