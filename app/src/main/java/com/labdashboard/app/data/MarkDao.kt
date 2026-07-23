package com.labdashboard.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkDao {
    @Query("SELECT * FROM marks")
    fun getAll(): Flow<List<Mark>>

    @Query("SELECT * FROM marks WHERE studentId = :studentId AND experimentId = :experimentId LIMIT 1")
    suspend fun getMark(studentId: Long, experimentId: Long): Mark?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mark: Mark): Long

    @Delete
    suspend fun delete(mark: Mark)

    @Query("DELETE FROM marks WHERE studentId = :studentId AND experimentId = :experimentId")
    suspend fun clearMark(studentId: Long, experimentId: Long)
}
