package com.example.examentp3.data.repository

import com.example.examentp3.data.local.ExerciseEntity
import com.example.examentp3.data.local.WorkoutDao
import com.example.examentp3.data.local.WorkoutDetailDao
import com.example.examentp3.data.local.WorkoutDetailEntity
import com.example.examentp3.data.local.WorkoutSummaryEntity
import com.example.examentp3.data.local.WorkoutWithExercises
import com.example.examentp3.data.remote.WorkoutApi
import com.example.examentp3.data.remote.dto.ExerciseDto
import com.example.examentp3.data.remote.dto.WorkoutDetailDto
import com.example.examentp3.data.remote.dto.WorkoutDetailResponseDto
import com.example.examentp3.data.remote.dto.WorkoutListResponseDto
import com.example.examentp3.data.remote.dto.WorkoutSummaryDto
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Checks that [WorkoutRepository] coordinates Retrofit and Room (RF06): the API only writes the cache,
 * the UI only reads Room, and a failed download never touches what was cached.
 */
class WorkoutRepositoryTest {

    private val api = FakeWorkoutApi()
    private val workoutDao = FakeWorkoutDao()
    private val detailDao = FakeWorkoutDetailDao()
    private val repository = WorkoutRepository(api, workoutDao, detailDao)

    private val w01 = WorkoutSummaryDto("w01", "Fuerza inicial", "beginner", 25, 2)
    private val w02 = WorkoutSummaryDto("w02", "Cardio suave", "beginner", 20, 3)
    private val squat = ExerciseDto("e01", "Sentadilla", "Piernas", 3, 10, 60, "Flexioná las rodillas.", "Controlá la carga.")
    private val row = ExerciseDto("e02", "Remo", "Espalda", 3, 10, 60, "Llevá la mancuerna al torso.", "Controlá la carga.")

    @Test
    fun refreshWorkouts_success_cachesInRoomAndUiReadsIt() = runBlocking {
        api.workouts = { WorkoutListResponseDto(listOf(w01, w02)) }

        val result = repository.refreshWorkouts()

        assertEquals(2, result.getOrNull())
        assertEquals(listOf("w01", "w02"), repository.observeWorkouts().first().map { it.id })
    }

    @Test
    fun refreshWorkouts_failure_keepsTheCachedCatalog() = runBlocking {
        api.workouts = { WorkoutListResponseDto(listOf(w01)) }
        repository.refreshWorkouts()

        api.workouts = { throw IOException("no connection") }
        val result = repository.refreshWorkouts()

        assertTrue(result.isFailure)
        assertEquals(listOf("w01"), repository.observeWorkouts().first().map { it.id })
    }

    @Test
    fun refreshWorkouts_emptyAnswer_reportsZeroAndKeepsTheCache() = runBlocking {
        api.workouts = { WorkoutListResponseDto(listOf(w01)) }
        repository.refreshWorkouts()

        api.workouts = { WorkoutListResponseDto(emptyList()) }
        val result = repository.refreshWorkouts()

        assertEquals(0, result.getOrNull())
        assertEquals(listOf("w01"), repository.observeWorkouts().first().map { it.id })
    }

    @Test
    fun refreshWorkouts_responseWithoutData_isFailure() = runBlocking {
        api.workouts = { WorkoutListResponseDto(null) }

        assertTrue(repository.refreshWorkouts().isFailure)
        assertTrue(workoutDao.rows.value.isEmpty())
    }

    @Test
    fun refreshWorkouts_dropsWorkoutsWithoutIdButCountsWhatTheServerSent() = runBlocking {
        api.workouts = { WorkoutListResponseDto(listOf(w01, w02.copy(id = null))) }

        val result = repository.refreshWorkouts()

        assertEquals(2, result.getOrNull())
        assertEquals(listOf("w01"), repository.observeWorkouts().first().map { it.id })
    }

    @Test
    fun observeWorkoutDetail_neverDownloaded_emitsNull() = runBlocking {
        assertNull(repository.observeWorkoutDetail("w01").first())
    }

