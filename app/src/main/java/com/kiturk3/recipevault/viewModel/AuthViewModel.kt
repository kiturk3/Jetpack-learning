package com.kiturk3.recipevault.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.User
import com.kiturk3.recipevault.domain.usecase.GetCurrentUserUseCase
import com.kiturk3.recipevault.domain.usecase.SignInUseCase
import com.kiturk3.recipevault.domain.usecase.SignInWithGoogleUseCase
import com.kiturk3.recipevault.domain.usecase.SignOutUseCase
import com.kiturk3.recipevault.domain.usecase.SignUpUseCase
import com.kiturk3.recipevault.uiStates.AuthNavigationEvent
import com.kiturk3.recipevault.uiStates.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
): ViewModel(){

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvent: SharedFlow<AuthNavigationEvent> = _navigationEvent.asSharedFlow()

    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked: StateFlow<Boolean> = _isAuthChecked.asStateFlow()


    init {
        observeAuthState()
    }

    fun signIn(email: String, password: String){
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value =
                when(val result = signInUseCase(email.trim(), password.trim()))
            {
                is Resource.Success -> AuthUiState.Success(result.data)
                is Resource.Error -> AuthUiState.Error(result.message)
                is Resource.Loading -> AuthUiState.Loading
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = signUpUseCase(email.trim(), password.trim())){
                is Resource.Success -> AuthUiState.Success(result.data)
                is Resource.Error -> AuthUiState.Error(result.message)
                is Resource.Loading -> AuthUiState.Loading
            }
        }
    }

    fun signInWithGoogle(tokenId: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = signInWithGoogleUseCase(tokenId)) {
                is Resource.Success -> AuthUiState.Success(result.data)
                is Resource.Error -> AuthUiState.Error(result.message)
                is Resource.Loading -> AuthUiState.Loading
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _currentUser.value = null
            resetState()
            _navigationEvent.emit(AuthNavigationEvent.NavigateToLogin)
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _currentUser.value = user
                _isAuthChecked.value = true
            }
        }
    }

    fun resetState(){
        _uiState.value = AuthUiState.Idle
    }
}