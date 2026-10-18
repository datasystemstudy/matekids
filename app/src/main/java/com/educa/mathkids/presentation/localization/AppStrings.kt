package com.educa.mathkids.presentation.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.educa.mathkids.domain.model.problemario.ProblemCategory

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String
) {
    ESPANOL("es", "Español", "🇪🇸"),
    INGLES("en", "English", "🇺🇸");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ESPANOL
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.ESPANOL }

@Composable
fun appStrings(): AppStrings {
    return when (LocalAppLanguage.current) {
        AppLanguage.INGLES -> EnglishStrings
        AppLanguage.ESPANOL -> SpanishStrings
    }
}

interface AppStrings {
    // General & Navegación
    val appName: String
    val welcomeHome: String
    val navHome: String
    val navBack: String
    val navGames: String
    val navLevels: String
    val navExit: String
    val navSettings: String
    val navRewards: String

    // Home
    val btnPlay: String
    val btnShop: String
    val btnMyToys: String
    val btnChallenge: String
    val btnProblemario: String
    val btnSettings: String
    val livesLabel: String
    val levelLabel: String
    val pointsLabel: String
    val shopTitle: String
    val shopSubtitle: String
    val myToysTitle: String
    val myToysSubtitle: String
    val noToysYetPrompt: String
    val playWithToyBtn: String
    val toyGamesTitle: String
    val toyMemoryTitle: String
    val toyCatcherTitle: String
    val toyRunnerTitle: String
    val coinsLabel: String
    val redeemBtn: String
    val redeemedBadge: String
    val needCoinsPrompt: String

    // Selección de Operación
    val operationSelectTitle: String
    val selectOptionToPracticePrompt: String
    val additionTitle: String
    val additionSubtitle: String
    val subtractionTitle: String
    val subtractionSubtitle: String
    val multiplicationTitle: String
    val multiplicationSubtitle: String
    val divisionTitle: String
    val divisionSubtitle: String
    val storyProblemsBanner: String
    val storyProblemsSubtitle: String

    // Ajustes y Herramientas
    val settingsTitle: String
    val languageSectionTitle: String
    val spanishLanguageName: String
    val englishLanguageName: String
    val soundsTitle: String
    val voiceTitle: String
    val musicTitle: String
    val soundEffectsTitle: String
    val hapticsTitle: String
    val animationsTitle: String
    val resetProgressBtn: String
    val resetDialogTitle: String
    val resetDialogMessage: String
    val resetDialogConfirm: String
    val resetDialogCancel: String

    // Problemario
    val problemarioTitle: String
    val problemarioWelcomeMsg: String
    val difficultyFacil: String
    val difficultyMedio: String
    val difficultyAvanzado: String
    val challengesBadge: String
    val btnPlayProblem: String
    val modeSelection: String
    val modeCompletion: String
    val modeWritten: String
    val promptSelection: String
    val promptCompletion: String
    val promptWritten: String
    val feedbackAwesome: String
    val feedbackAlmost: String
    val feedbackOutOfLives: String
    val btnNextProblem: String
    val btnTryAgain: String
    val btnRestoreLives: String
    val problemCompletedTitle: String
    fun problemCompletedMsg(category: String, stars: Int): String
    val btnChooseAnotherAdventure: String
    val exitProblemarioTitle: String
    val exitProblemarioMsg: String
    val exitConfirm: String
    val exitDismiss: String
    val exitGameDialogTitle: String
    val exitGameDialogMessage: String

    // Ejercicios y Desafío
    val challengeTitle: String
    val timeLabel: String
    val scoreLabel: String
    val howMuchIsQuestion: String
    val helpBtn: String
    val challengeModeSelection: String
    val challengeModeCompletion: String
    val challengeModeWritten: String
    val chooseLevelTitle: String
    val practicingLabel: String
    val listenBtn: String

    // Helpers
    fun getCategoryTitle(category: ProblemCategory): String
    fun getCategorySubtitle(category: ProblemCategory): String
    fun starEarnedFeedback(equationText: String): String
    fun theAnswerWasFeedback(correctAnswer: Int): String
}

object SpanishStrings : AppStrings {
    override val appName = "MateKids"
    override val welcomeHome = "Bienvenidos al mundo de las matemáticas"
    override val navHome = "INICIO"
    override val navBack = "ATRÁS"
    override val navGames = "EJERCICIOS"
    override val navLevels = "NIVELES"
    override val navExit = "SALIDA"
    override val navSettings = "AJUSTES"
    override val navRewards = "PREMIOS"

