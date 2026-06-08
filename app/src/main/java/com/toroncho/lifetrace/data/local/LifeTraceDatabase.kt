package com.toroncho.lifetrace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [EntryEntity::class, PromptEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class LifeTraceDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun promptDao(): PromptDao
}