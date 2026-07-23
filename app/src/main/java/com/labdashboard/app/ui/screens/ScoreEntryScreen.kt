package com.labdashboard.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.labdashboard.app.ui.components.EmptyState
import com.labdashboard.app.ui.components.MarkEntryDialog
import com.labdashboard.app.viewmodel.LabViewModel

private data class SelectedCell(val studentId: Long, val studentName: String, val experimentId: Long, val experimentName: String, val maxMarks: Double)

/**
 * The interactive score-entry grid. Every cell is tappable and writes
 * straight to the Room database through the ViewModel - this replaces
 * the stubbed-out "score entry cards would go here" comment from the
 * original build.
 */
@Composable
fun ScoreEntryScreen(viewModel: LabViewModel) {
    val students by viewModel.students.collectAsState()
    val experiments by viewModel.experiments.collectAsState()
    val marks by viewModel.marks.collectAsState()

    var selectedCell by remember { mutableStateOf<SelectedCell?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text(
            "Tap any cell to enter or edit a student's marks for an experiment.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))

        if (students.isEmpty() || experiments.isEmpty()) {
            EmptyState("Add at least one student and one experiment first.")
        } else {
            val markLookup = remember(marks) {
                marks.associateBy { it.studentId to it.experimentId }
            }
            val cellWidth = 110.dp

            Column(Modifier.horizontalScroll(rememberScrollState())) {
                // Header row
                Row {
                    Box(Modifier.width(140.dp).padding(8.dp)) {
                        Text("Student", fontWeight = FontWeight.Bold)
                    }
                    experiments.forEach { exp ->
                        Box(Modifier.width(cellWidth).padding(8.dp)) {
                            Text(exp.name, fontWeight = FontWeight.Bold, maxLines = 2)
                        }
                    }
                }
                Divider()

                students.forEach { student ->
                    Row {
                        Box(Modifier.width(140.dp).padding(8.dp)) {
                            Text(student.name, maxLines = 2)
                        }
                        experiments.forEach { exp ->
                            val mark = markLookup[student.id to exp.id]
                            Box(
                                Modifier
                                    .width(cellWidth)
                                    .padding(4.dp)
                            ) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = {
                                        selectedCell = SelectedCell(
                                            studentId = student.id,
                                            studentName = student.name,
                                            experimentId = exp.id,
                                            experimentName = exp.name,
                                            maxMarks = exp.maxMarks
                                        )
                                    }
                                ) {
                                    Box(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                                        Text(
                                            text = mark?.marksObtained?.toString() ?: "—",
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }

    selectedCell?.let { cell ->
        val existing = marks.firstOrNull { it.studentId == cell.studentId && it.experimentId == cell.experimentId }
        MarkEntryDialog(
            studentName = cell.studentName,
            experimentName = cell.experimentName,
            maxMarks = cell.maxMarks,
            initialMarks = existing?.marksObtained?.toString() ?: "",
            onDismiss = { selectedCell = null },
            onConfirm = { value ->
                viewModel.setMark(cell.studentId, cell.experimentId, value)
                selectedCell = null
            },
            onClear = {
                viewModel.clearMark(cell.studentId, cell.experimentId)
                selectedCell = null
            }
        )
    }
}
