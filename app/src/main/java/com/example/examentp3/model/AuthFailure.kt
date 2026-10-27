package com.example.examentp3.model

/**
 * Why a Firebase Authentication call failed, already translated by AuthRepository so the
 * ViewModels never depend on Firebase exception types (EXT-01).
 */
sealed class AuthFailure : Exception() {
    /** Wrong email or password, or the account does not exist. */
    data object InvalidCredentials : AuthFailure()
    data object InvalidEmail : AuthFailure()
    data object EmailInUse : AuthFailure()
    data object WeakPassword : AuthFailure()
    data object Network : AuthFailure()
    data object TooManyRequests : AuthFailure()
    /** The build has no google-services.json, so there is no FirebaseApp. */
    data object NotConfigured : AuthFailure()
    data object Unknown : AuthFailure()
}
