package com.example.billapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billapp.firebase.UserRepository
import com.example.billapp.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Initial)
    val uiState: StateFlow<SignInUiState> = _uiState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading
            try {
                val user = repository.signIn(email, password)
                _uiState.value = SignInUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = SignInUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class SignInUiState {
    object Initial : SignInUiState()
    object Loading : SignInUiState()
    data class Success(val user: User) : SignInUiState()
    data class Error(val message: String) : SignInUiState()
}