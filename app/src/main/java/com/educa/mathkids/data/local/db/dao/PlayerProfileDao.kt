package com.educa.mathkids.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educa.mathkids.data.local.db.entity.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {

    @Query("SELECT * FROM player_profile WHERE playerId = :playerId LIMIT 1")
    fun getProfile(playerId: String = "default_player"): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE playerId = :playerId LIMIT 1")
    suspend fun getProfileDirect(playerId: String = "default_player"): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: PlayerProfileEntity)

    @Query("DELETE FROM player_profile")
    suspend fun clear()
}
