package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.Meal
import com.example.examentp3.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MealDetailsUiState {
    data object Loading : MealDetailsUiState
    data class Content(val meal: Meal, val deleteFailed: Boolean = false) : MealDetailsUiState
    /** The id is invalid or the meal no longer exists. */
    data object NotFound : MealDetailsUiState
    /** Room confirmed the delete: the NavHost goes back. */
    data object Deleted : MealDetailsUiState
}

internal enum class MealDeleteStatus { Idle, InProgress, Failed, Done }

/** Pure builder of the detail state, unit-tested without Android. */
internal fun mealDetailsUiState(meal: Meal?, status: MealDeleteStatus): MealDetailsUiState = when {
    status == MealDeleteStatus.Done -> MealDetailsUiState.Deleted
    // Room may emit the missing row before the delete call returns: do not flash "not found".
    meal == null && status == MealDeleteStatus.InProgress -> MealDetailsUiState.Loading
    meal == null -> MealDetailsUiState.NotFound
    else -> MealDetailsUiState.Content(meal, deleteFailed = status == MealDeleteStatus.Failed)
}

@HiltViewModel
class MealDetailsViewModel @Inject constructor(
    private val repository: MealRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // The route argument is a String (NavType.StringType): an invalid id ends in NotFound.
    private val mealId: Long? = savedStateHandle.get<String>(NavArgs.MEAL_ID)?.toLongOrNull()

    private val deleteStatus = MutableStateFlow(MealDeleteStatus.Idle)

    val uiState: StateFlow<MealDetailsUiState> =
        combine(mealId?.let(repository::observeMeal) ?: flowOf(null), deleteStatus, ::mealDetailsUiState)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MealDetailsUiState.Loading)

    /**
     * Deletes the meal after the user confirmed it. The screen must not pop the back stack itself:
     * that would cancel viewModelScope before Room finishes. It waits for [MealDetailsUiState.Deleted].
     */
    fun deleteMeal() {
        val id = mealId ?: return
        if (deleteStatus.value == MealDeleteStatus.InProgress || deleteStatus.value == MealDeleteStatus.Done) return
        deleteStatus.update { MealDeleteStatus.InProgress }
        viewModelScope.launch {
            try {
                val deleted = repository.deleteMeal(id)
                deleteStatus.update { if (deleted) MealDeleteStatus.Done else MealDeleteStatus.Failed }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                deleteStatus.update { MealDeleteStatus.Failed }
            }
        }
    }
}
