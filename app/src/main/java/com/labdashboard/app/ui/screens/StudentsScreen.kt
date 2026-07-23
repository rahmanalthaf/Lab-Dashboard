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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labdashboard.app.data.Student
import com.labdashboard.app.ui.components.ConfirmDeleteDialog
import com.labdashboard.app.ui.components.EmptyState
import com.labdashboard.app.ui.components.GradeBadge
import com.labdashboard.app.ui.components.StudentEditDialog
import com.labdashboard.app.viewmodel.LabViewModel
import kotlinx.coroutines.launch
import java.util.Locale

private enum class SortBy(val label: String) { NAME("Name"), ROLL("Roll No."), AVERAGE("Average") }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(viewModel: LabViewModel, snackbarHostState: SnackbarHostState) {
    val students by viewModel.students.collectAsState()
    val performances by viewModel.performances.collectAsState()
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var deletingStudent by remember { mutableStateOf<Student?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf(SortBy.NAME) }

    val averageByStudentId = performances.associate { it.student.id to it.percentage }
    val gradeByStudentId = performances.associate { it.student.id to it.grade }
    val existingRolls = students.map { it.rollNumber.trim().lowercase() }

    val filtered = students.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.rollNumber.contains(searchQuery, ignoreCase = true)
    }
    val sorted = when (sortBy) {
        SortBy.NAME -> filtered.sortedBy { it.name.lowercase() }
        SortBy.ROLL -> filtered.sortedBy { it.rollNumber.lowercase() }
        SortBy.AVERAGE -> filtered.sortedByDescending { averageByStudentId[it.id] ?: 0.0 }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add student")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                        OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name or roll number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Sort by:", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
                SortBy.entries.forEach { option ->
                    FilterChip(
                        selected = sortBy == option,
                        onClick = { sortBy = option },
                        label = { Text(option.label) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            if (sorted.isEmpty()) {
                EmptyState(if (students.isEmpty()) "No students yet. Tap + to add one." else "No matches.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sorted, key = { it.id }) { student ->
                        Card(shape = RoundedCornerShape(12.dp)) {
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(student.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Roll: ${student.rollNumber}", style = MaterialTheme.typography.bodyMedium)
                                    averageByStudentId[student.id]?.let { avg ->
                                        Text(
                                            String.format(Locale.US, "Average: %.1f%%", avg),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    gradeByStudentId[student.id]?.let { GradeBadge(it) }
                                    IconButton(onClick = { editingStudent = student }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = { deletingStudent = student }) {
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
        StudentEditDialog(
            title = "Add Student",
            existingRollNumbers = existingRolls,
            onDismiss = { showAddDialog = false },
            onConfirm = { roll, name ->
                viewModel.addStudent(roll, name)
                showAddDialog = false
                scope.launch { snackbarHostState.showSnackbar("Added $name") }
            }
        )
    }

    editingStudent?.let { student ->
        StudentEditDialog(
            initialRoll = student.rollNumber,
            initialName = student.name,
            title = "Edit Student",
            existingRollNumbers = existingRolls - student.rollNumber.trim().lowercase(),
            onDismiss = { editingStudent = null },
            onConfirm = { roll, name ->
                viewModel.updateStudent(student.copy(rollNumber = roll, name = name))
                editingStudent = null
                scope.launch { snackbarHostState.showSnackbar("Updated $name") }
            }
        )
    }

    deletingStudent?.let { student ->
        ConfirmDeleteDialog(
            itemLabel = student.name,
            onDismiss = { deletingStudent = null },
            onConfirm = {
                viewModel.deleteStudent(student)
                deletingStudent = null
                scope.launch { snackbarHostState.showSnackbar("Deleted ${student.name}") }
            }
        )
    }
}
