package com.educa.mathkids.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educa.mathkids.data.local.db.entity.GameSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {

    @Query("SELECT * FROM game_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<GameSessionEntity>>

    @Query("SELECT * FROM game_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 5): Flow<List<GameSessionEntity>>

    @Query("SELECT COUNT(*) FROM game_sessions")
    fun getSessionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM game_sessions")
    suspend fun getSessionCountDirect(): Int

    @Query("SELECT * FROM game_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): GameSessionEntity?

    @Query("SELECT * FROM game_sessions WHERE operationType = :operationType ORDER BY timestamp DESC")
    fun getSessionsByOperation(operationType: String): Flow<List<GameSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity)

    @Query("DELETE FROM game_sessions")
    suspend fun clearAll()
}
