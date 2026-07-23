package com.labdashboard.app.data

/**
 * Seeds a couple of starter rows the first time the app runs on a
 * device (i.e. only if the tables are empty), purely so the UI isn't
 * blank on first launch. Everything seeded here is fully editable and
 * deletable afterward through the Students/Experiments screens - this
 * is not fixed or protected data in any way.
 */
suspend fun seedIfEmpty(database: AppDatabase) {
    val studentDao = database.studentDao()
    val experimentDao = database.experimentDao()

    if (studentDao.count() == 0) {
        studentDao.insert(Student(rollNumber = "R001", name = "Sample Student A"))
        studentDao.insert(Student(rollNumber = "R002", name = "Sample Student B"))
    }

    if (experimentDao.count() == 0) {
        experimentDao.insert(Experiment(name = "Experiment 1", maxMarks = 100.0))
        experimentDao.insert(Experiment(name = "Experiment 2", maxMarks = 100.0))
    }
}
