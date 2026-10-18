package com.educa.mathkids.data.local.db.mapper

import com.educa.mathkids.data.local.db.entity.GameSessionEntity
import com.educa.mathkids.data.local.db.entity.PlayerProfileEntity
import com.educa.mathkids.data.local.db.entity.PlayerProgressEntity
import com.educa.mathkids.data.local.db.entity.RewardEntity
import com.educa.mathkids.data.local.db.entity.SessionHistoryEntity
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.GameSession
import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.PlayerProgress
import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.model.RewardsConfig

fun PlayerProgressEntity.toDomain(): PlayerProgress {
    val opType = try {
        OperationType.valueOf(operationType)
    } catch (e: Exception) {
        OperationType.ADDITION
    }
    val diff = try {
        Difficulty.valueOf(currentLevel)
    } catch (e: Exception) {
        Difficulty.EASY
    }

    return PlayerProgress(
        operationType = opType,
        totalAnswered = totalAnswered,
        totalCorrect = totalCorrect,
        currentLevel = diff,
        bestStreak = bestStreak
    )
}

fun PlayerProgress.toEntity(): PlayerProgressEntity {
    return PlayerProgressEntity(
        operationType = operationType.name,
        totalAnswered = totalAnswered,
        totalCorrect = totalCorrect,
        currentLevel = currentLevel.name,
        bestStreak = bestStreak,
        lastUpdated = System.currentTimeMillis()
    )
}

fun PlayerProfileEntity.toDomain(unlockedRewards: List<Reward> = emptyList()): PlayerProgress {
    return PlayerProgress(
        playerId = playerId,
        totalPoints = totalPoints,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        level = currentLevel,
        unlockedRewards = unlockedRewards,
        totalSessionsCompleted = totalSessionsCompleted,
        additionSolved = additionSolved,
        subtractionSolved = subtractionSolved,
        multiplicationSolved = multiplicationSolved,
        divisionSolved = divisionSolved
    )
}

fun PlayerProgress.toProfileEntity(): PlayerProfileEntity {
    return PlayerProfileEntity(
        playerId = playerId,
        totalPoints = totalPoints,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        currentLevel = level,
        totalSessionsCompleted = totalSessionsCompleted,
        additionSolved = additionSolved,
        subtractionSolved = subtractionSolved,
        multiplicationSolved = multiplicationSolved,
        divisionSolved = divisionSolved,
        updatedAt = System.currentTimeMillis()
    )
}

fun RewardEntity.toDomain(): Reward {
    val base = RewardsConfig.INITIAL_REWARDS.find { it.id == id }
    return Reward(
        id = id,
        title = base?.title ?: id,
        description = base?.description ?: "",
        icon = base?.icon ?: "⭐",
        isUnlocked = isUnlocked,
        unlockedAt = unlockedAt
    )
}

fun Reward.toEntity(): RewardEntity {
    return RewardEntity(
        id = id,
        isUnlocked = isUnlocked,
        unlockedAt = unlockedAt
    )
}

fun GameSession.toEntity(): SessionHistoryEntity {
    return SessionHistoryEntity(
        sessionId = sessionId,
        operationType = operationType.name,
        difficulty = difficulty.name,
        score = sessionPoints,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        maxStreak = bestStreak,
        timestamp = System.currentTimeMillis()
    )
}

fun GameSessionEntity.toRecord(): GameSessionRecord {
    val opType = try {
        OperationType.valueOf(operationType)
    } catch (e: Exception) {
        OperationType.ADDITION
    }
    val diff = try {
        Difficulty.valueOf(difficulty)
    } catch (e: Exception) {
        Difficulty.EASY
    }

    return GameSessionRecord(
        sessionId = sessionId,
        timestamp = timestamp,
        operationType = opType,
        difficulty = diff,
        totalExercises = totalExercises,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        accuracyPercentage = accuracyPercentage,
        pointsEarned = pointsEarned,
        maxStreak = maxStreak
    )
}

fun GameSessionRecord.toEntity(): GameSessionEntity {
    return GameSessionEntity(
        sessionId = sessionId,
        operationType = operationType.name,
        difficulty = difficulty.name,
        totalExercises = totalExercises,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        accuracyPercentage = accuracyPercentage,
        pointsEarned = pointsEarned,
        maxStreak = maxStreak,
        timestamp = timestamp
    )
}

fun GameSession.toRecord(): GameSessionRecord {
    return GameSessionRecord(
        sessionId = sessionId,
        timestamp = System.currentTimeMillis(),
        operationType = operationType,
        difficulty = difficulty,
        totalExercises = totalExercises,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        accuracyPercentage = accuracyPercentage,
        pointsEarned = sessionPoints,
        maxStreak = bestStreak
    )
}

fun GameSession.toGameSessionEntity(): GameSessionEntity {
    return GameSessionEntity(
        sessionId = sessionId,
        operationType = operationType.name,
        difficulty = difficulty.name,
        totalExercises = totalExercises,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        accuracyPercentage = accuracyPercentage,
        pointsEarned = sessionPoints,
        maxStreak = bestStreak,
        timestamp = System.currentTimeMillis()
    )
}
