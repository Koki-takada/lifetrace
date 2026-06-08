package com.toroncho.lifetrace.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.toroncho.lifetrace.MainActivity
import com.toroncho.lifetrace.R

const val CHANNEL_ID = "lifetrace_daily_prompt"
const val EXTRA_PROMPT_TEXT = "prompt_text"
const val EXTRA_PROMPT_ID = "prompt_id"
const val EXTRA_SCHEDULED_TIME = "scheduled_time"

object NotificationHelper {

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "今日の問いかけ",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "毎日の自己問答通知"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, promptId: Long, promptText: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_PROMPT_ID, promptId)
            putExtra(EXTRA_PROMPT_TEXT, promptText)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            promptId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("LifeTrace")
            .setContentText(promptText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(promptText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(promptId.toInt(), notification)
    }
}