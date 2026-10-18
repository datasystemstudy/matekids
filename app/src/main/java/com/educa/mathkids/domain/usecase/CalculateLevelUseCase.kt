package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.ScoringConfig

data class LevelInfo(
    val level: Int,
    val pointsInCurrentLevel: Int,
    val pointsNeededForNextLevel: Int,
    val progressFraction: Float
)

class CalculateLevelUseCase {
    operator fun invoke(totalPoints: Int): LevelInfo {
        val level = ScoringConfig.calculateLevel(totalPoints)
        val inLevel = ScoringConfig.pointsInCurrentLevel(totalPoints)
        val needed = ScoringConfig.POINTS_PER_LEVEL
        val fraction = ScoringConfig.levelProgressFraction(totalPoints)

        return LevelInfo(
            level = level,
            pointsInCurrentLevel = inLevel,
            pointsNeededForNextLevel = needed,
            progressFraction = fraction
        )
    }
}
