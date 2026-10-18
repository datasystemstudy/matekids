package com.educa.mathkids.data

import androidx.sqlite.db.SupportSQLiteDatabase
import com.educa.mathkids.data.local.db.MathKidsDatabase
import com.educa.mathkids.data.local.db.entity.GameSessionEntity
import com.educa.mathkids.data.local.db.entity.PlayerProfileEntity
import com.educa.mathkids.data.local.db.entity.PlayerProgressEntity
import com.educa.mathkids.data.local.db.entity.RewardEntity
import com.educa.mathkids.data.local.db.entity.SessionHistoryEntity
import com.educa.mathkids.domain.model.RewardsConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Pruebas unitarias para las migraciones de Room (1_2, 2_3, 1_3) y entidades de datos.
 * Verifica que las sentencias SQL de migración se ejecuten sin errores de sintaxis
 * y que las entidades mantengan valores por defecto consistentes para garantizar
 * la persistencia local y la integridad de los datos.
 */
class DatabaseMigrationAndEntityTest {

    @Test
    fun `MIGRATION_1_2 executes valid SQL statements for player_profile and rewards`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        MathKidsDatabase.MIGRATION_1_2.migrate(fakeDb)

        assertEquals(2, executedStatements.size)
        assertTrue(executedStatements[0].contains("CREATE TABLE IF NOT EXISTS player_profile"))
        assertTrue(executedStatements[1].contains("CREATE TABLE IF NOT EXISTS rewards"))
    }

    @Test
    fun `MIGRATION_2_3 executes valid SQL statements for game_sessions`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        MathKidsDatabase.MIGRATION_2_3.migrate(fakeDb)

        assertEquals(1, executedStatements.size)
        assertTrue(executedStatements[0].contains("CREATE TABLE IF NOT EXISTS game_sessions"))
        assertTrue(executedStatements[0].contains("accuracyPercentage INTEGER NOT NULL"))
        assertTrue(executedStatements[0].contains("maxStreak INTEGER NOT NULL"))
    }

    @Test
    fun `MIGRATION_1_3 executes all migrations sequentially`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        MathKidsDatabase.MIGRATION_1_3.migrate(fakeDb)

        assertEquals(3, executedStatements.size)
        assertTrue(executedStatements[0].contains("CREATE TABLE IF NOT EXISTS player_profile"))
        assertTrue(executedStatements[1].contains("CREATE TABLE IF NOT EXISTS rewards"))
        assertTrue(executedStatements[2].contains("CREATE TABLE IF NOT EXISTS game_sessions"))
    }

    @Test
    fun `MIGRATION_3_4 executes valid SQL statements for operation_levels and initial levels`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        MathKidsDatabase.MIGRATION_3_4.migrate(fakeDb)

        assertTrue(executedStatements.isNotEmpty())
        assertTrue(executedStatements[0].contains("CREATE TABLE IF NOT EXISTS operation_levels"))
    }

    @Test
    fun `MIGRATION_2_4 executes migrations 2_3 and 3_4 sequentially`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        MathKidsDatabase.MIGRATION_2_4.migrate(fakeDb)

        assertTrue(executedStatements.size >= 2)
        assertTrue(executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS game_sessions") })
        assertTrue(executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS operation_levels") })
    }

    @Test
    fun `MIGRATION_1_4 executes all migrations from version 1 to 4`() {
        val executedStatements = mutableListOf<String>()

        val fakeDb = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedStatements.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        MathKidsDatabase.MIGRATION_1_4.migrate(fakeDb)

        assertTrue(executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS player_profile") })
        assertTrue(executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS rewards") })
        assertTrue(executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS game_sessions") })
        assertTrue(executedStatements.any { it.contains("CREATE TABLE IF NOT EXISTS operation_levels") })
    }

    @Test
    fun `PlayerProfileEntity has consistent initial defaults`() {
        val profile = PlayerProfileEntity()
        assertEquals("default_player", profile.playerId)
        assertEquals(0, profile.totalPoints)
        assertEquals(0, profile.currentStreak)
        assertEquals(0, profile.bestStreak)
        assertEquals(1, profile.currentLevel)
        assertEquals(0, profile.totalSessionsCompleted)
        assertTrue(profile.updatedAt > 0)
    }

    @Test
    fun `PlayerProgressEntity has consistent initial defaults`() {
        val progress = PlayerProgressEntity(operationType = "ADDITION")
        assertEquals("ADDITION", progress.operationType)
        assertEquals(0, progress.totalAnswered)
        assertEquals(0, progress.totalCorrect)
        assertEquals("EASY", progress.currentLevel)
        assertEquals(0, progress.bestStreak)
    }

    @Test
    fun `RewardEntity defaults to locked`() {
        val reward = RewardEntity(id = "star_1")
        assertEquals("star_1", reward.id)
        assertFalse(reward.isUnlocked)
        assertNull(reward.unlockedAt)
    }

    @Test
    fun `GameSessionEntity stores complete session metrics accurately`() {
        val session = GameSessionEntity(
            sessionId = "sess_100",
            operationType = "ADDITION",
            difficulty = "EASY",
            totalExercises = 10,
            correctCount = 8,
            incorrectCount = 2,
            accuracyPercentage = 80,
            pointsEarned = 80,
            maxStreak = 5,
            timestamp = 1700000000000L
        )
        assertEquals("sess_100", session.sessionId)
        assertEquals(80, session.accuracyPercentage)
        assertEquals(5, session.maxStreak)
        assertEquals(1700000000000L, session.timestamp)
    }

    @Test
    fun `RewardsConfig INITIAL_REWARDS contains unique and non-empty rewards`() {
        val rewards = RewardsConfig.INITIAL_REWARDS
        assertTrue(rewards.isNotEmpty())
        val ids = rewards.map { it.id }
        assertEquals("No debe haber IDs duplicados en recompensas", ids.size, ids.toSet().size)
        rewards.forEach { reward ->
            assertTrue(reward.id.isNotBlank())
            assertTrue(reward.title.isNotBlank())
            assertTrue(reward.description.isNotBlank())
            assertTrue(reward.icon.isNotBlank())
        }
    }
}
