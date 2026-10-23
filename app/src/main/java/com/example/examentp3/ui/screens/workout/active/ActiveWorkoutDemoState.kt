package com.example.examentp3.ui.screens.workout.active

/** Local demonstration only; no elapsed-time tracking or persisted workout data. */
internal data class ActiveWorkoutDemoState(
    val completedSets: Int = 1,
    val weightSteps: Int = 18,
    val restSeconds: Int = 78,
) {
    val allSetsComplete: Boolean get() = completedSets == 3
    val currentSet: Int get() = (completedSets + 1).coerceAtMost(3)
    val weightKg: Double get() = weightSteps * 2.5

    fun completeSet(): ActiveWorkoutDemoState = when {
        allSetsComplete -> this
        completedSets == 2 -> copy(completedSets = 3, restSeconds = 0)
        else -> copy(completedSets = completedSets + 1, restSeconds = 78)
    }

    fun addRest(): ActiveWorkoutDemoState =
        if (allSetsComplete) this else copy(restSeconds = restSeconds + 30)

    fun skipRest(): ActiveWorkoutDemoState = copy(restSeconds = 0)
    fun increaseWeight(): ActiveWorkoutDemoState = copy(weightSteps = weightSteps + 1)
    fun decreaseWeight(): ActiveWorkoutDemoState = copy(weightSteps = (weightSteps - 1).coerceAtLeast(0))
}
