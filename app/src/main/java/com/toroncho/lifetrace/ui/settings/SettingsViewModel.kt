package com.toroncho.lifetrace.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toroncho.lifetrace.data.repository.PromptRepository
import com.toroncho.lifetrace.domain.model.Prompt
import com.toroncho.lifetrace.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val promptRepository: PromptRepository,
    private val scheduler: NotificationScheduler,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val prompts = promptRepository.getAllPrompts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addPrompt(text: String, scheduledTime: String) {
        viewModelScope.launch {
            val id = promptRepository.addPrompt(text, scheduledTime)
            val newPrompt = Prompt(id = id, text = text, scheduledTime = scheduledTime)
            scheduler.schedulePrompt(context, newPrompt)
        }
    }

    fun togglePrompt(prompt: Prompt) {
        viewModelScope.launch {
            val updated = prompt.copy(isEnabled = !prompt.isEnabled)
            promptRepository.updatePrompt(updated)
            if (updated.isEnabled) scheduler.schedulePrompt(context, updated)
            else scheduler.cancelPrompt(context, updated)
        }
    }

    fun deletePrompt(prompt: Prompt) {
        viewModelScope.launch {
            scheduler.cancelPrompt(context, prompt)
            promptRepository.deletePrompt(prompt)
        }
    }
}