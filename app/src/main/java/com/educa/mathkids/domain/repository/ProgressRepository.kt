package com.educa.mathkids.domain.repository

import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.model.OperationStats
import com.educa.mathkids.domain.model.ProgressStats
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getProgressStats(): Flow<ProgressStats>
    fun getOperationStats(): Flow<List<OperationStats>>
    fun getRecentSessions(limit: Int = 5): Flow<List<GameSessionRecord>>
    suspend fun saveGameSession(session: GameSessionRecord)
}
