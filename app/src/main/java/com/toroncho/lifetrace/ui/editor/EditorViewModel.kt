package com.toroncho.lifetrace.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toroncho.lifetrace.data.repository.EntryRepository
import com.toroncho.lifetrace.domain.model.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
) : ViewModel() {

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    private var editingEntry: Entry? = null

    fun loadEntry(entryId: Long) {
        viewModelScope.launch {
            val entry = entryRepository.getEntryById(entryId) ?: return@launch
            editingEntry = entry
            _content.value = entry.content
        }
    }

    fun onContentChange(value: String) {
        _content.value = value
    }

    fun save(promptText: String) {
        if (_content.value.isBlank()) return
        viewModelScope.launch {
            val existing = editingEntry
            if (existing == null) {
                entryRepository.saveEntry(_content.value.trim(), promptText)
            } else {
                entryRepository.updateEntry(existing.copy(content = _content.value.trim()))
            }
            _isSaved.value = true
        }
    }
}