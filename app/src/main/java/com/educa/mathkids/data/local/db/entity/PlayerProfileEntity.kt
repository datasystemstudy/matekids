package com.educa.mathkids.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey
    val playerId: String = "default_player",
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val currentLevel: Int = 1,
    val totalSessionsCompleted: Int = 0,
    val additionSolved: Int = 0,
    val subtractionSolved: Int = 0,
    val multiplicationSolved: Int = 0,
    val divisionSolved: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
