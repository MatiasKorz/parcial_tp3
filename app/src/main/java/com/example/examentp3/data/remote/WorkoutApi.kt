package com.example.examentp3.data.remote

import com.example.examentp3.data.remote.dto.WorkoutDetailResponseDto
import com.example.examentp3.data.remote.dto.WorkoutListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkoutApi {
    @GET("v1/workouts")
    suspend fun getWorkouts(): WorkoutListResponseDto

    @GET("v1/workouts/{workoutId}")
    suspend fun getWorkout(@Path("workoutId") workoutId: String): WorkoutDetailResponseDto
}
