package com.aaryan.simdash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaryan.simdash.ui.viewmodels.SimInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimInfoScreen(
    viewModel: SimInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var limitInput by remember { mutableStateOf("") }
    var ussdInput by remember { mutableStateOf("") }

    // Sync input fields with profile initially
    LaunchedEffect(uiState.profile) {
        if (limitInput.isEmpty() && uiState.profile != null) {
            val mb = uiState.profile!!.dailyLimitBytes / (1024L * 1024L)
            if (mb > 0) limitInput = mb.toString()
            
            if (uiState.profile!!.ussdCode.isNotEmpty()) {
                ussdInput = uiState.profile!!.ussdCode
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("SIM & Plan Info") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val profile = uiState.profile
            if (profile == null) {
                Text("No SIM detected or permission missing.")
                return@Column
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Carrier: ${profile.carrierName}", style = MaterialTheme.typography.titleMedium)
                    Text("MCC/MNC: ${profile.mccMnc}", style = MaterialTheme.typography.bodyMedium)
                    Text("Plan Type: ${profile.planType}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            OutlinedTextField(
                value = limitInput,
                onValueChange = { limitInput = it },
                label = { Text("Daily Limit (MB)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val limit = limitInput.toLongOrNull() ?: 0L
                    viewModel.updatePlanLimit(limit)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Limit")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            OutlinedTextField(
                value = ussdInput,
                onValueChange = { ussdInput = it },
                label = { Text("Custom USSD Check Code (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.updateUssdCode(ussdInput)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save USSD Code")
            }
        }
    }
}
