package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.ScoringConfig

data class ScoreCalculationResult(
    val basePoints: Int,
    val streakBonus: Int,
    val totalPoints: Int,
    val streakBonusMessage: String?
)

class CalculateScoreUseCase {

    operator fun invoke(
        isCorrect: Boolean,
        currentStreak: Int,
        difficulty: Difficulty = Difficulty.EASY
    ): ScoreCalculationResult {
        if (!isCorrect) {
            return ScoreCalculationResult(
                basePoints = ScoringConfig.INCORRECT_ANSWER_POINTS,
                streakBonus = 0,
                totalPoints = 0,
                streakBonusMessage = null
            )
        }

        val basePoints = ScoringConfig.pointsForDifficulty(difficulty)
        val streakBonus = ScoringConfig.getStreakBonus(currentStreak)
        val total = basePoints + streakBonus
        val message = if (streakBonus > 0) {
            "🔥 ¡Racha de $currentStreak! +$streakBonus puntos"
        } else {
            null
        }

        return ScoreCalculationResult(
            basePoints = basePoints,
            streakBonus = streakBonus,
            totalPoints = total,
            streakBonusMessage = message
        )
    }

    // Sobrecarga de conveniencia para tests simples de racha
    operator fun invoke(newStreak: Int): Int {
        return invoke(isCorrect = true, currentStreak = newStreak).totalPoints
    }
}
