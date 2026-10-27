package com.example.examentp3.data.remote.dto

import com.example.examentp3.model.DemoUserProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileDocumentTest {

    private val saved = DemoUserProfile.copy(
        firstName = "Sam",
        lastName = "Lee",
        dateOfBirth = "2 June 1990",
        height = "165 cm",
        weight = "60 kg",
        gender = "Female",
    )

    @Test
    fun document_roundTripKeepsThePersonalData() {
        assertEquals(saved, DemoUserProfile.withProfileDocument(saved.toProfileDocument()))
    }

    @Test
    fun document_hasOnlyTheEditProfileFields() {
        assertEquals(
            setOf("firstName", "lastName", "dateOfBirth", "height", "weight", "gender"),
            saved.toProfileDocument().keys,
        )
    }

    @Test
    fun documentWithMissingField_keepsTheCurrentValue() {
        val profile = DemoUserProfile.withProfileDocument(mapOf("firstName" to "Sam"))

        assertEquals("Sam", profile.firstName)
        assertEquals(DemoUserProfile.lastName, profile.lastName)
    }

    @Test
    fun documentWithFieldThatIsNotText_keepsTheCurrentValue() {
        val profile = DemoUserProfile.withProfileDocument(mapOf("height" to 165L, "weight" to null))

        assertEquals(DemoUserProfile.height, profile.height)
        assertEquals(DemoUserProfile.weight, profile.weight)
    }

    @Test
    fun document_doesNotTouchGoalsAndPreferences() {
        val profile = DemoUserProfile.withProfileDocument(saved.toProfileDocument())

        assertEquals(DemoUserProfile.goals, profile.goals)
        assertEquals(DemoUserProfile.nutrition, profile.nutrition)
    }
}
