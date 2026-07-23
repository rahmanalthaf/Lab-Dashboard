package com.labdashboard.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labdashboard.app.ui.components.EmptyState
import com.labdashboard.app.ui.components.GradeBadge
import com.labdashboard.app.viewmodel.LabViewModel
import java.util.Locale

@Composable
fun LeaderboardScreen(viewModel: LabViewModel) {
    val performances by viewModel.performances.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val ranked = performances
        .sortedByDescending { it.percentage }
        .filter { it.student.name.contains(searchQuery, ignoreCase = true) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
                OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search student") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        if (ranked.isEmpty()) {
            EmptyState("No performance data yet.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(ranked) { index, perf ->
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("#${index + 1}", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(perf.student.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Roll: ${perf.student.rollNumber}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    String.format(Locale.US, "%.1f%%", perf.percentage),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.width(8.dp))
                                GradeBadge(perf.grade)
                            }
                        }
                    }
                }
            }
        }
    }
}
