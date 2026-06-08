package com.toroncho.lifetrace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.toroncho.lifetrace.domain.model.Prompt

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val scheduledTime: String,
    val isEnabled: Boolean,
    val isDefault: Boolean,
)

fun PromptEntity.toDomain() = Prompt(
    id = id,
    text = text,
    scheduledTime = scheduledTime,
    isEnabled = isEnabled,
    isDefault = isDefault,
)

fun Prompt.toEntity() = PromptEntity(
    id = id,
    text = text,
    scheduledTime = scheduledTime,
    isEnabled = isEnabled,
    isDefault = isDefault,
)
