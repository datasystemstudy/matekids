package com.educa.mathkids.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educa.mathkids.data.local.db.entity.RewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Query("SELECT * FROM rewards")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Query("SELECT * FROM rewards")
    suspend fun getAllRewardsDirect(): List<RewardEntity>

    @Query("SELECT * FROM rewards WHERE id = :id LIMIT 1")
    suspend fun getRewardById(id: String): RewardEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialRewards(rewards: List<RewardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(reward: RewardEntity)

    @Query("UPDATE rewards SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun unlockReward(id: String, unlockedAt: Long)

    @Query("DELETE FROM rewards")
    suspend fun clear()
}
