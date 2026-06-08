package com.toroncho.lifetrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.toroncho.lifetrace.notification.EXTRA_PROMPT_ID
import com.toroncho.lifetrace.notification.EXTRA_PROMPT_TEXT
import com.toroncho.lifetrace.ui.navigation.NavGraph
import com.toroncho.lifetrace.ui.theme.LifeTraceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
}