package com.educa.mathkids.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_history")
data class SessionHistoryEntity(
    @PrimaryKey
    val sessionId: String,
    val operationType: String,
    val difficulty: String,
    val score: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val maxStreak: Int,
    val timestamp: Long = System.currentTimeMillis()
)
