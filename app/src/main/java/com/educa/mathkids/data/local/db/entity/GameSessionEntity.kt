package com.educa.mathkids.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey
    val sessionId: String,
    val operationType: String,
    val difficulty: String,
    val totalExercises: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val accuracyPercentage: Int,
    val pointsEarned: Int,
    val maxStreak: Int,
    val timestamp: Long = System.currentTimeMillis()
)
