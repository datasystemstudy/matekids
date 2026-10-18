package com.educa.mathkids.domain.model

data class AnswerResult(
    val exercise: Exercise,
    val selectedAnswer: Int,
    val isCorrect: Boolean,
    val pointsEarned: Int,
    val responseTimeMillis: Long = 0L
)
