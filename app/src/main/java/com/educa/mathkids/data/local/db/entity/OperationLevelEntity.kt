package com.educa.mathkids.data.local.db.entity

import androidx.room.Entity

@Entity(
    tableName = "operation_levels",
    primaryKeys = ["operationType", "levelNumber"]
)
data class OperationLevelEntity(
    val operationType: String,
    val levelNumber: Int,
    val isUnlocked: Boolean = false,
    val stars: Int = 0,
    val bestScore: Int = 0,
    val exercisesCompleted: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
