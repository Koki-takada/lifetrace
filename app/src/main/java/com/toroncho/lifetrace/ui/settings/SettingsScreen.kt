package com.toroncho.lifetrace.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.toroncho.lifetrace.domain.model.Prompt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val prompts by viewModel.prompts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("問いかけ設定") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "追加")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(prompts, key = { it.id }) { prompt ->
                PromptItem(
                    prompt = prompt,
                    onToggle = { viewModel.togglePrompt(prompt) },
                    onDelete = { viewModel.deletePrompt(prompt) },
                )
            }
        }
    }

    if (showAddDialog) {
        AddPromptDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { text, time ->
                viewModel.addPrompt(text, time)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun PromptItem(
    prompt: Prompt,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prompt.scheduledTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = prompt.text,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(
                checked = prompt.isEnabled,
                onCheckedChange = { onToggle() },
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun AddPromptDialog(
    onDismiss: () -> Unit,
    onConfirm: (text: String, time: String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf(false) }

    val timeRegex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("問いかけを追加") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("問いかけ文") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = {
                        time = it
                        timeError = false
                    },
                    label = { Text("通知時刻 (HH:mm)") },
                    placeholder = { Text("例: 09:00") },
                    isError = timeError,
                    supportingText = if (timeError) ({ Text("HH:mm 形式で入力してください") }) else null,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!timeRegex.matches(time)) {
                        timeError = true
                        return@TextButton
                    }
                    if (text.isNotBlank()) onConfirm(text.trim(), time)
                },
            ) { Text("追加") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        },
    )
}