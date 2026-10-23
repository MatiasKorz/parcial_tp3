package com.example.examentp3.data.local

import com.example.examentp3.data.remote.dto.WorkoutSummaryDto
import com.example.examentp3.data.remote.dto.toModel
import com.example.examentp3.model.WorkoutSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkoutSummaryEntityMapperTest {

    private val workout = WorkoutSummary(
        id = "w01",
        name = "Fuerza inicial",
        level = "beginner",
        durationMinutes = 25,
        exerciseCount = 3,
    )

    @Test
    fun entityToModel_roundTripKeepsAllFields() {
        assertEquals(workout, workout.toEntity().toModel())
    }

    @Test
    fun dtoWithoutId_isDropped() {
        assertNull(WorkoutSummaryDto(null, "Fuerza inicial", "beginner", 25, 3).toModel())
    }

    @Test
    fun dtoWithMissingFields_mapsToDefaults() {
        val model = WorkoutSummaryDto("w01", null, null, null, null).toModel()
        assertEquals(WorkoutSummary("w01", "", "", 0, 0), model)
    }
}
