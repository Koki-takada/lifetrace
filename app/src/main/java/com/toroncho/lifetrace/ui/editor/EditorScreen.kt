package com.toroncho.lifetrace.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    promptText: String,
    entryId: Long?,
    onBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val content by viewModel.content.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(entryId) {
        if (entryId != null) viewModel.loadEntry(entryId)
    }
    LaunchedEffect(isSaved) {
        if (isSaved) onBack()
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = promptText,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Button(
                    onClick = { viewModel.save(promptText) },
                    enabled = content.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text("保存する")
                }
            }
        },
    ) { innerPadding ->
        OutlinedTextField(
            value = content,
            onValueChange = viewModel::onContentChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .focusRequester(focusRequester),
            placeholder = { Text("思ったことを自由に...") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            ),
        )
    }
}