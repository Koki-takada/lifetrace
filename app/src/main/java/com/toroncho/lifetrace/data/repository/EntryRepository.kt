package com.toroncho.lifetrace.data.repository

import com.toroncho.lifetrace.data.local.EntryDao
import com.toroncho.lifetrace.data.local.toDomain
import com.toroncho.lifetrace.data.local.toEntity
import com.toroncho.lifetrace.domain.model.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(private val dao: EntryDao) {

    fun getAllEntries(): Flow<List<Entry>> =
        dao.getAllEntries().map { list -> list.map { it.toDomain() } }

    suspend fun getEntryById(id: Long): Entry? =
        dao.getEntryById(id)?.toDomain()

    suspend fun saveEntry(content: String, promptText: String): Long {
        val now = LocalDateTime.now()
        val entity = Entry(
            content = content,
            promptText = promptText,
            createdAt = now,
            updatedAt = now,
        ).toEntity()
        return dao.insertEntry(entity)
    }

    suspend fun updateEntry(entry: Entry) {
        dao.updateEntry(entry.copy(updatedAt = LocalDateTime.now()).toEntity())
    }

    suspend fun deleteEntry(entry: Entry) {
        dao.deleteEntry(entry.toEntity())
    }
}