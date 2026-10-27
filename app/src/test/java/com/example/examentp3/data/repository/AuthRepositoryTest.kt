package com.example.examentp3.data.repository

import com.example.examentp3.model.AuthFailure
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Only the path without Firebase: Firebase exceptions call android.text.TextUtils in their constructor,
 * so toAuthFailure() cannot be fed real ones in a local test. It is checked by hand (README, "Firebase").
 */
class AuthRepositoryTest {

    private val repository = AuthRepository(auth = null)

    @Test
    fun withoutFirebase_isNotEnabled() {
        assertFalse(repository.isFirebaseEnabled)
    }

    @Test
    fun withoutFirebase_signInFailsAsNotConfigured() = runBlocking {
        assertEquals(AuthFailure.NotConfigured, repository.signIn("alex.morgan@email.com", "secret").exceptionOrNull())
    }

    @Test
    fun withoutFirebase_signUpFailsAsNotConfigured() = runBlocking {
        assertEquals(
            AuthFailure.NotConfigured,
            repository.signUp("Alex Morgan", "alex.morgan@email.com", "12345678").exceptionOrNull(),
        )
    }

    @Test
    fun withoutFirebase_signOutDoesNothing() {
        repository.signOut()
    }

    @Test
    fun authFailure_passesThroughUnchanged() {
        assertEquals(AuthFailure.EmailInUse, AuthFailure.EmailInUse.toAuthFailure())
    }

    @Test
    fun unexpectedException_mapsToUnknown() {
        assertEquals(AuthFailure.Unknown, IllegalStateException().toAuthFailure())
    }
}
