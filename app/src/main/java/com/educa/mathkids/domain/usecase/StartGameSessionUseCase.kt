package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.EvaluationStage
import com.educa.mathkids.domain.model.GameSession
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.SessionConfig
import java.util.UUID

class StartGameSessionUseCase(
    private val generateExerciseUseCase: GenerateExerciseUseCase = GenerateExerciseUseCase()
) {
    operator fun invoke(
        operationType: OperationType,
        difficulty: Difficulty,
        totalExercises: Int = SessionConfig.DEFAULT_EXERCISE_COUNT
    ): GameSession {
        val level = when (difficulty) {
            Difficulty.EASY -> 1
            Difficulty.BASIC -> 12
            Difficulty.INTERMEDIATE -> 26
            Difficulty.ADVANCED -> 41
        }
        return invoke(operationType, level, totalExercises)
    }

    operator fun invoke(
        operationType: OperationType,
        level: Int,
        totalExercises: Int = SessionConfig.DEFAULT_EXERCISE_COUNT,
        strategy: DidacticStrategy? = null
    ): GameSession {
        val safeLevel = level.coerceIn(1, OperationLevelConfig.MAX_LEVELS_PER_OPERATION)
        val mappedDiff = when (safeLevel) {
            in 1..10 -> Difficulty.EASY
            in 11..25 -> Difficulty.BASIC
            in 26..40 -> Difficulty.INTERMEDIATE
            else -> Difficulty.ADVANCED
        }
        val initialStage = EvaluationStage.fromIndex(0, totalExercises)
        val firstExercise = if (strategy != null) {
            generateExerciseUseCase(operationType, strategy, mappedDiff)
        } else {
            generateExerciseUseCase.generateByStage(operationType, safeLevel, initialStage)
        }
        return GameSession(
            sessionId = UUID.randomUUID().toString(),
            operationType = operationType,
            difficulty = mappedDiff,
            levelNumber = safeLevel,
            totalExercises = totalExercises,
            currentExerciseIndex = 0,
            currentExercise = firstExercise,
            correctCount = 0,
            incorrectCount = 0,
            results = emptyList(),
            isCompleted = false
        )
    }
}
