
package com.example.examentp3.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ExampleUiState(
    val message: String = "La aplicación está funcionando",
    val counter: Int = 0
)

@HiltViewModel
class ExampleViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ExampleUiState())
    val uiState = _uiState.asStateFlow()

    fun incrementCounter() {
        _uiState.value = _uiState.value.copy(
            counter = _uiState.value.counter + 1
        )
    }
}
