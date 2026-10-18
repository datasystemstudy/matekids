package com.educa.mathkids.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educa.mathkids.data.local.db.entity.SessionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionHistoryDao {

    @Query("SELECT * FROM session_history ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SessionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionHistoryEntity)

    @Query("DELETE FROM session_history")
    suspend fun clearAll()
}