    override val btnPlay = "EJERCICIOS"
    override val btnShop = "TIENDA"
    override val btnMyToys = "MIS JUGUETES"
    override val btnChallenge = "DESAFÍO"
    override val btnProblemario = "PROBLEMARIO"
    override val btnSettings = "AJUSTES"
    override val livesLabel = "Vidas"
    override val levelLabel = "Nivel"
    override val pointsLabel = "Puntos"
    override val shopTitle = "TIENDA DE JUGUETES"
    override val shopSubtitle = "Canjea tus monedas ganadas por fantásticos regalos"
    override val myToysTitle = "MIS JUGUETES"
    override val myToysSubtitle = "Juguetes canjeados en la tienda"
    override val noToysYetPrompt = "¡Aún no has canjeado juguetes! Resuelve retos para ganar monedas y canjear tus favoritos."
    override val playWithToyBtn = "¡JUGAR!"
    override val toyGamesTitle = "JUEGOS CON MIS JUGUETES"
    override val toyMemoryTitle = "Memorama de Juguetes"
    override val toyCatcherTitle = "Atrapa el Juguete"
    override val toyRunnerTitle = "Carrera de Juguetes"
    override val coinsLabel = "Monedas"
    override val redeemBtn = "CANJEAR"
    override val redeemedBadge = "¡CANJEADO!"
    override val needCoinsPrompt = "¡Sigue resolviendo ejercicios para ganar más monedas!"

    override val operationSelectTitle = "¿Qué ejercicio quieres practicar?"
    override val selectOptionToPracticePrompt = "¿Qué ejercicio quieres practicar?"
    override val additionTitle = "SUMAR"
    override val additionSubtitle = "Aprende a juntar cantidades"
    override val subtractionTitle = "RESTAR"
    override val subtractionSubtitle = "Aprende a quitar cantidades"
    override val multiplicationTitle = "MULTIPLICAR"
    override val multiplicationSubtitle = "Aprende a formar grupos"
    override val divisionTitle = "DIVIDIR"
    override val divisionSubtitle = "Aprende a repartir"
    override val storyProblemsBanner = "📚 Problemario Infantil"
    override val storyProblemsSubtitle = "Resuelve problemas matemáticos con lindas historias y dibujos"

    override val settingsTitle = "Ajustes y Herramientas"
    override val languageSectionTitle = "Idioma de la aplicación"
    override val spanishLanguageName = "Español"
    override val englishLanguageName = "English"
    override val soundsTitle = "Sonidos generales"
    override val voiceTitle = "Voz de Mate (Lectura)"
    override val musicTitle = "Música de fondo"
    override val soundEffectsTitle = "Efectos sonoros"
    override val hapticsTitle = "Vibración al tocar"
    override val animationsTitle = "Animaciones"
    override val resetProgressBtn = "Restablecer Progreso 🔄"
    override val resetDialogTitle = "Restablecer Progreso"
    override val resetDialogMessage = "¿Seguro que deseas reiniciar todas tus estrellas y avances?"
    override val resetDialogConfirm = "Sí, reiniciar"
    override val resetDialogCancel = "Cancelar"

    override val problemarioTitle = "📚 Problemario"
    override val problemarioWelcomeMsg = "¡Bienvenido al Problemario! Elige una aventura para resolver lindas historias juntos."
    override val difficultyFacil = "Fácil"
    override val difficultyMedio = "Medio"
    override val difficultyAvanzado = "Avanzado"
    override val challengesBadge = "10 Retos • 🔘 🧩 ✏️"
    override val btnPlayProblem = "¡JUGAR! ▶"
    override val modeSelection = "Modo: Selección"
    override val modeCompletion = "Modo: Completación"
    override val modeWritten = "Modo: Completación Escrita"
    override val promptSelection = "Elige la respuesta correcta:"
    override val promptCompletion = "Toca el número para completar:"
    override val promptWritten = "Escribe el resultado y presiona ✓:"
    override val feedbackAwesome = "🎉 ¡MUY BIEN!"
    override val feedbackAlmost = "🙂 ¡Casi lo logras!"
    override val feedbackOutOfLives = "💔 ¡Sin vidas!"
    override val btnNextProblem = "¡Siguiente Problema! ➜"
    override val btnTryAgain = "Intentar de nuevo ↻"
    override val btnRestoreLives = "Restaurar 3 Vidas ❤️"
    override val problemCompletedTitle = "🏆 ¡EXCELENTE TRABAJO!"
    override fun problemCompletedMsg(category: String, stars: Int) =
        "Completaste las historias de $category.\n¡Ganaste $stars estrellas doradas! ⭐"
    override val btnChooseAnotherAdventure = "Elegir otra aventura 📚"
    override val exitProblemarioTitle = "¿Salir del Problemario?"
    override val exitProblemarioMsg = "¿Quieres regresar a la aventura principal? Tu progreso se guardará automáticamente."
    override val exitConfirm = "Sí, salir"
    override val exitDismiss = "Seguir jugando"
    override val exitGameDialogTitle = "¿Salir de la partida?"
    override val exitGameDialogMessage = "¿Seguro que quieres salir? Tu progreso se guardará automáticamente."

