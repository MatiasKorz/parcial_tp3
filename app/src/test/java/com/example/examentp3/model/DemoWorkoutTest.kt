package com.example.examentp3.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoWorkoutTest {

    // Workout ids of GET /v1/workouts in the mock contract (REF-04).
    private val contractIds = setOf("w01", "w02", "w03")

    @Test
    fun everyDemoCard_opensAWorkoutOfTheContract() {
        assertTrue(DemoWorkout.entries.all { it.workoutId in contractIds })
    }

    @Test
    fun demoCards_openDistinctWorkouts() {
        assertEquals(DemoWorkout.entries.size, DemoWorkout.entries.map { it.workoutId }.toSet().size)
    }

    @Test
    fun upperBody_opensTheOnlyIntermediateWorkout() {
        assertEquals("w02", DemoWorkout.UpperBody.workoutId)
    }
}
