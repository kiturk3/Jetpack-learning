package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.User
import com.kiturk3.recipevault.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(tokenId : String): Resource<User> = repository.signInWithGoogle(tokenId)
}