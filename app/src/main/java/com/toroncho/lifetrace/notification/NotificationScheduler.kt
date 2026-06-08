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
        // Android 12+ では exact alarm の許可が必要
        if (!alarmManager.canScheduleExactAlarms()) return
        val pendingIntent = buildPendingIntent(context, prompt) ?: return

        val triggerMillis = nextTriggerMillis(prompt.scheduledTime)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent,
        )
    }

    fun cancelPrompt(context: Context, prompt: Prompt) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        buildPendingIntent(context, prompt)?.let { alarmManager.cancel(it) }
    }

    fun buildPendingIntent(context: Context, prompt: Prompt): PendingIntent? {
        if (prompt.id == 0L) return null
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SHOW_PROMPT
            putExtra(EXTRA_PROMPT_ID, prompt.id)
            putExtra(EXTRA_PROMPT_TEXT, prompt.text)
            putExtra(EXTRA_SCHEDULED_TIME, prompt.scheduledTime)
        }
        return PendingIntent.getBroadcast(
            context,
            prompt.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        fun nextTriggerMillis(scheduledTime: String): Long {
            val (hour, minute) = scheduledTime.split(":").map { it.toInt() }
            var trigger = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute))
            if (!trigger.isAfter(LocalDateTime.now())) trigger = trigger.plusDays(1)
            return trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }
}