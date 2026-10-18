package com.educa.mathkids.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.educa.mathkids.data.local.db.dao.GameSessionDao
import com.educa.mathkids.data.local.db.dao.OperationLevelDao
import com.educa.mathkids.data.local.db.dao.PlayerProfileDao
import com.educa.mathkids.data.local.db.dao.PlayerProgressDao
import com.educa.mathkids.data.local.db.dao.RewardDao
import com.educa.mathkids.data.local.db.dao.SessionHistoryDao
import com.educa.mathkids.data.local.db.entity.GameSessionEntity
import com.educa.mathkids.data.local.db.entity.OperationLevelEntity
import com.educa.mathkids.data.local.db.entity.PlayerProfileEntity
import com.educa.mathkids.data.local.db.entity.PlayerProgressEntity
import com.educa.mathkids.data.local.db.entity.RewardEntity
import com.educa.mathkids.data.local.db.entity.SessionHistoryEntity
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.RewardsConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlayerProgressEntity::class,
        SessionHistoryEntity::class,
        PlayerProfileEntity::class,
        RewardEntity::class,
        GameSessionEntity::class,
        OperationLevelEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class MathKidsDatabase : RoomDatabase() {

    abstract fun playerProgressDao(): PlayerProgressDao
    abstract fun sessionHistoryDao(): SessionHistoryDao
    abstract fun playerProfileDao(): PlayerProfileDao
    abstract fun rewardDao(): RewardDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun operationLevelDao(): OperationLevelDao

    companion object {
        @Volatile
        private var INSTANCE: MathKidsDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS player_profile (
                        playerId TEXT PRIMARY KEY NOT NULL,
                        totalPoints INTEGER NOT NULL,
                        currentStreak INTEGER NOT NULL,
                        bestStreak INTEGER NOT NULL,
                        currentLevel INTEGER NOT NULL,
                        totalSessionsCompleted INTEGER NOT NULL,
                        additionSolved INTEGER NOT NULL,
                        subtractionSolved INTEGER NOT NULL,
                        multiplicationSolved INTEGER NOT NULL,
                        divisionSolved INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS rewards (
                        id TEXT PRIMARY KEY NOT NULL,
                        isUnlocked INTEGER NOT NULL,
                        unlockedAt INTEGER
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS game_sessions (
                        sessionId TEXT PRIMARY KEY NOT NULL,
                        operationType TEXT NOT NULL,
                        difficulty TEXT NOT NULL,
                        totalExercises INTEGER NOT NULL,
                        correctCount INTEGER NOT NULL,
                        incorrectCount INTEGER NOT NULL,
                        accuracyPercentage INTEGER NOT NULL,
                        pointsEarned INTEGER NOT NULL,
                        maxStreak INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS operation_levels (
                        operationType TEXT NOT NULL,
                        levelNumber INTEGER NOT NULL,
                        isUnlocked INTEGER NOT NULL,
                        stars INTEGER NOT NULL,
                        bestScore INTEGER NOT NULL,
                        exercisesCompleted INTEGER NOT NULL,
                        lastUpdated INTEGER NOT NULL,
                        PRIMARY KEY(operationType, levelNumber)
                    )
                """.trimIndent())
                // Pre-poblar los 10 niveles para cada operación
                val now = System.currentTimeMillis()
                for (op in OperationType.entries) {
                    for (level in 1..10) {
                        val unlocked = if (level == 1) 1 else 0
                        db.execSQL("""
                            INSERT OR IGNORE INTO operation_levels (operationType, levelNumber, isUnlocked, stars, bestScore, exercisesCompleted, lastUpdated)
                            VALUES ('${op.name}', $level, $unlocked, 0, 0, 0, $now)
                        """.trimIndent())
                    }
                }
            }
        }

        val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                MIGRATION_1_2.migrate(db)
                MIGRATION_2_3.migrate(db)
            }
        }

        val MIGRATION_1_4 = object : Migration(1, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                MIGRATION_1_2.migrate(db)
                MIGRATION_2_3.migrate(db)
                MIGRATION_3_4.migrate(db)
            }
        }

        val MIGRATION_2_4 = object : Migration(2, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                MIGRATION_2_3.migrate(db)
                MIGRATION_3_4.migrate(db)
            }
        }

        fun getInstance(context: Context): MathKidsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MathKidsDatabase::class.java,
                    "mathkids_local.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_1_3, MIGRATION_1_4, MIGRATION_2_4)
                .fallbackToDestructiveMigrationOnDowngrade()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)

                            // 1. Pre-poblar las 4 operaciones al crear la base de datos local
                            val initialOps = OperationType.entries.map { op ->
                                PlayerProgressEntity(
                                    operationType = op.name,
                                    totalAnswered = 0,
                                    totalCorrect = 0,
                                    currentLevel = "EASY",
                                    bestStreak = 0
                                )
                            }
                            database.playerProgressDao().insertAll(initialOps)

                            // 2. Pre-poblar el perfil principal del jugador
                            database.playerProfileDao().insertOrUpdate(PlayerProfileEntity())

                            // 3. Pre-poblar el catálogo de recompensas
                            val initialRewards = RewardsConfig.INITIAL_REWARDS.map {
                                RewardEntity(id = it.id, isUnlocked = false, unlockedAt = null)
                            }
                            database.rewardDao().insertInitialRewards(initialRewards)

                            // 4. Pre-poblar los 50 niveles por cada operación (Nivel 1 desbloqueado)
                            val initialLevels = mutableListOf<OperationLevelEntity>()
                            for (op in OperationType.entries) {
                                for (lvl in 1..OperationLevelConfig.MAX_LEVELS_PER_OPERATION) {
                                    initialLevels.add(
                                        OperationLevelEntity(
                                            operationType = op.name,
                                            levelNumber = lvl,
                                            isUnlocked = (lvl == 1),
                                            stars = 0,
                                            bestScore = 0,
                                            exercisesCompleted = 0
                                        )
                                    )
                                }
                            }
                            database.operationLevelDao().insertInitialLevels(initialLevels)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
