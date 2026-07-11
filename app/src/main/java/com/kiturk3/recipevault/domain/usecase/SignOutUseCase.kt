package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.signOut()
}