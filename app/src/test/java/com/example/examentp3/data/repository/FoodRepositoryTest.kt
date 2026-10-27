package com.example.examentp3.data.repository

import com.example.examentp3.data.remote.FoodApi
import com.example.examentp3.data.remote.dto.FoodDto
import com.example.examentp3.data.remote.dto.FoodListResponseDto
import com.example.examentp3.data.remote.dto.NutritionDto
import com.example.examentp3.data.remote.dto.PageMetaDto
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** [FoodRepository] reads GET /v1/foods and maps it to the app model (EXT-05). */
class FoodRepositoryTest {

    private val api = FakeFoodApi()
    private val repository = FoodRepository(api)

    private val rice = FoodDto("f02", "Basmati rice", NutritionDto(130.0, 2.7, 28.0, 0.3))

    @Test
    fun blankQuery_isLeftOutOfTheRequest() = runBlocking {
        api.response = { FoodListResponseDto(listOf(rice), PageMetaDto(1, 10, 1)) }

        repository.search("  ")

        assertNull(api.lastQuery)
        assertEquals(1, api.lastPage)
        assertEquals(FOOD_PAGE_SIZE, api.lastPageSize)
    }

    @Test
    fun query_isTrimmedAndSent() = runBlocking {
        api.response = { FoodListResponseDto(listOf(rice), PageMetaDto(1, 10, 1)) }

        repository.search(" rice ", page = 2)

        assertEquals("rice", api.lastQuery)
        assertEquals(2, api.lastPage)
    }

    @Test
    fun results_mapNutritionPer100gAndUseMeta() = runBlocking {
        api.response = { FoodListResponseDto(listOf(rice), PageMetaDto(1, 10, 12)) }

        val page = repository.search("").getOrThrow()

        assertEquals("Basmati rice", page.items.single().name)
        assertEquals(28.0, page.items.single().carbsPer100g, 0.0)
        assertEquals(12, page.total)
        assertTrue(page.hasMore)
    }

    @Test
    fun foodWithoutNutrition_isDropped() = runBlocking {
        api.response = { FoodListResponseDto(listOf(rice, FoodDto("f99", "Mystery", null)), PageMetaDto(1, 10, 2)) }

        assertEquals(listOf("f02"), repository.search("").getOrThrow().items.map { it.id })
    }

    @Test
    fun emptyScenario_isAnEmptyPageNotAFailure() = runBlocking {
        api.response = { FoodListResponseDto(emptyList(), PageMetaDto(1, 10, 0)) }

        val page = repository.search("pizza").getOrThrow()

        assertTrue(page.items.isEmpty())
        assertFalse(page.hasMore)
    }

    @Test
    fun responseWithoutData_isFailure() = runBlocking {
        api.response = { FoodListResponseDto(null, null) }

        assertTrue(repository.search("").isFailure)
    }

    @Test
    fun networkError_isFailure() = runBlocking {
        api.response = { throw IOException("no connection") }

        assertTrue(repository.search("").isFailure)
    }
}

private class FakeFoodApi : FoodApi {
    var response: () -> FoodListResponseDto = { FoodListResponseDto(emptyList(), PageMetaDto(1, 10, 0)) }
    var lastQuery: String? = null
    var lastPage: Int? = null
    var lastPageSize: Int? = null

    override suspend fun getFoods(query: String?, page: Int, pageSize: Int): FoodListResponseDto {
        lastQuery = query
        lastPage = page
        lastPageSize = pageSize
        return response()
    }
}
