package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.EvaluationStage
import com.educa.mathkids.domain.model.GameSession

class NextExerciseUseCase(
    private val generateExerciseUseCase: GenerateExerciseUseCase = GenerateExerciseUseCase()
) {
    operator fun invoke(session: GameSession, strategy: DidacticStrategy? = null): GameSession {
        val nextIndex = session.currentExerciseIndex + 1
        return if (nextIndex < session.totalExercises) {
            val nextStage = EvaluationStage.fromIndex(nextIndex, session.totalExercises)
            val nextExercise = if (strategy != null) {
                generateExerciseUseCase(session.operationType, strategy, session.difficulty)
            } else {
                generateExerciseUseCase.generateByStage(session.operationType, session.levelNumber, nextStage)
            }
            session.copy(
                currentExerciseIndex = nextIndex,
                currentExercise = nextExercise
            )
        } else {
            session.copy(
                isCompleted = true,
                currentExercise = null
            )
        }
    }
}
