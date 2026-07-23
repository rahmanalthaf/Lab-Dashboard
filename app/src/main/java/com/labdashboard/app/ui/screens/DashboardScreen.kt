package com.labdashboard.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labdashboard.app.ui.components.SimpleBarChart
import com.labdashboard.app.ui.components.StatCard
import com.labdashboard.app.ui.theme.*
import com.labdashboard.app.viewmodel.LabViewModel
import java.util.Locale

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: LabViewModel) {
    val stats by viewModel.dashboardStats.collectAsState()
    val experiments by viewModel.experiments.collectAsState()
    val performances by viewModel.performances.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
                LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 240.dp)
        ) {
            items(
                listOf(
                    Triple("Students", stats.totalStudents.toString(), Indigo40),
                    Triple("Experiments", experiments.size.toString(), Teal40),
                    Triple("Class Average", String.format(Locale.US, "%.1f%%", stats.classAverage), Amber40),
                    Triple("Top Score", String.format(Locale.US, "%.1f%%", stats.topScore), Green40)
                )
            ) { (title, value, color) ->
                StatCard(title = title, value = value, accent = color, modifier = Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Performance by Student (%)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (performances.isEmpty()) {
            Text("Add students and experiments, then enter marks to see this chart update live.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        } else {
            SimpleBarChart(
                labels = performances.map { it.student.name.take(6) },
                values = performances.map { it.percentage },
                maxValue = 100.0
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("Pass / Fail", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Text("Pass: ${stats.passCount}", color = Green40, style = MaterialTheme.typography.titleMedium)
            Text("Fail: ${stats.failCount}", color = Red40, style = MaterialTheme.typography.titleMedium)
        }
    }
}
