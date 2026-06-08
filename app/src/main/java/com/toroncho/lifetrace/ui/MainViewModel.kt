package com.toroncho.lifetrace.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _pendingPromptText = MutableStateFlow<String?>(null)
    val pendingPromptText = _pendingPromptText.asStateFlow()

    fun onPromptReceived(text: String) {
        _pendingPromptText.value = text
    }

    fun onPromptConsumed() {
        _pendingPromptText.value = null
    }
}