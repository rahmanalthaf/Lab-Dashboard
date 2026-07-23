package com.labdashboard.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.labdashboard.app.ui.screens.*
import com.labdashboard.app.util.CsvExporter
import com.labdashboard.app.viewmodel.LabViewModel
import kotlinx.coroutines.launch

private sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Destination("dashboard", "Dashboard", Icons.Default.Home)
    object Students : Destination("students", "Students", Icons.Default.Person)
    object Experiments : Destination("experiments", "Experiments", Icons.Default.Science)
    object ScoreEntry : Destination("scores", "Scores", Icons.Default.Edit)
    object Leaderboard : Destination("leaderboard", "Leaderboard", Icons.Default.Leaderboard)
}

private val destinations = listOf(
    Destination.Dashboard,
    Destination.Students,
    Destination.Experiments,
    Destination.ScoreEntry,
    Destination.Leaderboard
)

/** Width breakpoint above which we switch from a bottom bar to a side rail - covers tablets and landscape phones. */
private const val WIDE_SCREEN_BREAKPOINT_DP = 600

@Composable
fun LabDashboardApp(viewModel: LabViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val useRail = screenWidthDp >= WIDE_SCREEN_BREAKPOINT_DP

    val experiments by viewModel.experiments.collectAsState()
    val performances by viewModel.performances.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentTitle = destinations.firstOrNull { it.route == currentRoute }?.label ?: "Lab Dashboard"

    fun navigate(destination: Destination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun exportReport() {
        if (performances.isEmpty()) {
            scope.launch { snackbarHostState.showSnackbar("Add students and marks before exporting.") }
            return
        }
        CsvExporter.shareCsv(context, experiments, performances)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(currentTitle) },
                actions = {
                    IconButton(onClick = { exportReport() }) {
                        Icon(Icons.Default.Share, contentDescription = "Export report as CSV")
                    }
                }
            )
        },
        bottomBar = {
            if (!useRail) {
                NavigationBar {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = { navigate(destination) },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Row(Modifier.padding(padding).fillMaxSize()) {
            if (useRail) {
                NavigationRail {
                    destinations.forEach { destination ->
                        NavigationRailItem(
                            selected = currentRoute == destination.route,
                            onClick = { navigate(destination) },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = Destination.Dashboard.route,
                modifier = Modifier.weight(1f).fillMaxSize()
            ) {
                composable(Destination.Dashboard.route) { DashboardScreen(viewModel) }
                composable(Destination.Students.route) { StudentsScreen(viewModel, snackbarHostState) }
                composable(Destination.Experiments.route) { ExperimentsScreen(viewModel, snackbarHostState) }
                composable(Destination.ScoreEntry.route) { ScoreEntryScreen(viewModel) }
                composable(Destination.Leaderboard.route) { LeaderboardScreen(viewModel) }
            }
        }
    }
}
