package com.example.billapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billapp.firebase.UserRepository
import com.example.billapp.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Initial)
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            try {
                val user = repository.signUp(name, email, password)
                _uiState.value = SignUpUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = SignUpUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class SignUpUiState {
    object Initial : SignUpUiState()
    object Loading : SignUpUiState()
    data class Success(val user: User) : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}