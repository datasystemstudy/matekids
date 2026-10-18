package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.engine.DefaultExerciseGenerator
import com.educa.mathkids.domain.engine.ExerciseGenerator
import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.EvaluationStage
import com.educa.mathkids.domain.model.Exercise
import com.educa.mathkids.domain.model.OperationType

class GenerateExerciseUseCase(
    private val generator: ExerciseGenerator = DefaultExerciseGenerator.shared
) {
    operator fun invoke(operation: OperationType, difficulty: Difficulty): Exercise {
        return generator.generate(operation, difficulty)
    }

    operator fun invoke(operation: OperationType, level: Int, includeCompletion: Boolean = true): Exercise {
        return generator.generateByLevel(operation, level, includeCompletion)
    }

    fun generateCompletion(operation: OperationType, level: Int): Exercise {
        return generator.generateCompletionExercise(operation, level)
    }

    fun generateByStage(operation: OperationType, level: Int, stage: EvaluationStage): Exercise {
        return generator.generateByStage(operation, level, stage)
    }

    operator fun invoke(operation: OperationType, strategy: DidacticStrategy, difficulty: Difficulty = Difficulty.EASY): Exercise {
        return generator.generateDidacticExercise(operation, strategy, difficulty)
    }
}
