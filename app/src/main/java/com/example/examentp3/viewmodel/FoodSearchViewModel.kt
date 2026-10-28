package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.FoodRepository
import com.example.examentp3.model.Food
import com.example.examentp3.model.FoodPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "FoodSearch"

/** Wait after the last keystroke before asking the mock, so typing does not send one request per letter. */
private const val SEARCH_DEBOUNCE_MS = 300L

data class FoodSearchUiState(
    val query: String = "",
    val results: List<Food> = emptyList(),
    /** Last page loaded; 0 before the first answer. */
    val page: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiError? = null,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && page > 0 && results.isEmpty()
}

/** A first page replaces the results; the next ones are appended (pagination with `meta`). */
internal fun FoodSearchUiState.withPage(result: FoodPage): FoodSearchUiState = copy(
    results = if (result.page <= 1) result.items else results + result.items.filter { new -> results.none { it.id == new.id } },
    page = result.page,
    hasMore = result.hasMore,
    isLoading = false,
    error = null,
)

/** A failure keeps the results already shown (RF05): the error goes next to them. */
internal fun FoodSearchUiState.withError(error: UiError): FoodSearchUiState =
    copy(isLoading = false, error = error)

/**
 * Food search inside Food Confirmation (EXT-05), with GET /v1/foods. It only finds foods: the chosen
 * portions live in [FoodConfirmationViewModel], which saves the meal in Room.
 */
@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val repository: FoodRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodSearchUiState())
    val uiState: StateFlow<FoodSearchUiState> = _uiState.asStateFlow()

    private var request: Job? = null

    init {
        // An empty query lists every food, so the sheet opens with something to pick.
        load(page = 1)
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        load(page = 1, debounce = true)
    }

    fun onLoadMore() {
        val state = _uiState.value
        if (state.hasMore && !state.isLoading) load(page = state.page + 1)
    }

    /** Repeats what failed: the first page if nothing is shown, otherwise the next one. */
    fun retry() {
        val state = _uiState.value
        if (state.isLoading) return
        load(page = if (state.results.isEmpty()) 1 else state.page + 1)
    }

    private fun load(page: Int, debounce: Boolean = false) {
        // A newer search cancels the previous one, so an old answer never overwrites the new results.
        request?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null) }
        val query = _uiState.value.query
        request = viewModelScope.launch {
            if (debounce) delay(SEARCH_DEBOUNCE_MS)
            repository.search(query, page)
                .onSuccess { result -> _uiState.update { it.withPage(result) } }
                .onFailure { error ->
                    Log.w(TAG, "GET v1/foods failed: ${error.javaClass.simpleName}", error)
                    _uiState.update { it.withError(error.toUiError()) }
                }
        }
    }
}
