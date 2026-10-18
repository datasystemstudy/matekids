package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.AnswerResult
import com.educa.mathkids.domain.model.GameSession
import kotlin.math.max

class SubmitAnswerUseCase(
    private val calculateScoreUseCase: CalculateScoreUseCase = CalculateScoreUseCase()
) {

    operator fun invoke(
        session: GameSession,
        selectedAnswer: Int
    ): Pair<GameSession, AnswerResult>? {
        val currentExercise = session.currentExercise ?: return null
        if (session.isCompleted) return null

        // Impedir respuestas múltiples para el mismo ejercicio (anti-exploit)
        if (session.results.size > session.currentExerciseIndex) {
            return null
        }

        val isCorrect = selectedAnswer == currentExercise.correctAnswer

        val newStreak = if (isCorrect) session.currentStreak + 1 else 0
        val newBestStreak = max(session.bestStreak, newStreak)

        val scoreResult = calculateScoreUseCase(
            isCorrect = isCorrect,
            currentStreak = newStreak,
            difficulty = session.difficulty
        )

        val answerResult = AnswerResult(
            exercise = currentExercise,
            selectedAnswer = selectedAnswer,
            isCorrect = isCorrect,
            pointsEarned = scoreResult.totalPoints
        )

        val updatedSession = session.copy(
            correctCount = if (isCorrect) session.correctCount + 1 else session.correctCount,
            incorrectCount = if (!isCorrect) session.incorrectCount + 1 else session.incorrectCount,
            results = session.results + answerResult,
            sessionPoints = session.sessionPoints + scoreResult.totalPoints,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            lastPointsEarned = scoreResult.totalPoints,
            lastStreakBonus = scoreResult.streakBonus,
            streakBonusMessage = scoreResult.streakBonusMessage
        )

        return Pair(updatedSession, answerResult)
    }
}
