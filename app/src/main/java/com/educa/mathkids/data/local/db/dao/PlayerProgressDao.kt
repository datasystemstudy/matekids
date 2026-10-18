package com.educa.mathkids.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educa.mathkids.data.local.db.entity.PlayerProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProgressDao {

    @Query("SELECT * FROM player_progress")
    fun getAllProgress(): Flow<List<PlayerProgressEntity>>

    @Query("SELECT * FROM player_progress WHERE operationType = :operationType LIMIT 1")
    fun getProgressByOperation(operationType: String): Flow<PlayerProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE operationType = :operationType LIMIT 1")
    suspend fun getProgressDirect(operationType: String): PlayerProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: PlayerProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PlayerProgressEntity>)

    @Query("DELETE FROM player_progress")
    suspend fun clearAll()
}
