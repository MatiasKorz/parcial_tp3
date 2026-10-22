package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MealRoomCheckUiState(
    val meals: List<Meal> = emptyList(),
    val saveFailed: Boolean = false,
)

/** Provisional check of the first Room insert and read; NUC-04, NUC-05 and NUC-06 replace it. */
@HiltViewModel
class MealRoomCheckViewModel @Inject constructor(
    private val repository: MealRepository,
) : ViewModel() {

    private val saveFailed = MutableStateFlow(false)

    val uiState: StateFlow<MealRoomCheckUiState> =
        combine(repository.observeMeals(), saveFailed) { meals, failed ->
            MealRoomCheckUiState(meals = meals, saveFailed = failed)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MealRoomCheckUiState())

    fun insertSampleMeal() {
        viewModelScope.launch {
            saveFailed.update { false }
            try {
                repository.addMeal(sampleMeal(date = LocalDate.now()))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                saveFailed.update { true }
            }
        }
    }
}

// Values from the Figma Nutrition frames.
private fun sampleMeal(date: LocalDate) = Meal(
    name = "Chicken · rice · vegetables",
    date = date,
    type = MealType.LUNCH,
    calories = 520,
    proteinGrams = 38.0,
    carbsGrams = 54.0,
    fatGrams = 14.0,
)
