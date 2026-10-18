package com.educa.mathkids.data.repository

import com.educa.mathkids.data.local.db.dao.GameSessionDao
import com.educa.mathkids.data.local.db.dao.OperationLevelDao
import com.educa.mathkids.data.local.db.dao.PlayerProfileDao
import com.educa.mathkids.data.local.db.dao.PlayerProgressDao
import com.educa.mathkids.data.local.db.dao.RewardDao
import com.educa.mathkids.data.local.db.dao.SessionHistoryDao
import com.educa.mathkids.data.local.db.entity.OperationLevelEntity
import com.educa.mathkids.data.local.db.entity.PlayerProfileEntity
import com.educa.mathkids.data.local.db.entity.PlayerProgressEntity
import com.educa.mathkids.data.local.db.entity.RewardEntity
import com.educa.mathkids.data.local.db.mapper.toDomain
import com.educa.mathkids.data.local.db.mapper.toEntity
import com.educa.mathkids.data.local.db.mapper.toGameSessionEntity
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.GameSession
import com.educa.mathkids.domain.model.OperationLevel
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.PlayerProgress
import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.model.RewardsConfig
import com.educa.mathkids.domain.model.ScoringConfig
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.domain.repository.SessionProgressResult
import com.educa.mathkids.domain.usecase.CheckRewardsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.math.max

