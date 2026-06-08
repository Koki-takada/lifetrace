package com.toroncho.lifetrace.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SHOW_PROMPT) return
        val promptId = intent.getLongExtra(EXTRA_PROMPT_ID, -1L)
        val promptText = intent.getStringExtra(EXTRA_PROMPT_TEXT) ?: return
        val scheduledTime = intent.getStringExtra(EXTRA_SCHEDULED_TIME) ?: return
        if (promptId == -1L) return

        NotificationHelper.showNotification(context, promptId, promptText)

        // setExactAndAllowWhileIdle は1回限りなので翌日分を再スケジュール
        rescheduleNext(context, promptId, promptText, scheduledTime)
    }

    private fun rescheduleNext(
        context: Context,
        promptId: Long,
        promptText: String,
        scheduledTime: String,
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) return

        val (hour, minute) = scheduledTime.split(":").map { it.toInt() }
        val tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(hour, minute))
        val triggerMillis = tomorrow.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val nextIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SHOW_PROMPT
            putExtra(EXTRA_PROMPT_ID, promptId)
            putExtra(EXTRA_PROMPT_TEXT, promptText)
            putExtra(EXTRA_SCHEDULED_TIME, scheduledTime)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            promptId.toInt(),
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
    }
}