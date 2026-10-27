package com.example.examentp3.data.repository

import com.example.examentp3.data.remote.FoodApi
import com.example.examentp3.data.remote.dto.toModel
import com.example.examentp3.model.FoodPage
import com.google.gson.JsonParseException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

/** Default page size of GET /v1/foods (1 to 50 in the contract). */
const val FOOD_PAGE_SIZE = 10

/**
 * Food search of EXT-05. Only reads the mock: the meal built from the results is saved in Room by
 * [MealRepository], never in the mock (SPEC p.4).
 */
@Singleton
class FoodRepository @Inject constructor(
    private val api: FoodApi,
) {
    /** A blank [query] lists every food. An empty page is a valid answer, a failure is not. */
    suspend fun search(query: String, page: Int = 1): Result<FoodPage> = try {
        val response = api.getFoods(query.trim().ifEmpty { null }, page, FOOD_PAGE_SIZE)
        val items = response.data ?: throw JsonParseException("Response without data")
        val meta = response.meta
        Result.success(
            FoodPage(
                items = items.mapNotNull { it.toModel() },
                page = meta?.page ?: page,
                pageSize = meta?.pageSize ?: FOOD_PAGE_SIZE,
                // Without meta there is no way to know about more pages: show this one only.
                total = meta?.total ?: ((page - 1) * FOOD_PAGE_SIZE + items.size),
            ),
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
