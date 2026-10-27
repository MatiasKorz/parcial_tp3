package com.example.examentp3.data.remote

import com.example.examentp3.data.remote.dto.CoachMessageRequestDto
import com.example.examentp3.data.remote.dto.CoachReplyResponseDto
import com.example.examentp3.data.remote.dto.CoachSuggestionListResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CoachApi {
    @GET("v1/coach/suggestions")
    suspend fun getSuggestions(): CoachSuggestionListResponseDto

    // The Gson converter sends Content-Type: application/json, which the mock requires (415 otherwise).
    @POST("v1/coach/messages")
    suspend fun sendMessage(@Body body: CoachMessageRequestDto): CoachReplyResponseDto
}
