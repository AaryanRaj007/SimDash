package com.aaryan.simdash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaryan.simdash.ui.components.DataRing
import com.aaryan.simdash.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SimDash") },
                actions = {
                    IconButton(onClick = { viewModel.refreshUsage() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val profile = uiState.profile

            if (profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            Text(
                text = profile.displayName.ifEmpty { profile.carrierName },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val limitMB = profile.dailyLimitBytes / (1024f * 1024f)
            val usedMB = profile.todayUsedBytes / (1024f * 1024f)
            val limitGB = limitMB / 1024f
            val percent = if (profile.dailyLimitBytes > 0) {
                (profile.todayUsedBytes.toFloat() / profile.dailyLimitBytes).coerceIn(0f, 1f)
            } else 0f

            val ringColor = when {
                percent < 0.5f -> Color(0xFF1D9E75)
                percent < 0.9f -> Color(0xFFBA7517)
                else -> Color(0xFFE24B4A)
            }

            DataRing(
                percent = percent,
                ringColor = ringColor,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 24.dp)
            )

            if (profile.dailyLimitBytes > 0) {
                Text(
                    text = "${"%.1f".format(limitMB - usedMB)} MB left of ${"%.1f".format(limitGB)} GB",
                    style = MaterialTheme.typography.titleLarge
                )
            } else {
                Text("No data limit set", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val validityStr = if (profile.validityEndDate > 0) {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
                val diff = profile.validityEndDate - System.currentTimeMillis()
                val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diff)
                "Valid till ${sdf.format(Date(profile.validityEndDate))} ($days days left)"
            } else {
                "Validity Unknown"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("Plan Validity", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(validityStr, style = MaterialTheme.typography.titleMedium)
                }
            }

            Button(
                onClick = { viewModel.triggerUssd() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isRefreshing
            ) {
                Text(if (uiState.isRefreshing) "Requesting..." else "Refresh via USSD")
            }

            uiState.ussdRawMessage?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("USSD Response:", style = MaterialTheme.typography.labelMedium)
                        Text(msg, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
