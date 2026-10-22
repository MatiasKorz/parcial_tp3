package com.example.examentp3.data.repository

import com.example.examentp3.model.DemoUserProfile
import com.example.examentp3.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory profile of the demo user. Being a singleton, what is saved lasts for the whole
 * app session (NAV04) and is lost when the process ends, which the card allows (SPEC p.12).
 */
@Singleton
class ProfileRepository @Inject constructor() {

    private val _profile = MutableStateFlow(DemoUserProfile)
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    /** Applies [transform] atomically, so each screen only changes the fields it owns. */
    fun updateProfile(transform: (UserProfile) -> UserProfile) {
        _profile.update(transform)
    }
}
