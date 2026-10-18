package com.educa.mathkids.domain.model

/**
 * Datos pedagógicos estructurados para el tutorial interactivo
 * previo a cada nivel de operación.
 */
data class OperationTutorialInfo(
    val operationType: OperationType,
    val levelNumber: Int,
    val levelTitle: String,
    val levelSubtitle: String,
    val conceptTitle: String,
    val conceptDescription: String,
    val spokenTutorial: String,
    val exampleLeft: Int,
    val exampleOperator: String,
    val exampleRight: Int,
    val exampleResult: Int,
    val exampleEmoji: String,
    val exampleExplanation: String
)

/**
 * Proveedor de explicaciones didácticas adaptadas para niños de 4 a 10 años,
 * con apoyo visual y texto de voz guiado por Mate.
 */
object OperationTutorialProvider {

    fun getTutorial(operationType: OperationType, levelNumber: Int): OperationTutorialInfo {
        val levelConfig = OperationLevelConfig.getDefaultLevels(operationType)
            .find { it.levelNumber == levelNumber }
            ?: OperationLevel(operationType, levelNumber, "Nivel $levelNumber", "Aventura de ${operationType.displayName}")

        return when (operationType) {
            OperationType.ADDITION -> {
                val left = when (levelNumber) {
                    1 -> 2
                    2 -> 3
                    3 -> 4
                    else -> 5
                }
                val right = when (levelNumber) {
                    1 -> 1
                    2 -> 2
                    3 -> 3
                    else -> 4
                }
                val result = left + right
                OperationTutorialInfo(
                    operationType = operationType,
                    levelNumber = levelNumber,
                    levelTitle = levelConfig.title,
                    levelSubtitle = levelConfig.subtitle,
                    conceptTitle = "¿Qué es Sumar?",
                    conceptDescription = "Sumar es juntar o agregar cosas para saber cuántas hay en total. ¡El total siempre crece!",
                    spokenTutorial = "¡Hola! Te doy la bienvenida al Nivel $levelNumber: ${levelConfig.title}. Sumar significa juntar o agregar cosas para tener más. Por ejemplo, si tienes $left ${if (left == 1) "manzana" else "manzanas"} y agregas $right, ¡ahora tienes $result manzanas! Cuenta con tus deditos o los dibujos de la pantalla. ¡Tú puedes, vamos a jugar!",
                    exampleLeft = left,
                    exampleOperator = "+",
                    exampleRight = right,
                    exampleResult = result,
                    exampleEmoji = "🍎",
                    exampleExplanation = "$left manzanas + $right ${if (right == 1) "manzana" else "manzanas"} = $result manzanas"
                )
            }
            OperationType.SUBTRACTION -> {
                val left = when (levelNumber) {
                    1 -> 3
                    2 -> 5
                    3 -> 6
                    else -> 7
                }
                val right = when (levelNumber) {
                    1 -> 1
                    2 -> 2
                    3 -> 2
                    else -> 3
                }
                val result = left - right
                OperationTutorialInfo(
                    operationType = operationType,
                    levelNumber = levelNumber,
                    levelTitle = levelConfig.title,
                    levelSubtitle = levelConfig.subtitle,
                    conceptTitle = "¿Qué es Restar?",
                    conceptDescription = "Restar es quitar, separar o tachar cosas para descubrir cuántas te quedan. ¡Descubrimos la diferencia!",
                    spokenTutorial = "¡Hola! Te doy la bienvenida al Nivel $levelNumber: ${levelConfig.title}. Restar significa quitar o separar cosas para saber cuántas te quedan. Por ejemplo, si tienes $left ${if (left == 1) "estrella" else "estrellas"} y quitas $right, ¡te quedan $result estrellas! Observa lo que queda después de quitar. ¡Vamos a descubrirlo juntos!",
                    exampleLeft = left,
                    exampleOperator = "−",
                    exampleRight = right,
                    exampleResult = result,
                    exampleEmoji = "⭐",
                    exampleExplanation = "$left estrellas − $right = $result estrellas"
                )
            }
            OperationType.MULTIPLICATION -> {
                val left = when (levelNumber) {
                    1 -> 2
                    2 -> 3
                    else -> 2
                }
                val right = when (levelNumber) {
                    1 -> 3
                    2 -> 3
                    else -> 4
                }
                val result = left * right
                OperationTutorialInfo(
                    operationType = operationType,
                    levelNumber = levelNumber,
                    levelTitle = levelConfig.title,
                    levelSubtitle = levelConfig.subtitle,
                    conceptTitle = "¿Qué es Multiplicar?",
                    conceptDescription = "Multiplicar es sumar grupos iguales varias veces rápidamente. ¡Es un súper poder matemático!",
                    spokenTutorial = "¡Hola! Te doy la bienvenida al Nivel $levelNumber: ${levelConfig.title}. Multiplicar es sumar grupos iguales rápidamente. Por ejemplo, $left grupos de $right cerezas son $result cerezas en total. Mira los grupos y encuentra el resultado. ¡Vamos a ganar muchas estrellas!",
                    exampleLeft = left,
                    exampleOperator = "×",
                    exampleRight = right,
                    exampleResult = result,
                    exampleEmoji = "🍒",
                    exampleExplanation = "$left grupos de $right cerezas = $result cerezas"
                )
            }
            OperationType.DIVISION -> {
                val left = when (levelNumber) {
                    1 -> 6
                    2 -> 8
                    else -> 6
                }
                val right = when (levelNumber) {
                    1 -> 2
                    2 -> 2
                    else -> 3
                }
                val result = left / right
                OperationTutorialInfo(
                    operationType = operationType,
                    levelNumber = levelNumber,
                    levelTitle = levelConfig.title,
                    levelSubtitle = levelConfig.subtitle,
                    conceptTitle = "¿Qué es Dividir?",
                    conceptDescription = "Dividir es repartir en partes exactamente iguales entre amigos o canastas para que todos tengan lo mismo.",
                    spokenTutorial = "¡Hola! Te doy la bienvenida al Nivel $levelNumber: ${levelConfig.title}. Dividir significa repartir en partes exactamente iguales para que todos tengan lo mismo. Por ejemplo, si tienes $left dulces y los repartes entre $right amigos, ¡a cada uno le tocan $result dulces! ¡Es muy fácil y divertido!",
                    exampleLeft = left,
                    exampleOperator = "÷",
                    exampleRight = right,
                    exampleResult = result,
                    exampleEmoji = "🍬",
                    exampleExplanation = "$left dulces repartidos entre $right amigos = $result para cada uno"
                )
            }
        }
    }
}
