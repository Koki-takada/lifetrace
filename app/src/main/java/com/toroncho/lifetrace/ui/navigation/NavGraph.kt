package com.toroncho.lifetrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.toroncho.lifetrace.ui.MainViewModel
import com.toroncho.lifetrace.ui.editor.EditorScreen
import com.toroncho.lifetrace.ui.home.HomeScreen
import com.toroncho.lifetrace.ui.settings.SettingsScreen

private const val DEFAULT_PROMPT = "今何を考えていますか？"

@Composable
fun NavGraph(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val pendingPromptText by mainViewModel.pendingPromptText.collectAsState()

    // 通知タップ時（onCreate / onNewIntent どちらの場合も）エディタ画面へ遷移
    LaunchedEffect(pendingPromptText) {
        if (pendingPromptText != null) {
            navController.navigate("editor") {
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = "home") {
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
                promptText = DEFAULT_PROMPT,
                entryId = entryId,
                onBack = { navController.popBackStack() },
            )
        }
        composable("editor") {
            val promptText = pendingPromptText ?: DEFAULT_PROMPT
            EditorScreen(
                promptText = promptText,
                entryId = null,
                onBack = {
                    mainViewModel.onPromptConsumed()
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