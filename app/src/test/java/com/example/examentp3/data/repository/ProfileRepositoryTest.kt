package com.example.examentp3.data.repository

import com.example.examentp3.model.DemoUserProfile
import com.example.examentp3.viewmodel.toProfileDashboardUiState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Only the path without Firebase (SPEC p.3): FirebaseFirestore cannot be created in a local test.
 * The Firestore path is checked by hand (README, "Perfil en Firestore").
 */
class ProfileRepositoryTest {

    private val repository = ProfileRepository(AuthRepository(auth = null), firestore = null)

    @Test
    fun withoutFirebase_startsWithTheDemoProfile() {
        assertEquals(DemoUserProfile, repository.profile.value)
        assertFalse(repository.isRemoteEnabled)
    }

    @Test
    fun withoutFirebase_saveKeepsTheProfileInMemory() {
        repository.savePersonalData { it.copy(firstName = "Sam") }

        assertEquals("Sam", repository.profile.value.firstName)
    }

    @Test
    fun withoutFirebase_syncWithSessionKeepsLocalChanges() {
        repository.savePersonalData { it.copy(firstName = "Sam") }

        repository.syncWithSession()

        assertEquals("Sam", repository.profile.value.firstName)
    }

    @Test
    fun withoutFirebase_refreshSucceedsAndKeepsLocalChanges() = runBlocking {
        repository.savePersonalData { it.copy(lastName = "Lee") }

        assertTrue(repository.refresh().isSuccess)
        assertEquals("Lee", repository.profile.value.lastName)
    }

    @Test
    fun dashboardName_joinsFirstAndLastName() {
        assertEquals("Alex Morgan", DemoUserProfile.toProfileDashboardUiState().fullName)
    }

    @Test
    fun dashboardName_withoutLastName_hasNoTrailingSpace() {
        assertEquals("Sam", DemoUserProfile.copy(firstName = "Sam", lastName = " ").toProfileDashboardUiState().fullName)
    }
}
