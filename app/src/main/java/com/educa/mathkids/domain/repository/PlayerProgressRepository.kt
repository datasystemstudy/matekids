package com.educa.mathkids.domain.repository

import com.educa.mathkids.domain.model.GameSession
import com.educa.mathkids.domain.model.OperationLevel
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.PlayerProgress
import com.educa.mathkids.domain.model.Reward
import kotlinx.coroutines.flow.Flow

data class SessionProgressResult(
    val updatedProgress: PlayerProgress,
    val newlyUnlockedRewards: List<Reward>
)

interface PlayerProgressRepository {
    fun getProgress(operationType: OperationType): Flow<PlayerProgress>
    fun getAllProgress(): Flow<List<PlayerProgress>>
    fun getPlayerProfile(): Flow<PlayerProgress>
    fun getAllRewards(): Flow<List<Reward>>
    fun getLevels(operationType: OperationType): Flow<List<OperationLevel>>
    suspend fun recordLevelResult(
        operationType: OperationType,
        levelNumber: Int,
        stars: Int,
        score: Int,
        exercisesCount: Int
    ): Boolean
    suspend fun saveSession(session: GameSession): SessionProgressResult
    suspend fun addPoints(points: Int)
    suspend fun updateStreak(currentStreak: Int, bestStreak: Int = currentStreak)
    suspend fun unlockReward(rewardId: String)
    suspend fun resetAllProgress()
}
