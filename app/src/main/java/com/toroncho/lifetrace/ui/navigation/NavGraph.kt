package com.toroncho.lifetrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.toroncho.lifetrace.notification.EXTRA_PROMPT_ID
import com.toroncho.lifetrace.notification.EXTRA_PROMPT_TEXT
import com.toroncho.lifetrace.ui.editor.EditorScreen
import com.toroncho.lifetrace.ui.home.HomeScreen
import com.toroncho.lifetrace.ui.settings.SettingsScreen

private const val DEFAULT_PROMPT = "今何を考えていますか？"

@Composable
fun NavGraph(
    initialPromptId: Long?,
    initialPromptText: String?,
) {
    val navController = rememberNavController()
    val startDestination = if (initialPromptId != null) "editor" else "home"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            HomeScreen(
                onNewEntry = { navController.navigate("editor") },
                onEditEntry = { id -> navController.navigate("editor?entryId=$id") },
                onSettings = { navController.navigate("settings") },
            )
        }
        composable(
            route = "editor?entryId={entryId}",
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                },
            ),
        ) { backStack ->
            val entryId = backStack.arguments?.getLong("entryId")?.takeIf { it != -1L }
            EditorScreen(
                promptText = initialPromptText ?: DEFAULT_PROMPT,
                entryId = entryId,
                onBack = { navController.popBackStack() },
            )
        }
        // 通知からの起動（promptIdあり）
        composable("editor") {
            EditorScreen(
                promptText = initialPromptText ?: DEFAULT_PROMPT,
                entryId = null,
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                    }
                },
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}