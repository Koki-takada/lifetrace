package com.toroncho.lifetrace.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toroncho.lifetrace.data.repository.EntryRepository
import com.toroncho.lifetrace.domain.model.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
) : ViewModel() {

    val entries = entryRepository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch { entryRepository.deleteEntry(entry) }
    }
}