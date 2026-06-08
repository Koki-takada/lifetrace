package com.toroncho.lifetrace

import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.toroncho.lifetrace.notification.EXTRA_PROMPT_ID
import com.toroncho.lifetrace.notification.EXTRA_PROMPT_TEXT
import com.toroncho.lifetrace.ui.navigation.NavGraph
import com.toroncho.lifetrace.ui.theme.LifeTraceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestPermissionsIfNeeded()

        val promptId = intent.getLongExtra(EXTRA_PROMPT_ID, -1L).takeIf { it != -1L }
        val promptText = intent.getStringExtra(EXTRA_PROMPT_TEXT)

        setContent {
            LifeTraceTheme {
                NavGraph(
                    initialPromptId = promptId,
                    initialPromptText = promptText,
                )
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        // POST_NOTIFICATIONS（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        // SCHEDULE_EXACT_ALARM（Android 12+）：設定画面へ誘導
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                )
            }
        }
    }
}