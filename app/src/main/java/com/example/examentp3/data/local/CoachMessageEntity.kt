package com.example.examentp3.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.examentp3.model.CoachMessage
import com.example.examentp3.model.CoachRole

@Entity(tableName = "coach_messages")
data class CoachMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    // Stored by enum name, not ordinal, like the meal type.
    val role: String,
    val text: String,
    @ColumnInfo(name = "sent_at_millis") val sentAtMillis: Long,
    @ColumnInfo(name = "is_simulated") val isSimulated: Boolean,
)

/** An unknown role in an old row reads as the coach's. */
fun CoachMessageEntity.toModel(): CoachMessage = CoachMessage(
    id = id,
    role = CoachRole.entries.firstOrNull { it.name == role } ?: CoachRole.Assistant,
    text = text,
    sentAtMillis = sentAtMillis,
    isSimulated = isSimulated,
)

fun CoachMessage.toEntity(): CoachMessageEntity = CoachMessageEntity(
    id = id,
    role = role.name,
    text = text,
    sentAtMillis = sentAtMillis,
    isSimulated = isSimulated,
)
