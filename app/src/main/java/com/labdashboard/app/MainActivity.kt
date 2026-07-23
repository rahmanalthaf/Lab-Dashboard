package com.labdashboard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.labdashboard.app.ui.LabDashboardApp
import com.labdashboard.app.ui.theme.LabDashboardTheme
import com.labdashboard.app.viewmodel.LabViewModel
import com.labdashboard.app.viewmodel.LabViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val app = application as LabApplication

        setContent {
            LabDashboardTheme {
                val viewModel: LabViewModel = viewModel(factory = LabViewModelFactory(app.repository))
                LabDashboardApp(viewModel)
            }
        }
    }
}
