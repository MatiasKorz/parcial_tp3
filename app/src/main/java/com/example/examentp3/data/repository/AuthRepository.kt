package com.example.examentp3.data.repository

import android.util.Log
import com.example.examentp3.data.remote.awaitTask
import com.example.examentp3.model.AuthFailure
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "AuthRepository"

/** Only door to Firebase Authentication (EXT-01): neither the UI nor the ViewModels call FirebaseAuth. */
@Singleton
class AuthRepository @Inject constructor(private val auth: FirebaseAuth?) {

    /** False when the build has no google-services.json: the app keeps the demo access (SPEC p.1). */
    val isFirebaseEnabled: Boolean get() = auth != null

    /** Firebase uid of the signed-in account, or null without a session; the profile is keyed by it (EXT-02). */
    val currentUserId: String? get() = auth?.currentUser?.uid

    suspend fun signIn(email: String, password: String): Result<Unit> = runAuth { firebase ->
        firebase.signInWithEmailAndPassword(email, password).awaitTask()
    }

    /** Creates the account (Firebase signs it in right away) and saves "Full Name" as its display name. */
    suspend fun signUp(name: String, email: String, password: String): Result<Unit> = runAuth { firebase ->
        val user = firebase.createUserWithEmailAndPassword(email, password).awaitTask().user
        try {
            user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.awaitTask()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // The account already exists: failing here would make a retry end in "email in use".
            Log.w(TAG, "Display name not saved: ${e.javaClass.simpleName}", e)
        }
    }

    fun signOut() {
        auth?.signOut()
    }

    private suspend fun runAuth(block: suspend (FirebaseAuth) -> Unit): Result<Unit> {
        val firebase = auth ?: return Result.failure(AuthFailure.NotConfigured)
        return try {
            block(firebase)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth failed: ${e.javaClass.simpleName}", e)
            Result.failure(e.toAuthFailure())
        }
    }
}

/** The weak password exception extends the invalid credentials one, so it is matched first. */
internal fun Exception.toAuthFailure(): AuthFailure = when (this) {
    is AuthFailure -> this
    is FirebaseAuthWeakPasswordException -> AuthFailure.WeakPassword
    is FirebaseAuthUserCollisionException -> AuthFailure.EmailInUse
    is FirebaseAuthInvalidCredentialsException ->
        if (errorCode == "ERROR_INVALID_EMAIL") AuthFailure.InvalidEmail else AuthFailure.InvalidCredentials
    is FirebaseAuthInvalidUserException -> AuthFailure.InvalidCredentials
    is FirebaseNetworkException -> AuthFailure.Network
    is FirebaseTooManyRequestsException -> AuthFailure.TooManyRequests
    else -> AuthFailure.Unknown
}
