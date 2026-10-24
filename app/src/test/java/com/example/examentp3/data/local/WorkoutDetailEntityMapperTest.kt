package com.example.examentp3.data.local

import com.example.examentp3.data.remote.dto.ExerciseDto
import com.example.examentp3.data.remote.dto.WorkoutDetailDto
import com.example.examentp3.data.remote.dto.toModel
import com.example.examentp3.model.Exercise
import com.example.examentp3.model.WorkoutDetail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkoutDetailEntityMapperTest {

    private val squat = Exercise("e01", "Sentadilla", "Piernas", 3, 10, 60, "Flexioná las rodillas.", "Controlá la carga.")
    private val row = Exercise("e02", "Remo con mancuerna", "Espalda", 3, 10, 60, "Llevá la mancuerna al torso.", "Controlá la carga.")
    private val bridge = Exercise("e04", "Puente de glúteos", "Piernas", 3, 10, 60, "Elevá la cadera.", "Controlá la carga.")

    private val w01 = WorkoutDetail("w01", "Fuerza inicial", "beginner", 25, "Rutina de ejemplo.", listOf(squat, row))
    private val w03 = WorkoutDetail("w03", "Fuerza y constancia", "beginner", 30, "Rutina de ejemplo.", listOf(squat, bridge, row))

    private fun WorkoutDetail.roundTrip() = WorkoutWithExercises(toEntity(), toExerciseEntities()).toModel()

    @Test
    fun entityToModel_roundTripKeepsAllFields() {
        assertEquals(w03, w03.roundTrip())
    }

    @Test
    fun entityToModel_keepsTheApiOrderEvenIfRoomReturnsItShuffled() {
        val shuffled = WorkoutWithExercises(w03.toEntity(), w03.toExerciseEntities().reversed())
        assertEquals(listOf("e01", "e04", "e02"), shuffled.toModel().exercises.map { it.id })
    }

    @Test
    fun sharedExerciseIds_getDifferentKeysPerWorkout() {
        val keys01 = w01.toExerciseEntities().map { it.workoutId to it.exerciseId }
        val keys03 = w03.toExerciseEntities().map { it.workoutId to it.exerciseId }
        assertEquals(emptySet<Pair<String, String>>(), keys01.toSet() intersect keys03.toSet())
        assertNotEquals(keys01.first(), keys03.first())
    }

    @Test
    fun dtoWithoutId_isDropped() {
        assertNull(WorkoutDetailDto(null, "Fuerza inicial", "beginner", 25, "", emptyList()).toModel())
    }

    @Test
    fun dtoExerciseWithoutId_isDropped() {
        val dto = WorkoutDetailDto(
            "w01", "Fuerza inicial", "beginner", 25, "",
            listOf(ExerciseDto(null, "Sin id", null, null, null, null, null, null)),
        )
        assertEquals(emptyList<Exercise>(), dto.toModel()?.exercises)
    }

    @Test
    fun dtoWithMissingFields_mapsToDefaults() {
        val model = WorkoutDetailDto("w01", null, null, null, null, null).toModel()
        assertEquals(WorkoutDetail("w01", "", "", 0, "", emptyList()), model)
    }
}
