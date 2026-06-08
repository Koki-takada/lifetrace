package com.toroncho.lifetrace.domain.model

data class Prompt(
    val id: Long = 0,
    val text: String,
    val scheduledTime: String,  // "HH:mm" 形式
    val isEnabled: Boolean = true,
    val isDefault: Boolean = false,
)