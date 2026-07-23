package com.labdashboard.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.labdashboard.app.data.Experiment
import com.labdashboard.app.viewmodel.StudentPerformance
import java.io.File
import java.util.Locale

/**
 * Builds a CSV report (one row per student, one column per experiment,
 * plus total % and grade) and hands it to the system share sheet so it
 * can go to email, Drive, WhatsApp, etc. This is the export feature
 * flagged as missing in the previous build - implemented as CSV rather
 * than a binary .xlsx to avoid an extra Maven/POI dependency in CI.
 */
object CsvExporter {

    fun buildCsv(experiments: List<Experiment>, performances: List<StudentPerformance>): String {
        val header = buildString {
            append("Roll Number,Student Name")
            experiments.forEach { append(",${escapeCsv(it.name)} (/${it.maxMarks})") }
            append(",Total %,Grade")
        }

        val rows = performances.map { perf ->
            buildString {
                append("${escapeCsv(perf.student.rollNumber)},${escapeCsv(perf.student.name)}")
                experiments.forEach { exp ->
                    val obtained = perf.marksByExperiment[exp.id]
                    append(",${obtained?.toString() ?: ""}")
                }
                append(",${String.format(Locale.US, "%.1f", perf.percentage)},${perf.grade}")
            }
        }

        return (listOf(header) + rows).joinToString("\n")
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    /** Writes the CSV to cache/exports/ and launches the Android share sheet. */
    fun shareCsv(context: Context, experiments: List<Experiment>, performances: List<StudentPerformance>) {
        val csv = buildCsv(experiments, performances)

        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportsDir, "lab_dashboard_report.csv")
        file.writeText(csv)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share lab performance report"))
    }
}
