package com.example.examentp3.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examentp3.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "DAT-01"

/** Provisional check of the first Retrofit query, logged to Logcat; NUC-01 and NUC-03 replace it. */
@HiltViewModel
class WorkoutConnectionCheckViewModel @Inject constructor(
    private val repository: WorkoutRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.getWorkouts()
                .onSuccess { workouts -> Log.d(TAG, "GET v1/workouts OK: ${workouts.map { it.id }} $workouts") }
                .onFailure { error -> Log.w(TAG, "GET v1/workouts failed: ${error.javaClass.simpleName}", error) }
        }
    }
}