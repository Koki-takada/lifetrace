package com.toroncho.lifetrace.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHOW_PROMPT) return
        val promptId = intent.getLongExtra(EXTRA_PROMPT_ID, -1L)
        val promptText = intent.getStringExtra(EXTRA_PROMPT_TEXT) ?: return
        if (promptId == -1L) return
        NotificationHelper.showNotification(context, promptId, promptText)
    }
}