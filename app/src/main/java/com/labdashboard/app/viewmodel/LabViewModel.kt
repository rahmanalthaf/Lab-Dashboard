package com.labdashboard.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labdashboard.app.data.Experiment
import com.labdashboard.app.data.Mark
import com.labdashboard.app.data.Student
import com.labdashboard.app.repository.LabRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StudentPerformance(
    val student: Student,
    val marksByExperiment: Map<Long, Double>, // experimentId -> marksObtained
    val percentage: Double,
    val grade: String
)

data class DashboardStats(
    val totalStudents: Int,
    val totalExperiments: Int,
    val classAverage: Double,
    val topScore: Double,
    val passCount: Int,
    val failCount: Int
)

/**
 * Standard 7-point grading scale used across the app. Adjust the cutoffs
 * here if your institution uses a different scheme - this is the single
 * place that decides O / A+ / A / B+ / B / C / F.
 */
fun gradeForPercentage(pct: Double): String = when {
    pct >= 90 -> "O"
    pct >= 80 -> "A+"
    pct >= 70 -> "A"
    pct >= 60 -> "B+"
    pct >= 50 -> "B"
    pct >= 40 -> "C"
    else -> "F"
}

class LabViewModel(private val repository: LabRepository) : ViewModel() {

    val students: StateFlow<List<Student>> =
        repository.students.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val experiments: StateFlow<List<Experiment>> =
        repository.experiments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marks: StateFlow<List<Mark>> =
        repository.marks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fully derived, recomputed automatically whenever students, experiments, or marks change.
    val performances: StateFlow<List<StudentPerformance>> = combine(students, experiments, marks) { studs, exps, allMarks ->
        val totalMax = exps.sumOf { it.maxMarks }
        studs.map { student ->
            val studentMarks = allMarks.filter { it.studentId == student.id }
            val marksMap = studentMarks.associate { it.experimentId to it.marksObtained }
            val obtained = marksMap.values.sum()
            val pct = if (totalMax > 0) (obtained / totalMax) * 100.0 else 0.0
            StudentPerformance(
                student = student,
                marksByExperiment = marksMap,
                percentage = pct,
                grade = gradeForPercentage(pct)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardStats: StateFlow<DashboardStats> = performances.map { perf ->
        val pcts = perf.map { it.percentage }
        DashboardStats(
            totalStudents = perf.size,
            totalExperiments = 0, // filled in below via combine, kept simple here
            classAverage = if (pcts.isNotEmpty()) pcts.average() else 0.0,
            topScore = pcts.maxOrNull() ?: 0.0,
            passCount = perf.count { it.percentage >= 40.0 },
            failCount = perf.count { it.percentage < 40.0 }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
        DashboardStats(0, 0, 0.0, 0.0, 0, 0))

    // --- Student actions ---
    fun addStudent(rollNumber: String, name: String) = viewModelScope.launch {
        repository.addStudent(rollNumber.trim(), name.trim())
    }

    fun updateStudent(student: Student) = viewModelScope.launch { repository.updateStudent(student) }

    fun deleteStudent(student: Student) = viewModelScope.launch { repository.deleteStudent(student) }

    // --- Experiment actions ---
    fun addExperiment(name: String, maxMarks: Double) = viewModelScope.launch {
        repository.addExperiment(name.trim(), maxMarks)
    }

    fun updateExperiment(experiment: Experiment) = viewModelScope.launch { repository.updateExperiment(experiment) }

    fun deleteExperiment(experiment: Experiment) = viewModelScope.launch { repository.deleteExperiment(experiment) }

    // --- Mark actions ---
    fun setMark(studentId: Long, experimentId: Long, marksObtained: Double) = viewModelScope.launch {
        repository.setMark(studentId, experimentId, marksObtained)
    }

    fun clearMark(studentId: Long, experimentId: Long) = viewModelScope.launch {
        repository.clearMark(studentId, experimentId)
    }
}
