package com.educa.mathkids.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.RewardsConfig
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.domain.repository.ProgressRepository
import com.educa.mathkids.domain.repository.SettingsRepository
import com.educa.mathkids.domain.usecase.GetOperationStatsUseCase
import com.educa.mathkids.domain.usecase.GetProgressStatsUseCase
import com.educa.mathkids.domain.usecase.GetSessionHistoryUseCase
import com.educa.mathkids.domain.usecase.GetSettingsUseCase
import com.educa.mathkids.domain.usecase.UpdateSettingsUseCase
import com.educa.mathkids.presentation.screens.achievements.AchievementsScreen
import com.educa.mathkids.presentation.screens.calculator.KidsCalculatorScreen
import com.educa.mathkids.presentation.screens.challenge.ChallengeScreen
import com.educa.mathkids.presentation.screens.challenge.ChallengeViewModel
import com.educa.mathkids.presentation.screens.exercise.ExerciseScreen
import com.educa.mathkids.presentation.screens.exercise.ExerciseViewModel
import com.educa.mathkids.presentation.screens.home.HomeScreen
import com.educa.mathkids.presentation.screens.home.HomeViewModel
import com.educa.mathkids.presentation.screens.operation_levels.OperationLevelsScreen
import com.educa.mathkids.presentation.screens.operation_select.OperationSelectScreen
import com.educa.mathkids.presentation.screens.practice.PracticeConfigScreen
import com.educa.mathkids.presentation.screens.progress.ProgressScreen
import com.educa.mathkids.presentation.screens.progress.ProgressViewModel
import com.educa.mathkids.presentation.screens.result.ResultScreen
import com.educa.mathkids.presentation.screens.result.ResultViewModel
import com.educa.mathkids.presentation.screens.settings.SettingsScreen
import com.educa.mathkids.presentation.screens.settings.SettingsViewModel
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.screens.numbers.NumbersScreen
import com.educa.mathkids.presentation.screens.numbers.NumbersViewModel
import com.educa.mathkids.presentation.screens.splash.SplashScreen
import android.app.Activity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.educa.mathkids.presentation.ads.AdManager
import com.educa.mathkids.data.repository.ProblemarioRepositoryImpl
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty
import com.educa.mathkids.presentation.screens.problemario.ProblemarioHubScreen
import com.educa.mathkids.presentation.screens.problemario.ProblemarioPlayScreen
import com.educa.mathkids.presentation.screens.problemario.ProblemarioViewModel
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.launch

