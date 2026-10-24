package com.example.examentp3.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory [MealDao] for unit tests (shared by NUC-05 and NUC-04). Every query re-emits when the
 * list changes, like Room does when the meals table changes. It mirrors the WHERE and ORDER BY of
 * each query in Kotlin: it tests the wiring of the repository, not the SQL.
 */
class FakeMealDao(initial: List<MealEntity> = emptyList()) : MealDao {

    private val rows = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0) + 1

    /** Current rows, for assertions. */
    val entities: List<MealEntity> get() = rows.value

    override suspend fun insert(meal: MealEntity): Long {
        // Room's default @Insert aborts on a duplicate primary key.
        require(meal.id == 0L || rows.value.none { it.id == meal.id }) { "Duplicate meal id ${meal.id}" }
        val id = if (meal.id == 0L) nextId else meal.id
        nextId = maxOf(nextId, id + 1)
        rows.update { it + meal.copy(id = id) }
        return id
    }

    override fun observeAll(): Flow<List<MealEntity>> =
        rows.map { list -> list.sortedWith(compareByDescending<MealEntity> { it.dateEpochDay }.thenByDescending { it.id }) }

    override fun observeByDate(epochDay: Long): Flow<List<MealEntity>> =
        rows.map { list -> list.filter { it.dateEpochDay == epochDay }.sortedBy { it.id } }

    override fun observeById(id: Long): Flow<MealEntity?> = rows.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun deleteById(id: Long): Int {
        val before = rows.value.size
        rows.update { list -> list.filterNot { it.id == id } }
        return before - rows.value.size
    }
}
