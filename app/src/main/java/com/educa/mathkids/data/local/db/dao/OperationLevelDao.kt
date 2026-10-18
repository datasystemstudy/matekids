package com.educa.mathkids.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educa.mathkids.data.local.db.entity.OperationLevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperationLevelDao {

    @Query("SELECT * FROM operation_levels WHERE operationType = :operationType ORDER BY levelNumber ASC")
    fun getLevelsByOperation(operationType: String): Flow<List<OperationLevelEntity>>

    @Query("SELECT * FROM operation_levels WHERE operationType = :operationType ORDER BY levelNumber ASC")
    suspend fun getLevelsDirect(operationType: String): List<OperationLevelEntity>

    @Query("SELECT * FROM operation_levels WHERE operationType = :operationType AND levelNumber = :levelNumber LIMIT 1")
    suspend fun getLevel(operationType: String, levelNumber: Int): OperationLevelEntity?

    @Query("SELECT * FROM operation_levels")
    fun getAllLevels(): Flow<List<OperationLevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(level: OperationLevelEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialLevels(levels: List<OperationLevelEntity>)

    @Query("UPDATE operation_levels SET isUnlocked = 1 WHERE operationType = :operationType AND levelNumber = :levelNumber")
    suspend fun unlockLevel(operationType: String, levelNumber: Int)

    @Query("""
        UPDATE operation_levels 
        SET stars = CASE WHEN :stars > stars THEN :stars ELSE stars END,
            bestScore = CASE WHEN :score > bestScore THEN :score ELSE bestScore END,
            exercisesCompleted = exercisesCompleted + :exercisesCount,
            lastUpdated = :timestamp
        WHERE operationType = :operationType AND levelNumber = :levelNumber
    """)
    suspend fun recordLevelResult(
        operationType: String,
        levelNumber: Int,
        stars: Int,
        score: Int,
        exercisesCount: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM operation_levels")
    suspend fun clearAll()
}
