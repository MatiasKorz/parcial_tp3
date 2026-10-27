package com.example.examentp3.data.repository

import android.util.Log
import com.example.examentp3.data.remote.awaitTask
import com.example.examentp3.data.remote.dto.toProfileDocument
import com.example.examentp3.data.remote.dto.withProfileDocument
import com.example.examentp3.model.DemoUserProfile
import com.example.examentp3.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

private const val TAG = "ProfileRepository"

/** Collection protected by firestore.rules (EXT-03): each account only reaches users/{its uid}. */
private const val USERS_COLLECTION = "users"

/**
 * Profile of the user. The screens always read the in-memory copy, so they open at once and stay
 * navigable offline (SPEC p.12). With a Firebase session, Edit Profile's data is also saved to and
 * loaded from Firestore (EXT-02); without one, the demo profile is used (SPEC p.3).
 */
@Singleton
class ProfileRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore?,
) {

    private val _profile = MutableStateFlow(DemoUserProfile)
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    // Account whose data is in [profile]; null while it holds the demo profile.
    private var loadedUserId: String? = null

    /** True when a signed-in account keeps its profile in Firestore. */
    val isRemoteEnabled: Boolean get() = firestore != null && authRepository.currentUserId != null

    /** Applies [transform] atomically, so each screen only changes the fields it owns. Local only. */
    fun updateProfile(transform: (UserProfile) -> UserProfile) {
        _profile.update(transform)
    }

    /** "Save changes" of Edit Profile: local right away, and in users/{uid} when signed in. */
    fun savePersonalData(transform: (UserProfile) -> UserProfile) {
        val updated = _profile.updateAndGet(transform)
        val db = firestore ?: return
        val userId = authRepository.currentUserId ?: return
        // Not awaited on purpose: Firestore keeps the write in its local cache and sends it when there
        // is connection, so saving never blocks the screen offline. Merge keeps fields of other screens.
        db.collection(USERS_COLLECTION).document(userId)
            .set(updated.toProfileDocument(), SetOptions.merge())
            .addOnFailureListener { e -> Log.w(TAG, "Profile not saved to Firestore: ${e.javaClass.simpleName}", e) }
    }

    /**
     * Drops the data of a previous account when the session changed (log out, or another account signed in),
     * so the next one starts from the demo profile and never sees it. Synchronous: call it before reading.
     */
    fun syncWithSession() {
        val userId = if (firestore != null) authRepository.currentUserId else null
        if (userId != loadedUserId) {
            _profile.value = DemoUserProfile
            loadedUserId = userId
        }
    }

    /**
     * Loads the profile of the signed-in account from Firestore. On failure (offline) or when the account
     * has not saved a profile yet, the in-memory copy stays as it is.
     */
    suspend fun refresh(): Result<Unit> {
        syncWithSession()
        val db = firestore ?: return Result.success(Unit)
        val userId = loadedUserId ?: return Result.success(Unit)
        return try {
            val snapshot = db.collection(USERS_COLLECTION).document(userId).get().awaitTask()
            snapshot.data?.let { document -> _profile.update { it.withProfileDocument(document) } }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Profile not loaded from Firestore: ${e.javaClass.simpleName}", e)
            Result.failure(e)
        }
    }
}
