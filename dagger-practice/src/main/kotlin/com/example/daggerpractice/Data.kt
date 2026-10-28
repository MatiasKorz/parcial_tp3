package com.example.daggerpractice

import java.time.Clock
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

data class Workout(val id: String, val name: String)

/** Stands in for the Retrofit interface of the app: the practice does not touch the network. */
interface WorkoutApi {
    val baseUrl: String
    fun getWorkouts(): List<Workout>
}

/** Marks which String is the base URL, like a qualifier such as @ApplicationContext marks a Context. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl

/** Our class: Dagger builds it from its @Inject constructor, no module needed. */
class FakeWorkoutApi @Inject constructor(@param:BaseUrl override val baseUrl: String) : WorkoutApi {
    override fun getWorkouts(): List<Workout> = listOf(
        Workout("w01", "Active Recovery"),
        Workout("w02", "Upper Body Strength"),
        Workout("w03", "Lower Body Strength"),
    )
}

/** Same shape as the app's repositories: @Singleton + @Inject constructor. */
@Singleton
class WorkoutRepository @Inject constructor(
    private val api: WorkoutApi,
    private val clock: Clock,
) {
    fun workouts(): List<Workout> = api.getWorkouts()
    fun find(id: String): Workout? = workouts().firstOrNull { it.id == id }
    fun loadedAt(): String = clock.instant().toString()
}
