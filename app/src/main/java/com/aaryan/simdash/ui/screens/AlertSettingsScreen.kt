package com.aaryan.simdash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaryan.simdash.data.db.AlertThreshold
import com.aaryan.simdash.ui.viewmodels.AlertSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertSettingsScreen(
    viewModel: AlertSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alert Settings") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Threshold")
            }
        }
    ) { innerPadding ->
        if (uiState.thresholds.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No custom alerts configured.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.thresholds) { threshold ->
                    ThresholdRow(
                        threshold = threshold,
                        onToggle = { enabled -> viewModel.toggleThreshold(threshold, enabled) },
                        onDelete = { viewModel.deleteThreshold(threshold) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddThresholdDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { percent, msg, color ->
                    viewModel.addThreshold(percent, msg, color)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ThresholdRow(
    threshold: AlertThreshold,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${threshold.percentageThreshold}% Threshold",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(android.graphics.Color.parseColor(threshold.colorHex))
                )
                if (!threshold.customMessage.isNullOrEmpty()) {
                    Text(
                        text = threshold.customMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = threshold.isEnabled,
                onCheckedChange = onToggle
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddThresholdDialog(
    onDismiss: () -> Unit,
    onAdd: (Int, String, String) -> Unit
) {
    var percentStr by remember { mutableStateOf("80") }
    var customMsg by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("#BA7517") } // Default Amber

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Alert Threshold") },
        text = {
            Column {
                OutlinedTextField(
                    value = percentStr,
                    onValueChange = { percentStr = it },
                    label = { Text("Percentage (1-100)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customMsg,
                    onValueChange = { customMsg = it },
                    label = { Text("Custom Message (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                // In a real app we'd have a color picker here, just hardcode options for now
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { colorHex = "#1D9E75" }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D9E75))) { Text("G") }
                    Button(onClick = { colorHex = "#BA7517" }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA7517))) { Text("A") }
                    Button(onClick = { colorHex = "#E24B4A" }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE24B4A))) { Text("R") }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val p = percentStr.toIntOrNull() ?: 80
                    onAdd(p.coerceIn(1..100), customMsg, colorHex)
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
