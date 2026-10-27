package com.example.examentp3.viewmodel

import androidx.annotation.StringRes
import com.example.examentp3.R
import com.example.examentp3.model.AuthFailure

/** Message shown on Sign In and Sign Up: a form check or a Firebase Authentication failure (EXT-01). */
sealed interface AuthError {
    @get:StringRes val message: Int

    data object NameRequired : AuthError { override val message = R.string.auth_error_name_required }
    data object EmailRequired : AuthError { override val message = R.string.auth_error_email_required }
    data object InvalidEmail : AuthError { override val message = R.string.auth_error_invalid_email }
    data object PasswordRequired : AuthError { override val message = R.string.auth_error_password_required }
    data object PasswordTooShort : AuthError { override val message = R.string.auth_error_password_too_short }
    data object InvalidCredentials : AuthError { override val message = R.string.auth_error_invalid_credentials }
    data object EmailInUse : AuthError { override val message = R.string.auth_error_email_in_use }
    data object WeakPassword : AuthError { override val message = R.string.auth_error_weak_password }
    data object Network : AuthError { override val message = R.string.auth_error_network }
    data object TooManyRequests : AuthError { override val message = R.string.auth_error_too_many_requests }
    data object Unknown : AuthError { override val message = R.string.error_unknown }
}

/** "8+ secure characters" in the Sign Up frame; Firebase itself only asks for 6. */
internal const val MIN_PASSWORD_LENGTH = 8

// Regex, not android.util.Patterns, so the check runs in local unit tests.
private val EmailRegex = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

private fun validateEmail(email: String): AuthError? = when {
    email.isBlank() -> AuthError.EmailRequired
    !EmailRegex.matches(email.trim()) -> AuthError.InvalidEmail
    else -> null
}

/** First problem of the Sign In form, or null if it can be sent. */
internal fun validateSignIn(email: String, password: String): AuthError? =
    validateEmail(email) ?: if (password.isEmpty()) AuthError.PasswordRequired else null

/** First problem of the Sign Up form, or null if it can be sent. */
internal fun validateSignUp(name: String, email: String, password: String): AuthError? = when {
    name.isBlank() -> AuthError.NameRequired
    else -> validateEmail(email) ?: when {
        password.isEmpty() -> AuthError.PasswordRequired
        password.length < MIN_PASSWORD_LENGTH -> AuthError.PasswordTooShort
        else -> null
    }
}

internal fun Throwable.toAuthError(): AuthError = when (this) {
    AuthFailure.InvalidCredentials -> AuthError.InvalidCredentials
    AuthFailure.InvalidEmail -> AuthError.InvalidEmail
    AuthFailure.EmailInUse -> AuthError.EmailInUse
    AuthFailure.WeakPassword -> AuthError.WeakPassword
    AuthFailure.Network -> AuthError.Network
    AuthFailure.TooManyRequests -> AuthError.TooManyRequests
    else -> AuthError.Unknown
}