    @Test
    fun refreshWorkoutDetail_success_cachesDetailWithExercisesInApiOrder() = runBlocking {
        api.detail = { id -> WorkoutDetailResponseDto(WorkoutDetailDto(id, "Fuerza inicial", "beginner", 25, "Rutina.", listOf(row, squat))) }

        val result = repository.refreshWorkoutDetail("w01")

        assertTrue(result.isSuccess)
        val detail = repository.observeWorkoutDetail("w01").first()
        assertEquals("Fuerza inicial", detail?.name)
        assertEquals(listOf("e02", "e01"), detail?.exercises?.map { it.id })
        assertEquals("Sentadilla", repository.observeExercise("w01", "e01").first()?.name)
    }

    @Test
    fun refreshWorkoutDetail_failure_keepsTheCachedDetail() = runBlocking {
        api.detail = { id -> WorkoutDetailResponseDto(WorkoutDetailDto(id, "Fuerza inicial", "beginner", 25, "Rutina.", listOf(squat))) }
        repository.refreshWorkoutDetail("w01")

        api.detail = { throw IOException("no connection") }
        val result = repository.refreshWorkoutDetail("w01")

        assertTrue(result.isFailure)
        assertEquals("Fuerza inicial", repository.observeWorkoutDetail("w01").first()?.name)
    }

    @Test
    fun refreshWorkoutDetail_dropsExercisesTheServerNoLongerSends() = runBlocking {
        api.detail = { id -> WorkoutDetailResponseDto(WorkoutDetailDto(id, "Fuerza inicial", "beginner", 25, "", listOf(squat, row))) }
        repository.refreshWorkoutDetail("w01")

        api.detail = { id -> WorkoutDetailResponseDto(WorkoutDetailDto(id, "Fuerza inicial", "beginner", 25, "", listOf(squat))) }
        repository.refreshWorkoutDetail("w01")

        assertEquals(listOf("e01"), repository.observeWorkoutDetail("w01").first()?.exercises?.map { it.id })
        assertNull(repository.observeExercise("w01", "e02").first())
    }
}

/** Each test sets what the "server" answers; the default fails like a server that is down. */
private class FakeWorkoutApi : WorkoutApi {
    var workouts: suspend () -> WorkoutListResponseDto = { throw IOException("not configured") }
    var detail: suspend (String) -> WorkoutDetailResponseDto = { throw IOException("not configured") }

    override suspend fun getWorkouts(): WorkoutListResponseDto = workouts()

    override suspend fun getWorkout(workoutId: String): WorkoutDetailResponseDto = detail(workoutId)
}

/** In-memory [WorkoutDao] that re-emits on every change, like Room. */
private class FakeWorkoutDao : WorkoutDao {
    val rows = MutableStateFlow<List<WorkoutSummaryEntity>>(emptyList())

    override suspend fun upsertAll(workouts: List<WorkoutSummaryEntity>) {
        rows.update { current -> (current.associateBy { it.id } + workouts.associateBy { it.id }).values.toList() }
    }

    override fun observeAll(): Flow<List<WorkoutSummaryEntity>> = rows.map { list -> list.sortedBy { it.id } }
}

/** In-memory [WorkoutDetailDao]; `replace` is the real default method of the interface. */
private class FakeWorkoutDetailDao : WorkoutDetailDao {
    private val workouts = MutableStateFlow<Map<String, WorkoutDetailEntity>>(emptyMap())
    private val exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())

    override fun observe(workoutId: String): Flow<WorkoutWithExercises?> =
        combine(workouts, exercises) { w, e ->
            w[workoutId]?.let { workout -> WorkoutWithExercises(workout, e.filter { it.workoutId == workoutId }) }
        }

    override fun observeExercise(workoutId: String, exerciseId: String): Flow<ExerciseEntity?> =
        exercises.map { list -> list.firstOrNull { it.workoutId == workoutId && it.exerciseId == exerciseId } }

    override suspend fun upsertWorkout(workout: WorkoutDetailEntity) {
        workouts.update { it + (workout.workoutId to workout) }
    }

    override suspend fun deleteExercises(workoutId: String) {
        exercises.update { list -> list.filterNot { it.workoutId == workoutId } }
    }

    override suspend fun insertExercises(exercises: List<ExerciseEntity>) {
        this.exercises.update { it + exercises }
    }
}