class PlayerProgressRepositoryImpl(
    private val playerProgressDao: PlayerProgressDao,
    private val sessionHistoryDao: SessionHistoryDao,
    private val playerProfileDao: PlayerProfileDao,
    private val rewardDao: RewardDao,
    private val gameSessionDao: GameSessionDao? = null,
    private val operationLevelDao: OperationLevelDao? = null,
    private val checkRewardsUseCase: CheckRewardsUseCase = CheckRewardsUseCase()
) : PlayerProgressRepository {

    override fun getLevels(operationType: OperationType): Flow<List<OperationLevel>> {
        val defaultList = OperationLevelConfig.getDefaultLevels(operationType)
        if (operationLevelDao == null) {
            return flowOf(defaultList)
        }
        return operationLevelDao.getLevelsByOperation(operationType.name).map { entities ->
            if (entities.isEmpty()) {
                defaultList
            } else {
                val entityMap = entities.associateBy { it.levelNumber }
                defaultList.map { defaultLvl ->
                    val entity = entityMap[defaultLvl.levelNumber]
                    if (entity != null) {
                        defaultLvl.copy(
                            isUnlocked = entity.isUnlocked,
                            stars = entity.stars,
                            bestScore = entity.bestScore,
                            exercisesCompleted = entity.exercisesCompleted
                        )
                    } else {
                        defaultLvl
                    }
                }
            }
        }
    }

    override suspend fun recordLevelResult(
        operationType: OperationType,
        levelNumber: Int,
        stars: Int,
        score: Int,
        exercisesCount: Int
    ): Boolean {
        operationLevelDao?.recordLevelResult(
            operationType = operationType.name,
            levelNumber = levelNumber,
            stars = stars,
            score = score,
            exercisesCount = exercisesCount
        )
        var nextUnlocked = false
        if (stars >= 1 && levelNumber < OperationLevelConfig.MAX_LEVELS_PER_OPERATION) {
            val nextLevel = levelNumber + 1
            val existing = operationLevelDao?.getLevel(operationType.name, nextLevel)
            if (existing != null) {
                operationLevelDao?.unlockLevel(operationType.name, nextLevel)
            } else {
                operationLevelDao?.insertOrUpdate(
                    OperationLevelEntity(
                        operationType = operationType.name,
                        levelNumber = nextLevel,
                        isUnlocked = true,
                        stars = 0,
                        bestScore = 0,
                        exercisesCompleted = 0
                    )
                )
            }
            nextUnlocked = true
        }
        return nextUnlocked
    }

    override fun getProgress(operationType: OperationType): Flow<PlayerProgress> {
        return playerProgressDao.getProgressByOperation(operationType.name).map { entity ->
            entity?.toDomain() ?: PlayerProgress(operationType = operationType)
        }
    }

    override fun getAllProgress(): Flow<List<PlayerProgress>> {
        return playerProgressDao.getAllProgress().map { entities ->
            val entityMap = entities.associateBy { it.operationType }
            OperationType.entries.map { op ->
                entityMap[op.name]?.toDomain() ?: PlayerProgress(operationType = op)
            }
        }
    }

    override fun getPlayerProfile(): Flow<PlayerProgress> {
        return combine(
            playerProfileDao.getProfile(),
            rewardDao.getAllRewards()
        ) { profileEntity, rewardEntities ->
            val entity = profileEntity ?: PlayerProfileEntity()
            val rewards = rewardEntities.map { it.toDomain() }
            entity.toDomain(unlockedRewards = rewards)
        }
    }

    override fun getAllRewards(): Flow<List<Reward>> {
        return rewardDao.getAllRewards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveSession(session: GameSession): SessionProgressResult {
        // 1. Guardar la sesión histórica en SQLite
        sessionHistoryDao.insertSession(session.toEntity())
        gameSessionDao?.insertSession(session.toGameSessionEntity())

        // 2. Actualizar el progreso acumulado por operación
        val currentOpEntity = playerProgressDao.getProgressDirect(session.operationType.name)
        val currentOpTotalAnswered = currentOpEntity?.totalAnswered ?: 0
        val currentOpTotalCorrect = currentOpEntity?.totalCorrect ?: 0
        val currentOpBestStreak = currentOpEntity?.bestStreak ?: 0

        val newOpTotalAnswered = currentOpTotalAnswered + session.results.size
        val newOpTotalCorrect = currentOpTotalCorrect + session.correctCount
        val newOpBestStreak = max(currentOpBestStreak, session.bestStreak)

        val newOpLevel = when {
            newOpTotalCorrect >= 50 -> Difficulty.ADVANCED
            newOpTotalCorrect >= 30 -> Difficulty.INTERMEDIATE
            newOpTotalCorrect >= 15 -> Difficulty.BASIC
            else -> Difficulty.EASY
        }

        playerProgressDao.insertOrUpdate(
            PlayerProgressEntity(
                operationType = session.operationType.name,
                totalAnswered = newOpTotalAnswered,
                totalCorrect = newOpTotalCorrect,
                currentLevel = newOpLevel.name,
                bestStreak = newOpBestStreak,
                lastUpdated = System.currentTimeMillis()
            )
        )

        // 3. Actualizar el perfil global del jugador (Puntos totales, racha, nivel, etc.)
        val currentProfile = playerProfileDao.getProfileDirect() ?: PlayerProfileEntity()
        val newTotalPoints = currentProfile.totalPoints + session.sessionPoints
        val newCurrentStreak = session.currentStreak
        val newBestStreak = max(currentProfile.bestStreak, session.bestStreak)
        val newLevel = ScoringConfig.calculateLevel(newTotalPoints)
        val newSessionsCount = currentProfile.totalSessionsCompleted + 1

        val newAdditionSolved = currentProfile.additionSolved +
            if (session.operationType == OperationType.ADDITION) session.results.size else 0
        val newSubtractionSolved = currentProfile.subtractionSolved +
            if (session.operationType == OperationType.SUBTRACTION) session.results.size else 0
        val newMultiplicationSolved = currentProfile.multiplicationSolved +
            if (session.operationType == OperationType.MULTIPLICATION) session.results.size else 0
        val newDivisionSolved = currentProfile.divisionSolved +
            if (session.operationType == OperationType.DIVISION) session.results.size else 0

        val updatedProfileEntity = PlayerProfileEntity(
            playerId = currentProfile.playerId,
            totalPoints = newTotalPoints,
            currentStreak = newCurrentStreak,
            bestStreak = newBestStreak,
            currentLevel = newLevel,
            totalSessionsCompleted = newSessionsCount,
            additionSolved = newAdditionSolved,
            subtractionSolved = newSubtractionSolved,
            multiplicationSolved = newMultiplicationSolved,
            divisionSolved = newDivisionSolved,
            updatedAt = System.currentTimeMillis()
        )
        playerProfileDao.insertOrUpdate(updatedProfileEntity)

        // 4. Evaluar y desbloquear recompensas
        val existingRewards = rewardDao.getAllRewardsDirect().map { it.toDomain() }
        val newlyUnlocked = checkRewardsUseCase(
            existingRewards = existingRewards,
            totalPoints = newTotalPoints,
            bestStreak = newBestStreak,
            totalSessionsCompleted = newSessionsCount,
            additionSolved = newAdditionSolved,
            multiplicationSolved = newMultiplicationSolved,
            subtractionSolved = newSubtractionSolved,
            divisionSolved = newDivisionSolved
        )

        for (reward in newlyUnlocked) {
            rewardDao.unlockReward(reward.id, reward.unlockedAt ?: System.currentTimeMillis())
        }

        // Obtener recompensas completas actualizadas
        val allRewards = rewardDao.getAllRewardsDirect().map { it.toDomain() }
        val updatedProgress = updatedProfileEntity.toDomain(unlockedRewards = allRewards)

        return SessionProgressResult(
            updatedProgress = updatedProgress,
            newlyUnlockedRewards = newlyUnlocked
        )
    }

    override suspend fun addPoints(points: Int) {
        val current = playerProfileDao.getProfileDirect() ?: PlayerProfileEntity()
        playerProfileDao.insertOrUpdate(
            current.copy(
                totalPoints = current.totalPoints + points,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun updateStreak(currentStreak: Int, bestStreak: Int) {
        val current = playerProfileDao.getProfileDirect() ?: PlayerProfileEntity()
        val newBest = max(current.bestStreak, bestStreak)
        playerProfileDao.insertOrUpdate(
            current.copy(
                currentStreak = currentStreak,
                bestStreak = newBest,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun unlockReward(rewardId: String) {
        rewardDao.unlockReward(rewardId, System.currentTimeMillis())
    }

    override suspend fun resetAllProgress() {
        playerProgressDao.clearAll()
        sessionHistoryDao.clearAll()
        playerProfileDao.clear()
        rewardDao.clear()
        gameSessionDao?.clearAll()

        // Reinicializar operaciones
        val defaultOps = OperationType.entries.map { op ->
            PlayerProgressEntity(
                operationType = op.name,
                totalAnswered = 0,
                totalCorrect = 0,
                currentLevel = Difficulty.EASY.name,
                bestStreak = 0,
                lastUpdated = System.currentTimeMillis()
            )
        }
        playerProgressDao.insertAll(defaultOps)

        // Reinicializar perfil
        playerProfileDao.insertOrUpdate(PlayerProfileEntity())

        // Reinicializar catálogo de recompensas
        val initialRewards = RewardsConfig.INITIAL_REWARDS.map {
            RewardEntity(id = it.id, isUnlocked = false, unlockedAt = null)
        }
        rewardDao.insertInitialRewards(initialRewards)

        // Reinicializar los 15 niveles de cada operación
        operationLevelDao?.clearAll()
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
        operationLevelDao?.insertInitialLevels(initialLevels)
    }
}
