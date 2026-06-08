package com.toroncho.lifetrace.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.toroncho.lifetrace.domain.model.Prompt
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

const val ACTION_SHOW_PROMPT = "com.toroncho.lifetrace.SHOW_PROMPT"

@Singleton
class NotificationScheduler @Inject constructor() {

    fun scheduleAll(context: Context, prompts: List<Prompt>) {
        prompts.forEach { prompt ->
            if (prompt.isEnabled) schedulePrompt(context, prompt)
            else cancelPrompt(context, prompt)
        }
    }

    fun schedulePrompt(context: Context, prompt: Prompt) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = buildPendingIntent(context, prompt) ?: return

        val (hour, minute) = prompt.scheduledTime.split(":").map { it.toInt() }
        var triggerTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute))
        if (triggerTime.isBefore(LocalDateTime.now())) {
            triggerTime = triggerTime.plusDays(1)
        }
        val triggerMillis = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
    }

    fun cancelPrompt(context: Context, prompt: Prompt) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        buildPendingIntent(context, prompt)?.let { alarmManager.cancel(it) }
    }

    private fun buildPendingIntent(context: Context, prompt: Prompt): PendingIntent? {
        if (prompt.id == 0L) return null
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SHOW_PROMPT
            putExtra(EXTRA_PROMPT_ID, prompt.id)
            putExtra(EXTRA_PROMPT_TEXT, prompt.text)
        }
        return PendingIntent.getBroadcast(
            context,
            prompt.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}