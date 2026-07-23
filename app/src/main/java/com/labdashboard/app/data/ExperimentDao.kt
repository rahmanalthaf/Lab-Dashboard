package com.labdashboard.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperimentDao {
    @Query("SELECT * FROM experiments ORDER BY id ASC")
    fun getAll(): Flow<List<Experiment>>

    @Query("SELECT COUNT(*) FROM experiments")
    suspend fun count(): Int

    @Insert
    suspend fun insert(experiment: Experiment): Long

    @Update
    suspend fun update(experiment: Experiment)

    @Delete
    suspend fun delete(experiment: Experiment)
}
