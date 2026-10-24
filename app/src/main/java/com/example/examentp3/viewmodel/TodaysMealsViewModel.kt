package com.example.examentp3.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.MealRepository
import com.example.examentp3.model.DailyTotals
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealSection
import com.example.examentp3.model.toDailyTotals
import com.example.examentp3.model.toMealSections
import com.example.examentp3.ui.navigation.NavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TodaysMealsUiState(
    val date: LocalDate,
    val isToday: Boolean,
    /** Always one section per MealType, in Figma order; empty types have no meals. */
    val sections: List<MealSection>,
    val totals: DailyTotals,
    val isLoading: Boolean = false,
)

/** Pure builder of the list state, kept outside the ViewModel so it is unit-tested without Android. */
internal fun todaysMealsUiState(date: LocalDate, meals: List<Meal>, today: LocalDate): TodaysMealsUiState =
    TodaysMealsUiState(
        date = date,
        isToday = date == today,
        sections = meals.toMealSections(),
        totals = meals.toDailyTotals(),
    )

/** Date the list opens on: the route argument (epoch day) if it is valid, otherwise [today]. */
internal fun initialEpochDay(routeArgument: String?, today: LocalDate): Long =
    routeArgument?.toLongOrNull() ?: today.toEpochDay()

@HiltViewModel
class TodaysMealsViewModel @Inject constructor(
    private val repository: MealRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // The chosen day lives in SavedStateHandle: it survives rotation and process death, and a Long
    // fits in the Bundle. The route argument only sets the first value.
    private val selectedEpochDay: StateFlow<Long> = savedStateHandle.getStateFlow(
        SELECTED_EPOCH_DAY_KEY,
        initialEpochDay(savedStateHandle.get<String>(NavArgs.DATE_EPOCH_DAY), LocalDate.now()),
    )

    // flatMapLatest drops the previous day's query when the date changes, so only one stays alive.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TodaysMealsUiState> = selectedEpochDay
        .flatMapLatest { epochDay ->
            val date = LocalDate.ofEpochDay(epochDay)
            repository.observeMealsByDate(date).map { meals -> todaysMealsUiState(date, meals, LocalDate.now()) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = todaysMealsUiState(
                date = LocalDate.ofEpochDay(selectedEpochDay.value),
                meals = emptyList(),
                today = LocalDate.now(),
            ).copy(isLoading = true),
        )

    fun onPreviousDay() {
        savedStateHandle[SELECTED_EPOCH_DAY_KEY] = selectedEpochDay.value - 1
    }

    fun onNextDay() {
        savedStateHandle[SELECTED_EPOCH_DAY_KEY] = selectedEpochDay.value + 1
    }

    fun onDateSelected(date: LocalDate) {
        savedStateHandle[SELECTED_EPOCH_DAY_KEY] = date.toEpochDay()
    }

    private companion object {
        const val SELECTED_EPOCH_DAY_KEY = "selectedEpochDay"
    }
}