@Composable
fun MathKidsNavGraph(
    navController: NavHostController,
    playerProgressRepository: PlayerProgressRepository,
    progressRepository: ProgressRepository,
    settingsRepository: SettingsRepository,
    soundManager: SoundManager,
    ttsManager: TtsManager,
    hapticUtil: HapticUtil,
    adManager: AdManager? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity
    val getSettingsUseCase = GetSettingsUseCase(settingsRepository)
    val updateSettingsUseCase = UpdateSettingsUseCase(settingsRepository)
    val problemarioRepository = remember { ProblemarioRepositoryImpl() }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // 0. Pantalla de Bienvenida (Splash)
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                soundManager = soundManager,
                ttsManager = ttsManager
            )
        }

        // 1. Pantalla de Inicio (Home)
        composable(Screen.Home.route) {
            val homeViewModel = remember { HomeViewModel(playerProgressRepository) }
            HomeScreen(
                viewModel = homeViewModel,
                ttsManager = ttsManager,
                soundManager = soundManager,
                adManager = adManager,
                onRewardEarned = { amount ->
                    coroutineScope.launch {
                        playerProgressRepository.addPoints(amount)
                    }
                    soundManager.playReward()
                },
                onNavigateToNumbers = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Numbers.route)
                },
                onNavigateToOperationSelect = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.OperationSelect.route)
                },
                onNavigateToOperationLevels = { operationType ->
                    hapticUtil.vibrateClick()
                    soundManager.playOperationSelect(operationType)
                    navController.navigate(Screen.OperationLevels.createRoute(operationType))
                },
                onNavigateToPractice = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Practice.route)
                },
                onNavigateToChallenge = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Challenge.route)
                },
                onNavigateToAchievements = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Achievements.route)
                },
                onNavigateToProgress = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Progress.route)
                },
                onNavigateToSettings = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToProblemario = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.ProblemarioHub.route)
                },
                onNavigateToAdventure = { operationType, level ->
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.AdventurePlay.createRoute(operationType, level))
                },
                onNavigateToCalculator = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Calculator.route)
                },
                onNavigateToShop = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Shop.route)
                },
                onNavigateToMyToys = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.MyToys.route)
                },
                onNavigateToLearnCompare = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Numbers.createRoute("COMPARE"))
                }
            )
        }

        // 1.1 Módulo Explorando los Números (Sección 6)
        composable(
            route = Screen.Numbers.route,
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.StringType
                    defaultValue = "EXPLORE"
                }
            )
        ) { backStackEntry ->
            val tabArg = backStackEntry.arguments?.getString("tab") ?: "EXPLORE"
            val numbersViewModel = remember {
                NumbersViewModel(
                    playerProgressRepository = playerProgressRepository,
                    soundManager = soundManager,
                    ttsManager = ttsManager,
                    hapticUtil = hapticUtil
                )
            }
            LaunchedEffect(tabArg) {
                val targetTab = try {
                    com.educa.mathkids.domain.model.NumberGameType.valueOf(tabArg)
                } catch (_: Exception) {
                    com.educa.mathkids.domain.model.NumberGameType.EXPLORE
                }
                numbersViewModel.selectTab(targetTab)
            }
            NumbersScreen(
                viewModel = numbersViewModel,
                ttsManager = ttsManager,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                }
            )
        }

        // 2. Selección de Operación
        composable(Screen.OperationSelect.route) {
            OperationSelectScreen(
                onOperationSelected = { operationType ->
                    hapticUtil.vibrateClick()
                    soundManager.playOperationSelect(operationType)
                    navController.navigate(Screen.OperationLevels.createRoute(operationType))
                },
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                onInicioClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onJuegosClick = { /* Ya estamos en Juegos */ },
                onPremiosClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Achievements.route)
                },
                onAjustesClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Settings.route)
                },
                onProblemarioClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.ProblemarioHub.route)
                },
                ttsManager = ttsManager,
                soundManager = soundManager
            )
        }

        // 2.1 Mapa de 50 Niveles de la Operación
        composable(
            route = Screen.OperationLevels.route,
            arguments = listOf(
                navArgument("operationType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val opName = backStackEntry.arguments?.getString("operationType") ?: OperationType.ADDITION.name
            val operationType = try {
                OperationType.valueOf(opName)
            } catch (e: Exception) {
                OperationType.ADDITION
            }

            OperationLevelsScreen(
                operationType = operationType,
                playerProgressRepository = playerProgressRepository,
                onLevelSelected = { levelNumber ->
                    hapticUtil.vibrateClick()
                    soundManager.playOperationSelect(operationType)
                    navController.navigate(Screen.Exercise.createRoute(operationType, levelNumber))
                },
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                onInicioClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onJuegosClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.OperationSelect.route) {
                        popUpTo(Screen.OperationSelect.route) { inclusive = true }
                    }
                },
                onPremiosClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Achievements.route)
                },
                onAjustesClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // 3. Pantalla de Ejercicio (10 Niveles Progresivos)
        composable(
            route = Screen.Exercise.route,
            arguments = listOf(
                navArgument("operationType") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("strategy") {
                    type = NavType.StringType
                    defaultValue = "NONE"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val opName = backStackEntry.arguments?.getString("operationType") ?: OperationType.ADDITION.name
            val levelArg = backStackEntry.arguments?.getString("level") ?: "1"
            val strategyArg = backStackEntry.arguments?.getString("strategy")

            val operationType = try {
                OperationType.valueOf(opName)
            } catch (e: Exception) {
                OperationType.ADDITION
            }

            val levelNumber = levelArg.toIntOrNull() ?: 1

            val didacticStrategy = if (!strategyArg.isNullOrEmpty() && strategyArg != "NONE") {
                try {
                    DidacticStrategy.valueOf(strategyArg)
                } catch (e: Exception) {
                    null
                }
            } else null

            val exerciseContext = androidx.compose.ui.platform.LocalContext.current
            val exerciseViewModel = remember(backStackEntry) {
                ExerciseViewModel(
                    operationType = operationType,
                    difficulty = Difficulty.EASY,
                    levelNumber = levelNumber,
                    didacticStrategy = didacticStrategy,
                    playerProgressRepository = playerProgressRepository,
                    soundManager = soundManager,
                    hapticUtil = hapticUtil,
                    ttsManager = ttsManager,
                    context = exerciseContext
                )
            }

            ExerciseScreen(
                viewModel = exerciseViewModel,
                ttsManager = ttsManager,
                soundManager = soundManager,
                adManager = adManager,
                onSessionCompleted = { sessionResult ->
                    navController.navigate(Screen.Result.createRoute(sessionResult)) {
                        popUpTo(Screen.Exercise.route) { inclusive = true }
                    }
                },
                onInicioClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                onNivelesClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.OperationSelect.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        // 4. Modo Práctica Libre (Fase 16)
        composable(Screen.Practice.route) {
            PracticeConfigScreen(
                onStartPractice = { op, lvl, count, timer, strategy ->
                    hapticUtil.vibrateClick()
                    soundManager.playOperationSelect(op)
                    navController.navigate(Screen.Exercise.createRoute(op, lvl, strategy))
                },
                onOperationClick = { op ->
                    hapticUtil.vibrateClick()
                    soundManager.playOperationSelect(op)
                },
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                }
            )
        }

        // 5. Modo Desafío Contra Reloj (Fase 17)
        composable(Screen.Challenge.route) {
            val challengeViewModel = remember {
                ChallengeViewModel(
                    playerProgressRepository = playerProgressRepository,
                    soundManager = soundManager,
                    hapticUtil = hapticUtil,
                    problemarioRepository = problemarioRepository
                )
            }
            ChallengeScreen(
                viewModel = challengeViewModel,
                adManager = adManager,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                }
            )
        }

        // 6. Pantalla de Logros y Medallas (Fase 11)
        composable(Screen.Achievements.route) {
            AchievementsScreen(
                playerProgressRepository = playerProgressRepository,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                }
            )
        }

        // 7. Pantalla de Resultados de Sesión
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("operationType") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("totalExercises") { type = NavType.IntType },
                navArgument("correctCount") { type = NavType.IntType },
                navArgument("incorrectCount") { type = NavType.IntType },
                navArgument("accuracyPercentage") { type = NavType.IntType },
                navArgument("sessionPoints") { type = NavType.IntType },
                navArgument("totalPoints") { type = NavType.IntType },
                navArgument("maxStreak") { type = NavType.IntType },
                navArgument("currentLevel") { type = NavType.IntType },
                navArgument("unlockedRewardIds") { type = NavType.StringType },
                navArgument("levelNumber") { type = NavType.IntType },
                navArgument("nextLevelUnlocked") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val opName = backStackEntry.arguments?.getString("operationType") ?: OperationType.ADDITION.name
            val diffName = backStackEntry.arguments?.getString("difficulty") ?: Difficulty.EASY.name
            val total = backStackEntry.arguments?.getInt("totalExercises") ?: 10
            val correct = backStackEntry.arguments?.getInt("correctCount") ?: 0
            val incorrect = backStackEntry.arguments?.getInt("incorrectCount") ?: 0
            val accuracy = backStackEntry.arguments?.getInt("accuracyPercentage") ?: 0
            val sessionPoints = backStackEntry.arguments?.getInt("sessionPoints") ?: 0
            val totalPoints = backStackEntry.arguments?.getInt("totalPoints") ?: 0
            val maxStreak = backStackEntry.arguments?.getInt("maxStreak") ?: 0
            val currentLevel = backStackEntry.arguments?.getInt("currentLevel") ?: 1
            val rawRewardIds = backStackEntry.arguments?.getString("unlockedRewardIds") ?: "none"
            val levelNumber = backStackEntry.arguments?.getInt("levelNumber") ?: 1
            val nextLevelUnlocked = backStackEntry.arguments?.getBoolean("nextLevelUnlocked") ?: false

            val operationType = try {
                OperationType.valueOf(opName)
            } catch (e: Exception) {
                OperationType.ADDITION
            }

            val difficulty = try {
                Difficulty.valueOf(diffName)
            } catch (e: Exception) {
                Difficulty.EASY
            }

            val unlockedRewards = if (rawRewardIds == "none") {
                emptyList()
            } else {
                val ids = rawRewardIds.split(",")
                RewardsConfig.INITIAL_REWARDS.filter { ids.contains(it.id) }.map { it.copy(isUnlocked = true) }
            }

            val resultViewModel = remember(backStackEntry) {
                ResultViewModel(
                    operationType = operationType,
                    difficulty = difficulty,
                    totalExercises = total,
                    correctCount = correct,
                    incorrectCount = incorrect,
                    accuracyPercentage = accuracy,
                    sessionPoints = sessionPoints,
                    totalPoints = totalPoints,
                    maxStreak = maxStreak,
                    currentLevel = currentLevel,
                    unlockedRewards = unlockedRewards,
                    levelNumber = levelNumber,
                    nextLevelUnlocked = nextLevelUnlocked
                )
            }

            LaunchedEffect(Unit) {
                soundManager.playGoalComplete()
            }

            ResultScreen(
                viewModel = resultViewModel,
                adManager = adManager,
                onRewardEarned = { amount ->
                    coroutineScope.launch {
                        playerProgressRepository.addPoints(amount)
                    }
                    soundManager.playReward()
                },
                onPlayAgainClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playOperationSelect(operationType)
                    val navigateAction = {
                        try {
                            navController.navigate(Screen.Exercise.createRoute(operationType, levelNumber)) {
                                popUpTo(Screen.Result.route) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            navController.navigate(Screen.Exercise.createRoute(operationType, levelNumber))
                        }
                    }
                    if (activity != null && adManager != null) {
                        adManager.showInterstitial(activity, navigateAction)
                    } else {
                        navigateAction()
                    }
                },
                onNextLevelClick = if (nextLevelUnlocked && levelNumber < 50) {
                    {
                        hapticUtil.vibrateClick()
                        soundManager.playOperationSelect(operationType)
                        val navigateAction = {
                            try {
                                navController.navigate(Screen.Exercise.createRoute(operationType, levelNumber + 1)) {
                                    popUpTo(Screen.Result.route) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                navController.navigate(Screen.Exercise.createRoute(operationType, levelNumber + 1))
                            }
                        }
                        if (activity != null && adManager != null) {
                            adManager.showInterstitial(activity, navigateAction)
                        } else {
                            navigateAction()
                        }
                    }
                } else null,
                onHomeClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    val navigateAction = {
                        try {
                            val popped = navController.popBackStack(Screen.Home.route, false)
                            if (!popped) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            navController.navigate(Screen.Home.route)
                        }
                    }
                    if (activity != null && adManager != null) {
                        adManager.showInterstitial(activity, navigateAction)
                    } else {
                        navigateAction()
                    }
                }
            )
        }

        // 8. Progreso y Estadísticas
        composable(Screen.Progress.route) {
            val progressViewModel = remember {
                ProgressViewModel(
                    getProgressStatsUseCase = GetProgressStatsUseCase(progressRepository),
                    getOperationStatsUseCase = GetOperationStatsUseCase(progressRepository),
                    getSessionHistoryUseCase = GetSessionHistoryUseCase(progressRepository)
                )
            }
            ProgressScreen(
                viewModel = progressViewModel,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                onNavigateToPlay = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.OperationSelect.route)
                }
            )
        }

        // 9. Ajustes
        composable(Screen.Settings.route) {
            val settingsViewModel = remember {
                SettingsViewModel(
                    getSettingsUseCase = getSettingsUseCase,
                    updateSettingsUseCase = updateSettingsUseCase,
                    playerProgressRepository = playerProgressRepository
                )
            }
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                }
            )
        }

        // 10. Problemario Infantil (Hub de Categorías)
        composable(Screen.ProblemarioHub.route) {
            ProblemarioHubScreen(
                onCategorySelected = { category, difficulty ->
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.ProblemarioPlay.createRoute(category, difficulty))
                },
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                ttsManager = ttsManager,
                soundManager = soundManager,
                onInicioClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onJuegosClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.OperationSelect.route)
                },
                onPremiosClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Achievements.route)
                },
                onAjustesClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // 11. Problemario Interactivo (Resolución de Problemas Ilustrados)
        composable(
            route = Screen.ProblemarioPlay.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("difficulty") {
                    type = NavType.StringType
                    defaultValue = ProblemDifficulty.FACIL.name
                }
            )
        ) { backStackEntry ->
            val catArg = backStackEntry.arguments?.getString("category") ?: ProblemCategory.SUMA.name
            val diffArg = backStackEntry.arguments?.getString("difficulty") ?: ProblemDifficulty.FACIL.name

            val category = try {
                ProblemCategory.valueOf(catArg)
            } catch (e: Exception) {
                ProblemCategory.SUMA
            }

            val difficulty = try {
                ProblemDifficulty.valueOf(diffArg)
            } catch (e: Exception) {
                ProblemDifficulty.FACIL
            }

            val problemarioViewModel = remember(category, difficulty) {
                ProblemarioViewModel(
                    problemarioRepository = problemarioRepository,
                    playerProgressRepository = playerProgressRepository,
                    soundManager = soundManager,
                    ttsManager = ttsManager,
                    hapticUtil = hapticUtil
                )
            }

            ProblemarioPlayScreen(
                category = category,
                difficulty = difficulty,
                viewModel = problemarioViewModel,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                onInicioClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onJuegosClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.OperationSelect.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                ttsManager = ttsManager,
                soundManager = soundManager
            )
        }

        composable(
            route = Screen.AdventurePlay.route,
            arguments = listOf(
                navArgument("operationType") {
                    type = NavType.StringType
                    defaultValue = OperationType.ADDITION.name
                },
                navArgument("levelNumber") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val opName = backStackEntry.arguments?.getString("operationType") ?: OperationType.ADDITION.name
            val operationType = try {
                OperationType.valueOf(opName)
            } catch (e: Exception) {
                OperationType.ADDITION
            }
            val levelNumber = backStackEntry.arguments?.getInt("levelNumber") ?: 1

            val context = androidx.compose.ui.platform.LocalContext.current
            com.educa.mathkids.presentation.screens.adventure.AdventureScreen(
                operationType = operationType,
                levelNumber = levelNumber,
                soundManager = soundManager,
                hapticUtil = hapticUtil,
                adManager = adManager,
                onNavigateBack = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                onLevelCompleted = { level, stars, score ->
                    com.educa.mathkids.core.game.AdventureProgressManager.unlockNextLevel(context, operationType, level)
                    coroutineScope.launch {
                        playerProgressRepository.recordLevelResult(
                            operationType = operationType,
                            levelNumber = level,
                            stars = stars,
                            score = score,
                            exercisesCount = 5
                        )
                        playerProgressRepository.addPoints(score)
                    }
                }
            )
        }

        // 9. Calculadora Infantil Interactiva con Objetos Didácticos
        composable(Screen.Calculator.route) {
            KidsCalculatorScreen(
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                soundManager = soundManager,
                ttsManager = ttsManager,
                hapticUtil = hapticUtil
            )
        }

        // 10. Tienda de Regalos y Juguetes Canjeables
        composable(Screen.Shop.route) {
            com.educa.mathkids.presentation.screens.shop.ShopScreen(
                soundManager = soundManager,
                ttsManager = ttsManager,
                hapticUtil = hapticUtil,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    val popped = navController.popBackStack(Screen.Home.route, false)
                    if (!popped) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                }
            )
        }

        // 11. Baúl de Mis Juguetes Canjeados
        composable(Screen.MyToys.route) {
            com.educa.mathkids.presentation.screens.shop.MyToysScreen(
                soundManager = soundManager,
                ttsManager = ttsManager,
                hapticUtil = hapticUtil,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    val popped = navController.popBackStack(Screen.Home.route, false)
                    if (!popped) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                },
                onNavigateToShop = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Shop.route)
                },
                onNavigateToGames = { gameType, toyId ->
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.ToyGames.createRoute(gameType, toyId))
                }
            )
        }

        // 12. Sala de Juegos de Juguetes Canjeados
        composable(
            route = Screen.ToyGames.route,
            arguments = listOf(
                navArgument("initialGame") {
                    type = NavType.StringType
                    defaultValue = "HUB"
                },
                navArgument("toyId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val initialGame = backStackEntry.arguments?.getString("initialGame") ?: "HUB"
            val toyId = backStackEntry.arguments?.getString("toyId") ?: ""
            com.educa.mathkids.presentation.screens.shop.games.ToyGamesHubScreen(
                initialGame = initialGame,
                selectedToyId = toyId,
                soundManager = soundManager,
                ttsManager = ttsManager,
                hapticUtil = hapticUtil,
                onBackClick = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.popBackStack()
                },
                onNavigateToShop = {
                    hapticUtil.vibrateClick()
                    soundManager.playButtonClick()
                    navController.navigate(Screen.Shop.route)
                }
            )
        }
    }
}
