package com.example.examentp3.data.remote.dto

import com.example.examentp3.model.Food
import com.google.gson.annotations.SerializedName

/** Envelope of GET /v1/foods: {"data": [...], "meta": {page, pageSize, total}}. */
data class FoodListResponseDto(
    @SerializedName("data") val data: List<FoodDto>?,
    @SerializedName("meta") val meta: PageMetaDto?,
)

data class PageMetaDto(
    @SerializedName("page") val page: Int?,
    @SerializedName("pageSize") val pageSize: Int?,
    @SerializedName("total") val total: Int?,
)

data class FoodDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("nutritionPer100g") val nutritionPer100g: NutritionDto?,
)

data class NutritionDto(
    @SerializedName("kcal") val kcal: Double?,
    @SerializedName("proteinG") val proteinG: Double?,
    @SerializedName("carbsG") val carbsG: Double?,
    @SerializedName("fatG") val fatG: Double?,
)

/** Without an id, a name or its nutrition the portion cannot be computed, so the food is dropped. */
fun FoodDto.toModel(): Food? {
    val safeId = id ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null
    val nutrition = nutritionPer100g ?: return null
    return Food(
        id = safeId,
        name = safeName,
        kcalPer100g = nutrition.kcal ?: 0.0,
        proteinPer100g = nutrition.proteinG ?: 0.0,
        carbsPer100g = nutrition.carbsG ?: 0.0,
        fatPer100g = nutrition.fatG ?: 0.0,
    )
}
