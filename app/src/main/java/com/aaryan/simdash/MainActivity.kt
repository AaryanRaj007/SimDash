package com.aaryan.simdash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aaryan.simdash.data.prefs.SettingsDataStore
import com.aaryan.simdash.ui.navigation.MainNavGraph
import com.aaryan.simdash.ui.navigation.NavRoutes
import com.aaryan.simdash.ui.theme.SimDashTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimDashTheme {
                val isSetupComplete = settingsDataStore.isSetupComplete.collectAsState(initial = null)
                
                if (isSetupComplete.value != null) {
                    val navController = rememberNavController()
                    val startRoute = if (isSetupComplete.value == true) NavRoutes.Home.route else NavRoutes.Onboarding.route
                    
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainNavGraph(
                            navController = navController,
                            startDestination = startRoute,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}