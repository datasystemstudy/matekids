package com.educa.mathkids.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_progress")
data class PlayerProgressEntity(
    @PrimaryKey
    val operationType: String, // "ADDITION", "SUBTRACTION", "MULTIPLICATION", "DIVISION"
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val currentLevel: String = "EASY",
    val bestStreak: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
