package com.toroncho.lifetrace.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.toroncho.lifetrace.data.repository.PromptRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var promptRepository: PromptRepository
    @Inject lateinit var scheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        CoroutineScope(Dispatchers.IO).launch {
            val prompts = promptRepository.getEnabledPrompts()
            scheduler.scheduleAll(context, prompts)
        }
    }
}