    override val challengeTitle = "Desafío Matemático"
    override val timeLabel = "Tiempo restante"
    override val scoreLabel = "Puntuación"
    override val howMuchIsQuestion = "¿Cuánto es?"
    override val helpBtn = "Ayuda"
    override val challengeModeSelection = "🎯 Modalidad: Selección de Opción"
    override val challengeModeCompletion = "🧩 Modalidad: Completar el Número Faltante"
    override val challengeModeWritten = "✍️ Modalidad: Escribir con Teclado"
    override val chooseLevelTitle = "Elige tu nivel"
    override val practicingLabel = "Practicando:"
    override val listenBtn = "Escuchar"

    override fun getCategoryTitle(category: ProblemCategory): String = when (category) {
        ProblemCategory.SUMA -> "SUMAR"
        ProblemCategory.RESTA -> "RESTAR"
        ProblemCategory.MULTIPLICACION -> "MULTIPLICAR"
        ProblemCategory.DIVISION -> "DIVIDIR"
    }

    override fun getCategorySubtitle(category: ProblemCategory): String = when (category) {
        ProblemCategory.SUMA -> "Aprende a juntar cantidades"
        ProblemCategory.RESTA -> "Aprende a quitar cantidades"
        ProblemCategory.MULTIPLICACION -> "Aprende a formar grupos"
        ProblemCategory.DIVISION -> "Aprende a repartir"
    }

    override fun starEarnedFeedback(equationText: String): String =
        "$equationText ⭐ +1 estrella"

    override fun theAnswerWasFeedback(correctAnswer: Int): String =
        "La respuesta era $correctAnswer."
}

object EnglishStrings : AppStrings {
    override val appName = "MateKids"
    override val welcomeHome = "Welcome to the world of math"
    override val navHome = "HOME"
    override val navBack = "BACK"
    override val navGames = "EXERCISES"
    override val navLevels = "LEVELS"
    override val navExit = "EXIT"
    override val navSettings = "SETTINGS"
    override val navRewards = "REWARDS"

    override val btnPlay = "EXERCISES"
    override val btnShop = "SHOP"
    override val btnMyToys = "MY TOYS"
    override val btnChallenge = "CHALLENGE"
    override val btnProblemario = "STORY PROBLEMS"
    override val btnSettings = "SETTINGS"
    override val livesLabel = "Lives"
    override val levelLabel = "Level"
    override val pointsLabel = "Points"
    override val shopTitle = "TOY SHOP"
    override val shopSubtitle = "Redeem your earned coins for awesome toys"
    override val myToysTitle = "MY TOYS"
    override val myToysSubtitle = "Toys redeemed from the shop"
    override val noToysYetPrompt = "No toys redeemed yet! Solve challenges to earn coins and redeem your favorites."
    override val playWithToyBtn = "PLAY!"
    override val toyGamesTitle = "PLAY WITH MY TOYS"
    override val toyMemoryTitle = "Toy Memory Match"
    override val toyCatcherTitle = "Catch the Toy"
    override val toyRunnerTitle = "Toy Runner"
    override val coinsLabel = "Coins"
    override val redeemBtn = "REDEEM"
    override val redeemedBadge = "REDEEMED!"
    override val needCoinsPrompt = "Keep solving exercises to earn more coins!"

    override val operationSelectTitle = "What exercise do you want to practice?"
    override val selectOptionToPracticePrompt = "What exercise do you want to practice?"
    override val additionTitle = "ADD"
    override val additionSubtitle = "Learn to put amounts together"
    override val subtractionTitle = "SUBTRACT"
    override val subtractionSubtitle = "Learn to take amounts away"
    override val multiplicationTitle = "MULTIPLY"
    override val multiplicationSubtitle = "Learn to make equal groups"
    override val divisionTitle = "DIVIDE"
    override val divisionSubtitle = "Learn to share equally"
    override val storyProblemsBanner = "📚 Story Math Problems"
    override val storyProblemsSubtitle = "Solve math problems with cute stories and pictures"

