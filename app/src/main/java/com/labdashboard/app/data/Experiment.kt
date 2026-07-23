package com.labdashboard.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "experiments")
data class Experiment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val maxMarks: Double = 100.0
)
