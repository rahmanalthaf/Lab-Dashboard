package com.labdashboard.app.repository

import com.labdashboard.app.data.*
import kotlinx.coroutines.flow.Flow

class LabRepository(private val db: AppDatabase) {

    val students: Flow<List<Student>> = db.studentDao().getAll()
    val experiments: Flow<List<Experiment>> = db.experimentDao().getAll()
    val marks: Flow<List<Mark>> = db.markDao().getAll()

    suspend fun addStudent(rollNumber: String, name: String) {
        db.studentDao().insert(Student(rollNumber = rollNumber, name = name))
    }

    suspend fun updateStudent(student: Student) = db.studentDao().update(student)

    suspend fun deleteStudent(student: Student) = db.studentDao().delete(student)

    suspend fun addExperiment(name: String, maxMarks: Double) {
        db.experimentDao().insert(Experiment(name = name, maxMarks = maxMarks))
    }

    suspend fun updateExperiment(experiment: Experiment) = db.experimentDao().update(experiment)

    suspend fun deleteExperiment(experiment: Experiment) = db.experimentDao().delete(experiment)

    suspend fun setMark(studentId: Long, experimentId: Long, marksObtained: Double) {
        val existing = db.markDao().getMark(studentId, experimentId)
        db.markDao().upsert(Mark(id = existing?.id ?: 0, studentId = studentId, experimentId = experimentId, marksObtained = marksObtained))
    }

    suspend fun clearMark(studentId: Long, experimentId: Long) {
        db.markDao().clearMark(studentId, experimentId)
    }
}
