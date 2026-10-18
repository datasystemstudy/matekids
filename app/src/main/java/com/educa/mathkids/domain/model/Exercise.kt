package com.educa.mathkids.domain.model

data class Exercise(
    val id: String,
    val operationType: OperationType,
    val operand1: Int,
    val operand2: Int,
    val correctAnswer: Int,
    val options: List<Int>,
    val difficulty: Difficulty = Difficulty.EASY,
    val level: Int = 1,
    val questionText: String = "",
    val visualRepresentation: VisualRepresentation = VisualRepresentation(
        visualType = VisualType.OBJECT_COUNT,
        countA = operand1,
        countB = operand2
    ),
    val explanation: String = "",
    val isKeypadAllowed: Boolean = false,
    val timeLimitSeconds: Int? = null,
    val displayEquation: String? = null,
    val didacticStrategy: DidacticStrategy = DidacticStrategy.CONCRETE_OBJECTS,
    val missingTarget: MissingTarget = MissingTarget.RESULT,
    val evaluationStage: EvaluationStage? = null
) {
    val leftNumber: Int get() = operand1
    val rightNumber: Int get() = operand2
    val operation: OperationType get() = operationType
}
