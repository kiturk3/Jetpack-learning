package com.kiturk3.recipevault.viewModel

import androidx.compose.ui.Modifier.Companion.any
import app.cash.turbine.test
import com.google.common.base.CharMatcher.any
import com.google.common.truth.Truth.assertThat
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.User
import com.kiturk3.recipevault.domain.repository.AuthRepository
import com.kiturk3.recipevault.domain.usecase.GetCurrentUserUseCase
import com.kiturk3.recipevault.domain.usecase.SignInUseCase
import com.kiturk3.recipevault.domain.usecase.SignInWithGoogleUseCase
import com.kiturk3.recipevault.domain.usecase.SignOutUseCase
import com.kiturk3.recipevault.domain.usecase.SignUpUseCase
import com.kiturk3.recipevault.uiStates.AuthUiState
import com.kiturk3.recipevault.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AuthViewModel
    private val authRepository: AuthRepository = mockk()

    private val testUser = User(
        uid = "test-uid-123",
        email = "test@example.com",
        displayName = "Test User"
    )

    @Before
    fun setUp() {
        // Default — no logged in user
        every { authRepository.currentUser } returns flowOf(null)

        viewModel = AuthViewModel(
            signInUseCase = SignInUseCase(authRepository),
            signUpUseCase = SignUpUseCase(authRepository),
            signOutUseCase = SignOutUseCase(authRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
            signInWithGoogleUseCase = SignInWithGoogleUseCase(authRepository)
        )
    }

    @Test
    fun `initial uiState is Idle`() = runTest {
        assertThat(viewModel.uiState.value).isEqualTo(AuthUiState.Idle)
    }

    @Test
    fun `signIn success emits Success state`() = runTest {
        coEvery {
            authRepository.signInWithEmail("test@example.com", "password123")
        } returns Resource.Success(testUser)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(AuthUiState.Idle)

            viewModel.signIn("test@example.com", "password123")
            assertThat(awaitItem()).isEqualTo(AuthUiState.Loading)
            advanceUntilIdle()

            val success = awaitItem() as AuthUiState.Success
            assertThat(success.user.email).isEqualTo("test@example.com")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `signIn failure emits Error state`() = runTest {
        coEvery {
            authRepository.signInWithEmail(any(), any())
        } returns Resource.Error("Invalid credentials")

        viewModel.uiState.test {
            awaitItem() // Idle

            viewModel.signIn("wrong@example.com", "wrongpassword")
            awaitItem() // Loading
            advanceUntilIdle()

            val error = awaitItem() as AuthUiState.Error
            assertThat(error.message).isEqualTo("Invalid credentials")
            cancelAndIgnoreRemainingEvents()
        }
    }

//    @Test
//    fun `signUp success emits Success state`() = runTest {
//        coEvery {
//            authRepository.signUpWithEmail(any(), any())
//        } returns Resource.Success(testUser)
//
//        viewModel.uiState.test {
//            awaitItem() // Idle
//
//            viewModel.signUp("new@example.com", "newpassword")
//            awaitItem() // Loading
//            advanceUntilIdle()
//
//            val success = awaitItem() as AuthUiState.Success
//            assertThat(success.user.uid).isEqualTo("test-uid-123")
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

    @Test
    fun `signOut calls repository and clears user`() = runTest {
        coEvery { authRepository.signOut() } returns Unit

        viewModel.signOut()
        advanceUntilIdle()

        coVerify { authRepository.signOut() }
        assertThat(viewModel.currentUser.value).isNull()
    }

    @Test
    fun `resetState returns uiState to Idle`() = runTest {
        coEvery {
            authRepository.signInWithEmail(any(), any())
        } returns Resource.Success(testUser)

        viewModel.signIn("test@example.com", "password")
        advanceUntilIdle()

        viewModel.resetState()
        assertThat(viewModel.uiState.value).isEqualTo(AuthUiState.Idle)
    }

    @Test
    fun `currentUser updates when auth state changes`() = runTest {
        every { authRepository.currentUser } returns flowOf(testUser)

        val freshViewModel = AuthViewModel(
            signInUseCase = SignInUseCase(authRepository),
            signUpUseCase = SignUpUseCase(authRepository),
            signOutUseCase = SignOutUseCase(authRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
            signInWithGoogleUseCase = SignInWithGoogleUseCase(authRepository)
        )

        // Let observeAuthState coroutine run and collect the user
        advanceUntilIdle()

        freshViewModel.currentUser.test {
//            advanceUntilIdle()
            val user = awaitItem()
            assertThat(user?.email).isEqualTo("test@example.com")
            cancelAndIgnoreRemainingEvents()
        }
    }
}