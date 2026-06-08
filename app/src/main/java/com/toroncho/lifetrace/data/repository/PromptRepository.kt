package com.toroncho.lifetrace.data.repository

import com.toroncho.lifetrace.data.local.PromptDao
import com.toroncho.lifetrace.data.local.PromptEntity
import com.toroncho.lifetrace.data.local.toDomain
import com.toroncho.lifetrace.data.local.toEntity
import com.toroncho.lifetrace.domain.model.Prompt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val DEFAULT_PROMPTS = listOf(
    PromptEntity(text = "今やっていることで、私は何を避けようとしているのか", scheduledTime = "11:00", isEnabled = true, isDefault = true),
    PromptEntity(text = "過去2時間のフィルムを見たら、私は人生に何を望んでいると結論づけられるか？", scheduledTime = "13:30", isEnabled = true, isDefault = true),
    PromptEntity(text = "私はバッドエンドに向かっているか。それともグッドエンドに向かっているか", scheduledTime = "15:15", isEnabled = true, isDefault = true),
    PromptEntity(text = "私が重要でないふりをしている、最も重要なことは何か？", scheduledTime = "17:00", isEnabled = true, isDefault = true),
    PromptEntity(text = "今日、最も「生」を感じたのはいつ？最も「死」を感じたのはいつ？", scheduledTime = "21:00", isEnabled = true, isDefault = true),
)

@Singleton
class PromptRepository @Inject constructor(private val dao: PromptDao) {

    fun getAllPrompts(): Flow<List<Prompt>> =
        dao.getAllPrompts().map { list -> list.map { it.toDomain() } }

    suspend fun getEnabledPrompts(): List<Prompt> =
        dao.getEnabledPrompts().map { it.toDomain() }

    suspend fun initializeDefaultsIfEmpty() {
        if (dao.getCount() == 0) {
            dao.insertAll(DEFAULT_PROMPTS)
        }
    }

    suspend fun addPrompt(text: String, scheduledTime: String): Long {
        val entity = PromptEntity(text = text, scheduledTime = scheduledTime, isEnabled = true, isDefault = false)
        return dao.insertPrompt(entity)
    }

    suspend fun updatePrompt(prompt: Prompt) {
        dao.updatePrompt(prompt.toEntity())
    }

    suspend fun deletePrompt(prompt: Prompt) {
        dao.deletePrompt(prompt.toEntity())
    }
}