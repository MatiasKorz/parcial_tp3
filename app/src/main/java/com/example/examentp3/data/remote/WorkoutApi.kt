package com.example.examentp3.data.remote

import com.example.examentp3.data.remote.dto.WorkoutListResponseDto
import retrofit2.http.GET

interface WorkoutApi {
    @GET("v1/workouts")
    suspend fun getWorkouts(): WorkoutListResponseDto
}