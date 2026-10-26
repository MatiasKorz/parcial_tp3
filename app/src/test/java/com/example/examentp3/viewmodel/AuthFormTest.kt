package com.example.examentp3.viewmodel

import com.example.examentp3.data.repository.AuthRepository
import com.example.examentp3.model.AuthFailure
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthFormTest {

    // Without FirebaseAuth, as in a build without google-services.json.
    private val demoRepository = AuthRepository(auth = null)

    @Test
    fun signIn_validForm_hasNoError() {
        assertNull(validateSignIn("alex.morgan@email.com", "secret"))
    }

    @Test
    fun signIn_blankEmail_asksForEmail() {
        assertEquals(AuthError.EmailRequired, validateSignIn("  ", "secret"))
    }

    @Test
    fun signIn_emailWithoutDomain_isInvalid() {
        assertEquals(AuthError.InvalidEmail, validateSignIn("alex.morgan", "secret"))
    }

    @Test
    fun signIn_emailWithSurroundingSpaces_isAccepted() {
        assertNull(validateSignIn(" alex.morgan@email.com ", "secret"))
    }

    @Test
    fun signIn_emptyPassword_asksForPassword() {
        assertEquals(AuthError.PasswordRequired, validateSignIn("alex.morgan@email.com", ""))
    }

    @Test
    fun signUp_blankName_asksForName() {
        assertEquals(AuthError.NameRequired, validateSignUp(" ", "alex.morgan@email.com", "12345678"))
    }

    @Test
    fun signUp_passwordShorterThanEight_isTooShort() {
        assertEquals(AuthError.PasswordTooShort, validateSignUp("Alex Morgan", "alex.morgan@email.com", "1234567"))
    }

    @Test
    fun signUp_passwordOfEight_isAccepted() {
        assertNull(validateSignUp("Alex Morgan", "alex.morgan@email.com", "12345678"))
    }

    @Test
    fun authFailures_mapToTheirOwnMessage() {
        assertEquals(AuthError.InvalidCredentials, AuthFailure.InvalidCredentials.toAuthError())
        assertEquals(AuthError.EmailInUse, AuthFailure.EmailInUse.toAuthError())
        assertEquals(AuthError.WeakPassword, AuthFailure.WeakPassword.toAuthError())
        assertEquals(AuthError.Network, AuthFailure.Network.toAuthError())
        assertEquals(AuthError.TooManyRequests, AuthFailure.TooManyRequests.toAuthError())
    }

    @Test
    fun unexpectedFailure_mapsToUnknown() {
        assertEquals(AuthError.Unknown, IllegalStateException().toAuthError())
    }

    @Test
    fun signIn_withoutFirebase_keepsTheDemoAccess() {
        val viewModel = SignInViewModel(demoRepository)

        // Same as PAN-1.1: the prefilled example email and an empty password open Home.
        viewModel.onSignIn("alex.morgan@email.com", "")

        assertTrue(viewModel.uiState.value.isSignedIn)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun signIn_signedInHandled_clearsTheEvent() {
        val viewModel = SignInViewModel(demoRepository)
        viewModel.onSignIn("", "")

        viewModel.onSignedInHandled()

        assertFalse(viewModel.uiState.value.isSignedIn)
    }

    @Test
    fun signUp_withoutFirebase_showsTheDemoNoticeAndCreatesNothing() {
        val viewModel = SignUpViewModel(demoRepository)

        viewModel.onSignUp("Alex Morgan", "alex.morgan@email.com", "12345678")

        assertTrue(viewModel.uiState.value.showDemoNotice)
        assertFalse(viewModel.uiState.value.isSignedUp)
    }

    @Test
    fun signUp_demoNoticeDismiss_hidesIt() {
        val viewModel = SignUpViewModel(demoRepository)
        viewModel.onSignUp("", "", "")

        viewModel.onDemoNoticeDismiss()

        assertFalse(viewModel.uiState.value.showDemoNotice)
    }
}
