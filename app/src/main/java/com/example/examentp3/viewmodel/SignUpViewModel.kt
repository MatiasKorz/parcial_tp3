package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    /** Without Firebase "Sign Up" keeps the PAN-1.2 notice: registration is simulated. */
    val showDemoNotice: Boolean = false,
    /** One-shot event: the account was created and is signed in. The NavHost opens Home. */
    val isSignedUp: Boolean = false,
)

/** "Sign Up" of Login - 02 with Firebase Authentication (EXT-01). */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onSignUp(name: String, email: String, password: String) {
        val state = _uiState.value
        if (state.isLoading || state.isSignedUp) return

        if (!repository.isFirebaseEnabled) {
            _uiState.update { it.copy(showDemoNotice = true) }
            return
        }

        validateSignUp(name, email, password)?.let { error ->
            _uiState.update { it.copy(error = error) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.signUp(name.trim(), email.trim(), password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSignedUp = true) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, error = error.toAuthError()) } }
        }
    }

    fun onInputChange() {
        if (_uiState.value.error != null) _uiState.update { it.copy(error = null) }
    }

    fun onDemoNoticeDismiss() = _uiState.update { it.copy(showDemoNotice = false) }

    fun onSignedUpHandled() = _uiState.update { it.copy(isSignedUp = false) }
}
