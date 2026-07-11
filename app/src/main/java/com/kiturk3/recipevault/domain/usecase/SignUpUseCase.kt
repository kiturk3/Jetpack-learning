package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.repository.AuthRepository
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.signUpwithEmail(email, password)
}