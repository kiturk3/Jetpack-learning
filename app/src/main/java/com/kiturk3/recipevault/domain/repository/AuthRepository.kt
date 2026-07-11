package com.kiturk3.recipevault.domain.repository

import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): Resource<User>
    suspend fun signUpwithEmail(email: String, password: String): Resource<User>
    suspend fun signInWithGoogle(idToken: String): Resource<User>
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}