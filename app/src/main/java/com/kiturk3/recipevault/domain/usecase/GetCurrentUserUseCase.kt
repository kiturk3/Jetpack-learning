package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.model.User
import com.kiturk3.recipevault.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Flow<User?> = repository.currentUser
}