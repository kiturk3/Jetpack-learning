package com.kiturk3.recipevault.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.User
import com.kiturk3.recipevault.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomainUser())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Resource<User> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user?.toDomainUser()
            ?: return Resource.Error("Sign in failed — no user returned")
        Resource.Success(user)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Sign in failed")
    }


    override suspend fun signUpwithEmail(
        email: String,
        password: String
    ): Resource<User> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user?.toDomainUser()
            ?: return Resource.Error("Sign up failed — no user returned")
        Resource.Success(user)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Sign up failed")
    }

    override suspend fun signInWithGoogle(idToken: String): Resource<User> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = result.user?.toDomainUser()
            ?: return Resource.Error("Google sign in failed")
        Resource.Success(user)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Google sign in failed")
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    private fun com.google.firebase.auth.FirebaseUser.toDomainUser() = User(
        uid = uid,
        email = email,
        displayName = displayName
    )
}