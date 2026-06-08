package com.toroncho.lifetrace.domain.model

import java.time.LocalDateTime

data class Entry(
    val id: Long = 0,
    val content: String,
    val promptText: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)