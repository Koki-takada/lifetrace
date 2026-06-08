package com.toroncho.lifetrace.di

import android.content.Context
import androidx.room.Room
import com.toroncho.lifetrace.data.local.EntryDao
import com.toroncho.lifetrace.data.local.LifeTraceDatabase
import com.toroncho.lifetrace.data.local.PromptDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LifeTraceDatabase =
        Room.databaseBuilder(context, LifeTraceDatabase::class.java, "lifetrace.db").build()

    @Provides
    fun provideEntryDao(db: LifeTraceDatabase): EntryDao = db.entryDao()

    @Provides
    fun providePromptDao(db: LifeTraceDatabase): PromptDao = db.promptDao()
}
