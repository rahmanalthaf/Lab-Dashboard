package com.labdashboard.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun StudentEditDialog(
    initialRoll: String = "",
    initialName: String = "",
    title: String,
    existingRollNumbers: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (rollNumber: String, name: String) -> Unit
) {
    var roll by remember { mutableStateOf(initialRoll) }
    var name by remember { mutableStateOf(initialName) }
    val isDuplicate = roll.isNotBlank() && existingRollNumbers.contains(roll.trim().lowercase())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = roll,
                    onValueChange = { roll = it },
                    label = { Text("Roll Number") },
                    singleLine = true,
                    isError = isDuplicate,
                    supportingText = { if (isDuplicate) Text("A student with this roll number already exists") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank() && roll.isNotBlank() && !isDuplicate) onConfirm(roll.trim(), name.trim()) },
                enabled = name.isNotBlank() && roll.isNotBlank() && !isDuplicate
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun ExperimentEditDialog(
    initialName: String = "",
    initialMax: String = "100",
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, maxMarks: Double) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var maxMarks by remember { mutableStateOf(initialMax) }
    val maxValid = maxMarks.toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Experiment Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = maxMarks,
                    onValueChange = { maxMarks = it },
                    label = { Text("Maximum Marks") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = !maxValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank() && maxValid) onConfirm(name, maxMarks.toDouble()) },
                enabled = name.isNotBlank() && maxValid
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun MarkEntryDialog(
    studentName: String,
    experimentName: String,
    maxMarks: Double,
    initialMarks: String,
    onDismiss: () -> Unit,
    onConfirm: (marksObtained: Double) -> Unit,
    onClear: () -> Unit
) {
    var marks by remember { mutableStateOf(initialMarks) }
    val parsed = marks.toDoubleOrNull()
    val valid = parsed != null && parsed in 0.0..maxMarks

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$studentName — $experimentName") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter marks out of $maxMarks")
                OutlinedTextField(
                    value = marks,
                    onValueChange = { marks = it },
                    label = { Text("Marks Obtained") },
                    singleLine = true,
                    isError = marks.isNotBlank() && !valid,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                if (marks.isNotBlank() && !valid) {
                    Text("Enter a value between 0 and $maxMarks", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (valid) onConfirm(parsed!!) }, enabled = valid) { Text("Save") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onClear) { Text("Clear") }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(itemLabel: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete $itemLabel?") },
        text = { Text("This will remove $itemLabel and any marks linked to it. This cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Delete", color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
