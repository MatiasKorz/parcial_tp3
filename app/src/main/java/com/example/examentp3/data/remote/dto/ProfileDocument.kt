package com.example.examentp3.data.remote.dto

import com.example.examentp3.model.UserProfile

/**
 * Fields of the Firestore document users/{uid} (EXT-02): the personal data of Edit Profile, as text,
 * like the form. Goals and preferences (7.3 to 7.5) are out of scope and stay local.
 */
internal object ProfileDocumentFields {
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val DATE_OF_BIRTH = "dateOfBirth"
    const val HEIGHT = "height"
    const val WEIGHT = "weight"
    const val GENDER = "gender"
}

fun UserProfile.toProfileDocument(): Map<String, String> = mapOf(
    ProfileDocumentFields.FIRST_NAME to firstName,
    ProfileDocumentFields.LAST_NAME to lastName,
    ProfileDocumentFields.DATE_OF_BIRTH to dateOfBirth,
    ProfileDocumentFields.HEIGHT to height,
    ProfileDocumentFields.WEIGHT to weight,
    ProfileDocumentFields.GENDER to gender,
)

/**
 * Applies a stored document on top of this profile. Firestore has no schema, so a missing field or
 * one that is not text keeps the current value instead of crashing or blanking the form.
 */
fun UserProfile.withProfileDocument(document: Map<String, Any?>): UserProfile {
    fun text(field: String, current: String) = document[field] as? String ?: current
    return copy(
        firstName = text(ProfileDocumentFields.FIRST_NAME, firstName),
        lastName = text(ProfileDocumentFields.LAST_NAME, lastName),
        dateOfBirth = text(ProfileDocumentFields.DATE_OF_BIRTH, dateOfBirth),
        height = text(ProfileDocumentFields.HEIGHT, height),
        weight = text(ProfileDocumentFields.WEIGHT, weight),
        gender = text(ProfileDocumentFields.GENDER, gender),
    )
}
