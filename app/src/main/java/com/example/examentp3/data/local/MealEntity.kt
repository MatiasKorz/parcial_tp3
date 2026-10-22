package com.example.examentp3.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.examentp3.model.Meal
import com.example.examentp3.model.MealType
import java.time.LocalDate

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    // Epoch day keeps dates sortable in SQL without a TypeConverter.
    @ColumnInfo(name = "date_epoch_day") val dateEpochDay: Long,
    // Stored by enum name, not ordinal, so reordering MealType does not corrupt rows.
    @ColumnInfo(name = "meal_type") val mealType: String,
    val calories: Int,
    @ColumnInfo(name = "protein_g") val proteinGrams: Double,
    @ColumnInfo(name = "carbs_g") val carbsGrams: Double,
    @ColumnInfo(name = "fat_g") val fatGrams: Double,
)

fun MealEntity.toModel(): Meal = Meal(
    id = id,
    name = name,
    date = LocalDate.ofEpochDay(dateEpochDay),
    type = MealType.valueOf(mealType),
    calories = calories,
    proteinGrams = proteinGrams,
    carbsGrams = carbsGrams,
    fatGrams = fatGrams,
)

fun Meal.toEntity(): MealEntity = MealEntity(
    id = id,
    name = name,
    dateEpochDay = date.toEpochDay(),
    mealType = type.name,
    calories = calories,
    proteinGrams = proteinGrams,
    carbsGrams = carbsGrams,
    fatGrams = fatGrams,
)
