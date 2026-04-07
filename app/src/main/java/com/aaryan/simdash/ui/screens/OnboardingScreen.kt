package com.aaryan.simdash.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.aaryan.simdash.ui.components.PermissionCard
import com.aaryan.simdash.ui.viewmodels.OnboardingViewModel
import com.aaryan.simdash.util.PermissionUtils

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var currentStep by remember { mutableIntStateOf(1) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentStep) {
                1 -> WelcomeStep(onNext = { currentStep = 2 })
                2 -> PermissionsStep(onNext = { 
                    viewModel.detectSim()
                    currentStep = 3 
                })
                3 -> PlanTypeStep(
                    detectedCarrier = uiState.detectedCarrier,
                    onPlanSelected = { type ->
                        viewModel.selectPlanType(type)
                    },
                    selectedType = uiState.selectedPlanType,
                    onFinish = {
                        viewModel.completeOnboarding(onSuccess = onFinish)
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeStep(onNext: () -> Unit) {
    Text(
        text = "Welcome to SimDash",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Text(
        text = "Track your mobile data securely with a beautiful home widget. No internet required, ever.",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    )
    Button(
        onClick = onNext,
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text("Get Started")
    }
}

@Composable
fun PermissionsStep(onNext: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasPhone by remember { mutableStateOf(PermissionUtils.hasPhoneStatePermission(context)) }
    var hasUsage by remember { mutableStateOf(PermissionUtils.hasUsageAccessPermission(context)) }
    var hasNotif by remember { mutableStateOf(PermissionUtils.hasNotificationPermission(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPhone = PermissionUtils.hasPhoneStatePermission(context)
                hasUsage = PermissionUtils.hasUsageAccessPermission(context)
                hasNotif = PermissionUtils.hasNotificationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val phoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasPhone = granted }

    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasNotif = granted }

    Text(
        text = "Permissions",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "SimDash works entirely offline but requires these permissions to track your data securely.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    PermissionCard(
        title = "Phone State",
        description = "Identifies your active SIM card to track data correctly per carrier.",
        icon = Icons.Default.Phone,
        isGranted = hasPhone,
        onGrantClick = {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                phoneLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            }
        }
    )

    PermissionCard(
        title = "Usage Access",
        description = "Crucial to measure how much data your device has consumed accurately.",
        icon = Icons.Default.Info,
        isGranted = hasUsage,
        onGrantClick = {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    )

    PermissionCard(
        title = "Notifications",
        description = "Used to warn you when your data runs out or plan expires.",
        icon = Icons.Default.Notifications,
        isGranted = hasNotif,
        onGrantClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onNext,
        enabled = hasUsage, // Require Usage Access at minimum as specified in PRD
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(if (hasPhone && hasUsage && hasNotif) "Continue" else "Skip & Continue")
    }
}

@Composable
fun PlanTypeStep(
    detectedCarrier: String,
    onPlanSelected: (String) -> Unit,
    selectedType: String,
    onFinish: () -> Unit
) {
    Text(
        text = "Detected SIM",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = detectedCarrier,
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 32.dp)
    )
    
    Text(
        text = "How does your mobile data plan work?",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
    )

    PlanOption(
        title = "I get fresh data every day",
        desc = "e.g., 2 GB daily limit",
        isSelected = selectedType == "DAILY_LIMIT",
        onClick = { onPlanSelected("DAILY_LIMIT") }
    )
    PlanOption(
        title = "I have a total data pool",
        desc = "e.g., 50 GB for the month",
        isSelected = selectedType == "TOTAL_POOL",
        onClick = { onPlanSelected("TOTAL_POOL") }
    )
    PlanOption(
        title = "Truly unlimited",
        desc = "No data cap",
        isSelected = selectedType == "TRULY_UNLIMITED",
        onClick = { onPlanSelected("TRULY_UNLIMITED") }
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onFinish,
        enabled = selectedType.isNotEmpty(),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text("Finish Setup")
    }
}

@Composable
fun PlanOption(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = if(isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = if(isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
