package com.toroncho.lifetrace

import android.app.Application
import com.toroncho.lifetrace.data.repository.PromptRepository
import com.toroncho.lifetrace.notification.NotificationHelper
import com.toroncho.lifetrace.notification.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LifeTraceApp : Application() {

    @Inject lateinit var promptRepository: PromptRepository
    @Inject lateinit var scheduler: NotificationScheduler

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        CoroutineScope(Dispatchers.IO).launch {
            promptRepository.initializeDefaultsIfEmpty()
            val prompts = promptRepository.getEnabledPrompts()
            scheduler.scheduleAll(this@LifeTraceApp, prompts)
        }
    }
}