package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.AnswerResult
import com.educa.mathkids.domain.model.Exercise

class ValidateAnswerUseCase(
    private val calculateScoreUseCase: CalculateScoreUseCase = CalculateScoreUseCase()
) {
    operator fun invoke(
        exercise: Exercise,
        selectedAnswer: Int,
        currentStreak: Int,
        responseTimeMillis: Long = 0L
    ): AnswerResult {
        val isCorrect = selectedAnswer == exercise.correctAnswer
        val points = if (isCorrect) {
            calculateScoreUseCase(currentStreak + 1)
        } else {
            0
        }

        return AnswerResult(
            exercise = exercise,
            selectedAnswer = selectedAnswer,
            isCorrect = isCorrect,
            pointsEarned = points,
            responseTimeMillis = responseTimeMillis
        )
    }
}
