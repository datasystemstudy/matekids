package com.educa.mathkids.data.repository

import com.educa.mathkids.data.local.db.dao.GameSessionDao
import com.educa.mathkids.data.local.db.dao.PlayerProfileDao
import com.educa.mathkids.data.local.db.dao.PlayerProgressDao
import com.educa.mathkids.data.local.db.entity.PlayerProfileEntity
import com.educa.mathkids.data.local.db.mapper.toEntity
import com.educa.mathkids.data.local.db.mapper.toRecord
import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.model.OperationStats
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.ProgressStats
import com.educa.mathkids.domain.repository.ProgressRepository
import com.educa.mathkids.domain.usecase.CalculateAccuracyUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ProgressRepositoryImpl(
    private val playerProfileDao: PlayerProfileDao,
    private val playerProgressDao: PlayerProgressDao,
    private val gameSessionDao: GameSessionDao,
    private val calculateAccuracyUseCase: CalculateAccuracyUseCase = CalculateAccuracyUseCase()
) : ProgressRepository {

    override fun getProgressStats(): Flow<ProgressStats> {
        return combine(
            playerProfileDao.getProfile(),
            gameSessionDao.getAllSessions()
        ) { profileEntity, sessionEntities ->
            val profile = profileEntity ?: PlayerProfileEntity()
            val totalExercises = sessionEntities.sumOf { it.totalExercises }
            val correctAnswers = sessionEntities.sumOf { it.correctCount }
            val incorrectAnswers = sessionEntities.sumOf { it.incorrectCount }
            val overallAccuracy = calculateAccuracyUseCase(correctAnswers, totalExercises)
            val lastSessionDate = sessionEntities.firstOrNull()?.timestamp

            ProgressStats(
                totalPoints = profile.totalPoints,
                currentLevel = profile.currentLevel,
                currentStreak = profile.currentStreak,
                bestStreak = profile.bestStreak,
                totalSessions = sessionEntities.size,
                totalExercises = totalExercises,
                correctAnswers = correctAnswers,
                incorrectAnswers = incorrectAnswers,
                overallAccuracy = overallAccuracy,
                lastSessionDate = lastSessionDate
            )
        }
    }

    override fun getOperationStats(): Flow<List<OperationStats>> {
        return combine(
            playerProgressDao.getAllProgress(),
            gameSessionDao.getAllSessions()
        ) { progressEntities, sessionEntities ->
            val entityMap = progressEntities.associateBy { it.operationType }

            OperationType.entries.map { op ->
                val entity = entityMap[op.name]
                val opSessions = sessionEntities.filter { it.operationType == op.name }

                val exercises = if (entity != null && entity.totalAnswered > 0) {
                    entity.totalAnswered
                } else {
                    opSessions.sumOf { it.totalExercises }
                }

                val correct = if (entity != null && entity.totalCorrect > 0) {
                    entity.totalCorrect
                } else {
                    opSessions.sumOf { it.correctCount }
                }

                val incorrect = (exercises - correct).coerceAtLeast(0)
                val accuracy = calculateAccuracyUseCase(correct, exercises)
                val bestStreak = entity?.bestStreak ?: (opSessions.maxOfOrNull { it.maxStreak } ?: 0)

                OperationStats(
                    operationType = op,
                    exercisesCompleted = exercises,
                    correctAnswers = correct,
                    incorrectAnswers = incorrect,
                    accuracy = accuracy,
                    bestStreak = bestStreak
                )
            }
        }
    }

    override fun getRecentSessions(limit: Int): Flow<List<GameSessionRecord>> {
        return gameSessionDao.getRecentSessions(limit).map { entities ->
            entities.map { it.toRecord() }
        }
    }

    override suspend fun saveGameSession(session: GameSessionRecord) {
        gameSessionDao.insertSession(session.toEntity())
    }
}
