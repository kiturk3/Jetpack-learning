package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.signInWithEmail(email,password)
}