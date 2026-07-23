package com.labdashboard.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labdashboard.app.data.Experiment
import com.labdashboard.app.ui.components.ConfirmDeleteDialog
import com.labdashboard.app.ui.components.EmptyState
import com.labdashboard.app.ui.components.ExperimentEditDialog
import com.labdashboard.app.viewmodel.LabViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ExperimentsScreen(viewModel: LabViewModel, snackbarHostState: SnackbarHostState) {
    val experiments by viewModel.experiments.collectAsState()
    val marks by viewModel.marks.collectAsState()
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingExperiment by remember { mutableStateOf<Experiment?>(null) }
    var deletingExperiment by remember { mutableStateOf<Experiment?>(null) }

    // Class average for each experiment, computed only from students who have a mark recorded for it.
    val averageByExperimentId = experiments.associate { exp ->
        val recorded = marks.filter { it.experimentId == exp.id }
        val avg = if (recorded.isNotEmpty()) recorded.map { it.marksObtained }.average() else null
        exp.id to avg
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add experiment")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                        if (experiments.isEmpty()) {
                EmptyState("No experiments yet. Tap + to add one.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(experiments, key = { it.id }) { experiment ->
                        Card(shape = RoundedCornerShape(12.dp)) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(experiment.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Max marks: ${experiment.maxMarks}", style = MaterialTheme.typography.bodyMedium)
                                    averageByExperimentId[experiment.id]?.let { avg ->
                                        Text(
                                            String.format(Locale.US, "Class average: %.1f", avg),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Row {
                                    IconButton(onClick = { editingExperiment = experiment }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = { deletingExperiment = experiment }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ExperimentEditDialog(
            title = "Add Experiment",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, maxMarks ->
                viewModel.addExperiment(name, maxMarks)
                showAddDialog = false
                scope.launch { snackbarHostState.showSnackbar("Added $name") }
            }
        )
    }

    editingExperiment?.let { experiment ->
        ExperimentEditDialog(
            initialName = experiment.name,
            initialMax = experiment.maxMarks.toString(),
            title = "Edit Experiment",
            onDismiss = { editingExperiment = null },
            onConfirm = { name, maxMarks ->
                viewModel.updateExperiment(experiment.copy(name = name, maxMarks = maxMarks))
                editingExperiment = null
                scope.launch { snackbarHostState.showSnackbar("Updated $name") }
            }
        )
    }

    deletingExperiment?.let { experiment ->
        ConfirmDeleteDialog(
            itemLabel = experiment.name,
            onDismiss = { deletingExperiment = null },
            onConfirm = {
                viewModel.deleteExperiment(experiment)
                deletingExperiment = null
                scope.launch { snackbarHostState.showSnackbar("Deleted ${experiment.name}") }
            }
        )
    }
}
