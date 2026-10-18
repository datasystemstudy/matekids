package com.educa.mathkids.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey
    val id: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)
