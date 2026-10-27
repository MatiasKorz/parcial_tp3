package com.example.examentp3.data.remote

import com.example.examentp3.data.remote.dto.FoodListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {
    // A null query is left out of the URL: the mock answers 400 to empty `page=` or `pageSize=` (REF-04).
    @GET("v1/foods")
    suspend fun getFoods(
        @Query("q") query: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): FoodListResponseDto
}
