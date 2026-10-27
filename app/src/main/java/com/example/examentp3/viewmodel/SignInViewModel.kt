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

data class SignInUiState(
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    /** One-shot event: the NavHost opens Home and calls onSignedInHandled(). */
    val isSignedIn: Boolean = false,
)

/** "Sign In" of Login - 01 with Firebase Authentication, or the demo access without it (EXT-01). */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onSignIn(email: String, password: String) {
        val state = _uiState.value
        // A second tap while signing in does not send a second request.
        if (state.isLoading || state.isSignedIn) return

        // Without google-services.json the demo access of PAN-1.1 stays as it was (SPEC p.1).
        if (!repository.isFirebaseEnabled) {
            _uiState.update { it.copy(isSignedIn = true) }
            return
        }

        validateSignIn(email, password)?.let { error ->
            _uiState.update { it.copy(error = error) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.signIn(email.trim(), password)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSignedIn = true) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, error = error.toAuthError()) } }
        }
    }

    /** Editing a field hides the previous message. */
    fun onInputChange() {
        if (_uiState.value.error != null) _uiState.update { it.copy(error = null) }
    }

    fun onSignedInHandled() = _uiState.update { it.copy(isSignedIn = false) }
}
