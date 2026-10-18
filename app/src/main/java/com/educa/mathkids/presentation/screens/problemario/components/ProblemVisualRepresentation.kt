package com.educa.mathkids.presentation.screens.problemario.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.problemario.MathProblem
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Representación visual interactiva y pedagógica de cada problema matemático
 * adaptada especialmente para niños de 4 a 6 años.
 */
@Composable
fun ProblemVisualRepresentation(
    problem: MathProblem,
    showSolution: Boolean,
    modifier: Modifier = Modifier
) {
    val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceWhite)
            .border(2.dp, Color(problem.category.colorHex).copy(alpha = 0.35f), RoundedCornerShape(22.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (problem.category) {
                ProblemCategory.SUMA -> AdditionVisual(problem, showSolution, isEnglish)
                ProblemCategory.RESTA -> SubtractionVisual(problem, showSolution, isEnglish)
                ProblemCategory.MULTIPLICACION -> MultiplicationVisual(problem, showSolution, isEnglish)
                ProblemCategory.DIVISION -> DivisionVisual(problem, showSolution, isEnglish)
            }
        }
    }
}

/**
 * ➕ SUMA: Juntar cantidades.
 * Muestra el Grupo 1 + Grupo 2. Al resolver muestra la cantidad unida total.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdditionVisual(problem: MathProblem, showSolution: Boolean, isEnglish: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "addition_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Grupo 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(8.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 4,
                    modifier = Modifier.scale(pulse)
                ) {
                    // FIX: Limitar emojis para evitar overflow en pantalla
                    repeat(problem.num1.coerceAtMost(12)) {
                        Text(
                            text = problem.itemEmoji,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                    if (problem.num1 > 12) {
                        Text(
                            text = "+${problem.num1 - 12}",
                            fontSize = 16.sp, fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont, color = GreenPrimary,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${problem.num1}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = GreenPrimary
                )
            }

            // Símbolo +
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = GreenPrimary
                )
            }

            // Grupo 2
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(8.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 4,
                    modifier = Modifier.scale(pulse)
                ) {
                    repeat(problem.num2.coerceAtMost(12)) {
                        Text(
                            text = problem.itemEmoji,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                    if (problem.num2 > 12) {
                        Text(
                            text = "+${problem.num2 - 12}",
                            fontSize = 16.sp, fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont, color = GreenPrimary,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${problem.num2}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = GreenPrimary
                )
            }
        }

        if (showSolution) {
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFC8E6C9))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "⬇️ Together they make ${problem.correctAnswer}! ⬇️" else "⬇️ ¡Juntos son ${problem.correctAnswer}! ⬇️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        maxItemsInEachRow = 6
                    ) {
                        repeat(problem.correctAnswer) {
                            Text(text = problem.itemEmoji, fontSize = 26.sp, modifier = Modifier.padding(2.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * ➖ RESTA: Quitar cantidades.
 * Muestra el total inicial, tachando o atenuando visualmente los que se quitan.
 * FIX: remainingCount ahora está protegido con coerceAtLeast(0) para evitar
 * que repeat() reciba un valor negativo y cause un crash.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubtractionVisual(problem: MathProblem, showSolution: Boolean, isEnglish: Boolean) {
    // CRITICAL BUG FIX: num1 puede ser menor que num2 en datos incorrectos → crash
    val remainingCount = (problem.num1 - problem.num2).coerceAtLeast(0)
    // Limitar emojis visibles para evitar overflow en pantalla
    val maxVisibleEmojis = 15

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isEnglish) "There were ${problem.num1} and ${problem.num2} are taken away:" else "Había ${problem.num1} y se van ${problem.num2}:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FredokaFont,
            color = SlateDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elementos que quedan
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFF3E0))
                    .padding(8.dp)
            ) {
                if (remainingCount == 0 && showSolution) {
                    // Caso especial: no quedan elementos
                    Text(
                        text = "🚫",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(4.dp)
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center,
                        maxItemsInEachRow = 5
                    ) {
                        repeat(remainingCount.coerceAtMost(maxVisibleEmojis)) {
                            Text(
                                text = problem.itemEmoji,
                                fontSize = 32.sp,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                        if (remainingCount > maxVisibleEmojis) {
                            Text(
                                text = "+${remainingCount - maxVisibleEmojis}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = OrangeSecondary,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (showSolution) {
                        if (isEnglish) "Left: $remainingCount" else "Quedan: $remainingCount"
                    } else {
                        if (isEnglish) "Left: ❓" else "Quedan: ❓"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = OrangeSecondary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Elementos que se quitaron (con tachado/atenuación)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFEBEE))
                    .padding(8.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 3
                ) {
                    repeat(problem.num2.coerceAtMost(maxVisibleEmojis)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = problem.itemEmoji,
                                fontSize = 32.sp,
                                modifier = Modifier
                                    .alpha(0.35f)
                                    .padding(2.dp)
                            )
                            Text(
                                text = "❌",
                                fontSize = 16.sp
                            )
                        }
                    }
                    if (problem.num2 > maxVisibleEmojis) {
                        Text(
                            text = "+${problem.num2 - maxVisibleEmojis}❌",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SoftRed,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEnglish) "Taken: ${problem.num2}" else "Se van: ${problem.num2}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FredokaFont,
                    color = SoftRed
                )
            }
        }

        if (showSolution) {
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFE0B2))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "⬇️ Exactly $remainingCount remain! ⬇️" else "⬇️ ¡Quedan exactamente $remainingCount! ⬇️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
            }
        }
    }
}

/**
 * ✖️ MULTIPLICACIÓN: Formar grupos iguales.
 * Muestra contenedores (platos, cajas, etc.) con sus elementos adentro.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiplicationVisual(problem: MathProblem, showSolution: Boolean, isEnglish: Boolean) {
    val groups = problem.groupCount ?: problem.num1
    val itemsPer = problem.itemsPerGroup ?: problem.num2
    val itemName = problem.getItemName(isEnglish)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isEnglish) "$groups groups of $itemsPer $itemName" else "$groups grupos de $itemsPer $itemName",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FredokaFont,
            color = Color(problem.category.colorHex)
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = 3,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(groups.coerceAtMost(6)) { groupIndex ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3E5F5))
                        .border(1.5.dp, Color(0xFFAB47BC).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isEnglish) "Group ${groupIndex + 1}" else "Grupo ${groupIndex + 1}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E24AA)
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(itemsPer.coerceAtMost(6)) {
                                Text(
                                    text = problem.itemEmoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showSolution) {
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE1BEE7))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "⬇️ In total there are ${problem.correctAnswer} $itemName! ⬇️" else "⬇️ ¡En total son ${problem.correctAnswer} $itemName! ⬇️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
            }
        }
    }
}

/**
 * ➗ DIVISIÓN: Repartir en partes iguales.
 * Muestra los destinatarios (niños 👧👦 o platos 🍽️) con su porción asignada.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DivisionVisual(problem: MathProblem, showSolution: Boolean, isEnglish: Boolean) {
    val recipients = problem.recipientCount ?: problem.num2
    val itemsEach = problem.correctAnswer
    val itemName = problem.getItemName(isEnglish)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isEnglish) "Share ${problem.num1} $itemName among $recipients friends:" else "Repartimos ${problem.num1} $itemName entre $recipients amigos:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FredokaFont,
            color = SlateDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Si aún no se resuelve, mostrar el total a repartir arriba para que el niño pueda visualizarlo y calcular
        if (!showSolution) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF8E1))
                    .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEnglish) "Items to share: ${problem.num1}" else "Total a repartir: ${problem.num1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57F17)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        maxItemsInEachRow = 6
                    ) {
                        repeat(problem.num1.coerceAtMost(18)) {
                            Text(
                                text = problem.itemEmoji,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(1.dp)
                            )
                        }
                        if (problem.num1 > 18) {
                            Text(
                                text = "+${problem.num1 - 18}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF57F17),
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(recipients.coerceAtMost(5)) { index ->
                val recipientEmoji = problem.recipientEmojis.getOrElse(index) { "👧" }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE1F5FE))
                        .border(1.5.dp, Color(0xFF03A9F4).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    Text(text = recipientEmoji, fontSize = 34.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isEnglish) "Gets:" else "Recibe:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )
                    if (showSolution) {
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            maxItemsInEachRow = 3
                        ) {
                            repeat(itemsEach.coerceAtMost(9)) {
                                Text(
                                    text = problem.itemEmoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(1.dp)
                                )
                            }
                        }
                        Text(
                            text = "$itemsEach",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF0288D1)
                        )
                    } else {
                        // Antes de responder: incógnita didáctica sin revelar la respuesta
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "❓",
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color(0xFF0288D1)
                        )
                    }
                }
            }
        }

        if (showSolution) {
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedVisibility(visible = true, enter = fadeIn() + scaleIn()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFB3E5FC))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEnglish) "⬇️ Each friend gets $itemsEach $itemName! ⬇️" else "⬇️ ¡Cada amigo recibe $itemsEach $itemName! ⬇️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateDark
                    )
                }
            }
        }
    }
}
