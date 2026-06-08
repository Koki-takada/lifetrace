package com.toroncho.lifetrace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.toroncho.lifetrace.domain.model.Entry
import java.time.LocalDateTime

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val promptText: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun EntryEntity.toDomain() = Entry(
    id = id,
    content = content,
    promptText = promptText,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Entry.toEntity() = EntryEntity(
    id = id,
    content = content,
    promptText = promptText,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