    override val settingsTitle = "Settings & Tools"
    override val languageSectionTitle = "App Language"
    override val spanishLanguageName = "Español"
    override val englishLanguageName = "English"
    override val soundsTitle = "General Sounds"
    override val voiceTitle = "Mate's Voice (Speech)"
    override val musicTitle = "Background Music"
    override val soundEffectsTitle = "Sound Effects"
    override val hapticsTitle = "Touch Vibration"
    override val animationsTitle = "Animations"
    override val resetProgressBtn = "Reset Progress 🔄"
    override val resetDialogTitle = "Reset Progress"
    override val resetDialogMessage = "Are you sure you want to reset all your stars and progress?"
    override val resetDialogConfirm = "Yes, reset"
    override val resetDialogCancel = "Cancel"

    override val problemarioTitle = "📚 Story Problems"
    override val problemarioWelcomeMsg = "Welcome to Story Problems! Choose an adventure to solve fun stories together."
    override val difficultyFacil = "Easy"
    override val difficultyMedio = "Medium"
    override val difficultyAvanzado = "Advanced"
    override val challengesBadge = "10 Challenges • 🔘 🧩 ✏️"
    override val btnPlayProblem = "PLAY! ▶"
    override val modeSelection = "Mode: Selection"
    override val modeCompletion = "Mode: Completion"
    override val modeWritten = "Mode: Written Completion"
    override val promptSelection = "Choose the correct answer:"
    override val promptCompletion = "Tap the number to complete:"
    override val promptWritten = "Type the answer and press ✓:"
    override val feedbackAwesome = "🎉 AWESOME!"
    override val feedbackAlmost = "🙂 Almost there!"
    override val feedbackOutOfLives = "💔 Out of lives!"
    override val btnNextProblem = "Next Problem! ➜"
    override val btnTryAgain = "Try again ↻"
    override val btnRestoreLives = "Restore 3 Lives ❤️"
    override val problemCompletedTitle = "🏆 GREAT JOB!"
    override fun problemCompletedMsg(category: String, stars: Int) =
        "You completed the $category stories.\nYou earned $stars gold stars! ⭐"
    override val btnChooseAnotherAdventure = "Choose another adventure 📚"
    override val exitProblemarioTitle = "Exit Story Problems?"
    override val exitProblemarioMsg = "Do you want to go back? Your progress will be saved automatically."
    override val exitConfirm = "Yes, exit"
    override val exitDismiss = "Keep playing"
    override val exitGameDialogTitle = "Leave game?"
    override val exitGameDialogMessage = "Are you sure you want to leave? Your progress will be saved automatically."

    override val challengeTitle = "Math Challenge"
    override val timeLabel = "Remaining time"
    override val scoreLabel = "Score"
    override val howMuchIsQuestion = "How much is it?"
    override val helpBtn = "Help"
    override val challengeModeSelection = "🎯 Mode: Multiple Choice"
    override val challengeModeCompletion = "🧩 Mode: Fill in Missing Number"
    override val challengeModeWritten = "✍️ Mode: Type on Keypad"
    override val chooseLevelTitle = "Choose your level"
    override val practicingLabel = "Practicing:"
    override val listenBtn = "Listen"

    override fun getCategoryTitle(category: ProblemCategory): String = when (category) {
        ProblemCategory.SUMA -> "ADDITION"
        ProblemCategory.RESTA -> "SUBTRACTION"
        ProblemCategory.MULTIPLICACION -> "MULTIPLICATION"
        ProblemCategory.DIVISION -> "DIVISION"
    }

    override fun getCategorySubtitle(category: ProblemCategory): String = when (category) {
        ProblemCategory.SUMA -> "Learn to put amounts together"
        ProblemCategory.RESTA -> "Learn to take amounts away"
        ProblemCategory.MULTIPLICACION -> "Learn to make equal groups"
        ProblemCategory.DIVISION -> "Learn to share equally"
    }

    override fun starEarnedFeedback(equationText: String): String =
        "$equationText ⭐ +1 star"

    override fun theAnswerWasFeedback(correctAnswer: Int): String =
        "The answer was $correctAnswer."
}
