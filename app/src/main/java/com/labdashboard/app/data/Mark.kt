package com.labdashboard.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "marks",
    foreignKeys = [
        ForeignKey(entity = Student::class, parentColumns = ["id"], childColumns = ["studentId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Experiment::class, parentColumns = ["id"], childColumns = ["experimentId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("studentId"), Index("experimentId")]
)
data class Mark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val experimentId: Long,
    val marksObtained: Double
)
