package com.kiturk3.recipevault.data.remote.mapper

import com.google.firebase.auth.FirebaseUser
import com.kiturk3.recipevault.domain.model.User

fun FirebaseUser.toDomainUser(): User {
    return User(
        uid = uid,
        email = email,
        displayName = displayName
    )
}
