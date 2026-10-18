package com.educa.mathkids.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Exercise
import com.educa.mathkids.domain.model.MissingTarget
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.BluePrimaryDark
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.OrangeSecondaryDark
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.TtsManager

/**
 * Sistema de pistas progresivas guiadas por Mate (Fase 13).
 *
 * Enseña el procedimiento de forma comprensiva, visual y paso a paso:
 * - Para sumas de 2 y 3 cifras: Tablero interactivo de columnas (U-D-C) con llevadas y descomposición posicional.
 * - Para restas de 2 y 3 cifras: Algoritmo vertical con préstamos y desagrupación.
 * - Para multiplicación y división: Descomposición distributiva y relación inversa.
 * - Para números pequeños: Conteo visual estructurado.
 */
@Composable
fun MetaProgressiveHintDialog(
    exercise: Exercise,
    accentColor: Color,
    ttsManager: TtsManager?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }

    val hintSet = remember(exercise) {
        generateProgressiveHints(exercise)
    }

    val currentHint = when (currentStep) {
        1 -> hintSet.step1
        2 -> hintSet.step2
        else -> hintSet.step3
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager?.stop()
        }
    }

    LaunchedEffect(currentStep) {
        ttsManager?.speak(currentHint.spokenText)
    }

    Dialog(onDismissRequest = {
        ttsManager?.stop()
        onDismiss()
    }) {
        val shape = RoundedCornerShape(26.dp)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .shadow(16.dp, shape)
                .clip(shape)
                .background(SurfaceWhite)
                .border(2.5.dp, OrangeSecondary.copy(alpha = 0.5f), shape)
                .padding(18.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Cabecera con Mate
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Mascot(
                        size = 52.dp,
                        mood = if (currentStep == 3) MascotMood.HAPPY else MascotMood.THINKING
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "💡 Ayuda de Mate",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeSecondary
                        )
                        Text(
                            text = "Paso $currentStep de 3",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Indicador visual de los 3 pasos
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..3) {
                        val active = i <= currentStep
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .weight(1f)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (active) OrangeSecondary else Color(0xFFE2E8F0))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Contenido animado del paso actual
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                    label = "hint_step_transition"
                ) { step ->
                    val hint = when (step) {
                        1 -> hintSet.step1
                        2 -> hintSet.step2
                        else -> hintSet.step3
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Título del paso
                        Text(
                            text = hint.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Visual pedagógico específico para el paso
                        HintVisualContent(
                            hint = hint,
                            step = step,
                            accentColor = accentColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Botón para escuchar con Mate
                        if (ttsManager != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { ttsManager.speak(hint.spokenText) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(text = "🔊", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Escuchar a Mate",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de avance y salida
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep < 3) {
                        AnimatedGameButton(
                            text = "Siguiente ayuda ➜",
                            onClick = { currentStep++ },
                            primaryColor = OrangeSecondary,
                            darkColor = OrangeSecondaryDark,
                            height = 46.dp,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AnimatedGameButton(
                        text = if (currentStep < 3) "¡Ya lo sé!" else "¡Entendido! 🚀",
                        onClick = onDismiss,
                        primaryColor = GreenPrimary,
                        darkColor = GreenPrimaryDark,
                        height = 46.dp,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Componentes Visuales del Diálogo de Ayuda
// -------------------------------------------------------------

@Composable
private fun HintVisualContent(
    hint: HintStep,
    step: Int,
    accentColor: Color
) {
    when (hint.visualType) {
        HintVisualType.VERTICAL_ADDITION -> {
            hint.columnAddition?.let { data ->
                VerticalAdditionBoard(data = data, activeStep = step, accentColor = accentColor)
            } ?: DefaultTextBox(hint.displayText, accentColor)
        }
        HintVisualType.VERTICAL_SUBTRACTION -> {
            hint.columnSubtraction?.let { data ->
                VerticalSubtractionBoard(data = data, activeStep = step, accentColor = accentColor)
            } ?: DefaultTextBox(hint.displayText, accentColor)
        }
        HintVisualType.DECOMPOSITION -> {
            DecompositionBoard(hint = hint, accentColor = accentColor)
        }
        HintVisualType.MULTIPLICATION_STEPS -> {
            MultiplicationStepsBoard(hint = hint, accentColor = accentColor)
        }
        HintVisualType.DIVISION_STEPS -> {
            DivisionStepsBoard(hint = hint, accentColor = accentColor)
        }
        HintVisualType.SIMPLE_COUNTING,
        HintVisualType.NONE -> {
            DefaultTextBox(hint.displayText, accentColor)
        }
    }
}

@Composable
private fun DefaultTextBox(text: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.5.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = if (text.length > 25) 16.sp else 20.sp,
            fontWeight = FontWeight.Black,
            color = SlateDark,
            textAlign = TextAlign.Center,
            lineHeight = 23.sp
        )
    }
}

/**
 * Tablero visual interactivo de suma por columnas con llevadas (Centenas, Decenas, Unidades).
 */
@Composable
private fun VerticalAdditionBoard(
    data: ColumnAdditionData,
    activeStep: Int,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFAFAFA))
            .border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        // Cabeceras de columnas: Centenas (C), Decenas (D), Unidades (U)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp)) // espacio del operador '+'
            if (data.isThreeDigits) {
                ColumnHeaderCell(label = "C", subtitle = "Centenas", color = Color(0xFF2E7D32), isActive = activeStep == 3)
                Spacer(modifier = Modifier.width(8.dp))
            }
            ColumnHeaderCell(label = "D", subtitle = "Decenas", color = Color(0xFF1565C0), isActive = activeStep == 2)
            Spacer(modifier = Modifier.width(8.dp))
            ColumnHeaderCell(label = "U", subtitle = "Unidades", color = Color(0xFFE65100), isActive = activeStep == 1)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Fila de Llevadas (Carries)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            if (data.isThreeDigits) {
                CarryBadgeCell(
                    carry = data.carryHundreds,
                    isVisible = activeStep >= 2 && data.carryHundreds > 0,
                    color = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            CarryBadgeCell(
                carry = data.carryTens,
                isVisible = activeStep >= 1 && data.carryTens > 0,
                color = Color(0xFF1565C0)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(width = 46.dp, height = 22.dp)) // columna U no tiene llevada entrante
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Fila 1: Primer operando
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            if (data.isThreeDigits) {
                DigitCell(digit = data.c1.toString(), isActive = activeStep == 3)
                Spacer(modifier = Modifier.width(8.dp))
            }
            DigitCell(digit = data.d1.toString(), isActive = activeStep == 2)
            Spacer(modifier = Modifier.width(8.dp))
            DigitCell(digit = data.u1.toString(), isActive = activeStep == 1)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Fila 2: Segundo operando con signo '+'
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "+",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = OrangeSecondary,
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center
            )
            if (data.isThreeDigits) {
                DigitCell(digit = data.c2.toString(), isActive = activeStep == 3)
                Spacer(modifier = Modifier.width(8.dp))
            }
            DigitCell(digit = data.d2.toString(), isActive = activeStep == 2)
            Spacer(modifier = Modifier.width(8.dp))
            DigitCell(digit = data.u2.toString(), isActive = activeStep == 1)
        }

        // Línea divisoria de la suma
        Box(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .width(if (data.isThreeDigits) 200.dp else 145.dp)
                .height(2.5.dp)
                .background(SlateDark)
        )

        // Fila 3: Resultados parciales o finales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            if (data.isThreeDigits) {
                val cText = if (activeStep >= 3) data.cAns.toString() else "?"
                ResultDigitCell(digit = cText, isResolved = activeStep >= 3, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(8.dp))
            }
            val dText = if (activeStep >= 2) data.dAns.toString() else "?"
            ResultDigitCell(digit = dText, isResolved = activeStep >= 2, color = Color(0xFF1565C0))
            Spacer(modifier = Modifier.width(8.dp))
            val uText = if (activeStep >= 1) data.uAns.toString() else "?"
            ResultDigitCell(digit = uText, isResolved = activeStep >= 1, color = Color(0xFFE65100))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tarjeta explicativa del paso en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when (activeStep) {
                        1 -> Color(0xFFFFF3E0)
                        2 -> Color(0xFFE3F2FD)
                        else -> Color(0xFFE8F5E9)
                    }
                )
                .border(
                    1.dp,
                    when (activeStep) {
                        1 -> Color(0xFFFFB74D)
                        2 -> Color(0xFF64B5F6)
                        else -> Color(0xFF81C784)
                    },
                    RoundedCornerShape(12.dp)
                )
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (activeStep) {
                    1 -> "🟠 Unidades: ${data.u1} + ${data.u2} = ${data.u1 + data.u2}${if (data.carryTens > 0) " (ponemos ${data.uAns} y llevamos ${data.carryTens})" else ""}"
                    2 -> "🔵 Decenas: ${data.d1} + ${data.d2}${if (data.carryTens > 0) " + 1" else ""} = ${data.dAns + data.carryHundreds * 10}${if (data.carryHundreds > 0) " (ponemos ${data.dAns} y llevamos ${data.carryHundreds})" else ""}"
                    else -> if (data.isThreeDigits) "🟢 Centenas: ${data.c1} + ${data.c2}${if (data.carryHundreds > 0) " + 1" else ""} = ${data.cAns}. ¡Total: ${data.ans}!" else "🏆 ¡Resultado completo: ${data.ans}!"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SlateDark,
                textAlign = TextAlign.Center
            )
        }

        // Tarjeta de Descomposición Singapur (Singapore Math)
        if (data.isThreeDigits) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${data.op1} = ${data.c1 * 100} + ${data.d1 * 10} + ${data.u1}\n${data.op2} = ${data.c2 * 100} + ${data.d2 * 10} + ${data.u2}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Tablero visual interactivo de resta por columnas con préstamos/desagrupación.
 */
@Composable
private fun VerticalSubtractionBoard(
    data: ColumnSubtractionData,
    activeStep: Int,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFAFAFA))
            .border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            if (data.isThreeDigits) {
                ColumnHeaderCell(label = "C", subtitle = "Centenas", color = Color(0xFF2E7D32), isActive = activeStep == 3)
                Spacer(modifier = Modifier.width(8.dp))
            }
            ColumnHeaderCell(label = "D", subtitle = "Decenas", color = Color(0xFF1565C0), isActive = activeStep == 2)
            Spacer(modifier = Modifier.width(8.dp))
            ColumnHeaderCell(label = "U", subtitle = "Unidades", color = Color(0xFFE65100), isActive = activeStep == 1)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Fila 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            if (data.isThreeDigits) {
                DigitCell(digit = data.c1.toString(), isActive = activeStep == 3)
                Spacer(modifier = Modifier.width(8.dp))
            }
            DigitCell(digit = data.d1.toString(), isActive = activeStep == 2)
            Spacer(modifier = Modifier.width(8.dp))
            DigitCell(digit = data.u1.toString(), isActive = activeStep == 1)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Fila 2 con signo '-'
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "−",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFE53935),
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center
            )
            if (data.isThreeDigits) {
                DigitCell(digit = data.c2.toString(), isActive = activeStep == 3)
                Spacer(modifier = Modifier.width(8.dp))
            }
            DigitCell(digit = data.d2.toString(), isActive = activeStep == 2)
            Spacer(modifier = Modifier.width(8.dp))
            DigitCell(digit = data.u2.toString(), isActive = activeStep == 1)
        }

        Box(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .width(if (data.isThreeDigits) 200.dp else 145.dp)
                .height(2.5.dp)
                .background(SlateDark)
        )

        // Fila 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            if (data.isThreeDigits) {
                val cText = if (activeStep >= 3) data.cAns.toString() else "?"
                ResultDigitCell(digit = cText, isResolved = activeStep >= 3, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(8.dp))
            }
            val dText = if (activeStep >= 2) data.dAns.toString() else "?"
            ResultDigitCell(digit = dText, isResolved = activeStep >= 2, color = Color(0xFF1565C0))
            Spacer(modifier = Modifier.width(8.dp))
            val uText = if (activeStep >= 1) data.uAns.toString() else "?"
            ResultDigitCell(digit = uText, isResolved = activeStep >= 1, color = Color(0xFFE65100))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3E0))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (activeStep) {
                    1 -> if (data.borrowedFromTens) "🟠 Como ${data.u1} < ${data.u2}, pedimos 1 decena prestada: ${data.u1 + 10} − ${data.u2} = ${data.uAns}" else "🟠 Unidades: ${data.u1} − ${data.u2} = ${data.uAns}"
                    2 -> "🔵 Decenas: ${data.d1 - (if (data.borrowedFromTens) 1 else 0)} − ${data.d2} = ${data.dAns}"
                    else -> "🏆 ¡Resultado completo: ${data.ans}!"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SlateDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ColumnHeaderCell(label: String, subtitle: String, color: Color, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(width = 46.dp, height = 36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) color.copy(alpha = 0.15f) else Color.Transparent)
            .border(if (isActive) 1.5.dp else 0.dp, color, RoundedCornerShape(8.dp))
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = subtitle, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = color.copy(alpha = 0.8f))
    }
}

@Composable
private fun DigitCell(digit: String, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) Color(0xFFFFF9C4) else Color.White)
            .border(1.dp, if (isActive) OrangeSecondary else Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SlateDark
        )
    }
}

@Composable
private fun ResultDigitCell(digit: String, isResolved: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isResolved) color.copy(alpha = 0.12f) else Color(0xFFEEEEEE))
            .border(1.5.dp, if (isResolved) color else Color.Transparent, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = if (isResolved) color else Color.Gray
        )
    }
}

@Composable
private fun CarryBadgeCell(carry: Int, isVisible: Boolean, color: Color) {
    Box(
        modifier = Modifier.size(width = 46.dp, height = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isVisible && carry > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "+$carry",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun DecompositionBoard(hint: HintStep, accentColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.5.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = hint.displayText,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SlateDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MultiplicationStepsBoard(hint: HintStep, accentColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.5.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = hint.displayText,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SlateDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DivisionStepsBoard(hint: HintStep, accentColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.5.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = hint.displayText,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SlateDark,
            textAlign = TextAlign.Center
        )
    }
}

// -------------------------------------------------------------
// Modelos de Datos para Pistas Progresivas
// -------------------------------------------------------------

enum class HintVisualType {
    NONE,
    VERTICAL_ADDITION,
    VERTICAL_SUBTRACTION,
    DECOMPOSITION,
    MULTIPLICATION_STEPS,
    DIVISION_STEPS,
    SIMPLE_COUNTING
}

data class ColumnAdditionData(
    val op1: Int,
    val op2: Int,
    val ans: Int,
    val u1: Int,
    val u2: Int,
    val uAns: Int,
    val carryTens: Int,
    val d1: Int,
    val d2: Int,
    val dAns: Int,
    val carryHundreds: Int,
    val c1: Int,
    val c2: Int,
    val cAns: Int,
    val isThreeDigits: Boolean
)

data class ColumnSubtractionData(
    val op1: Int,
    val op2: Int,
    val ans: Int,
    val u1: Int,
    val u2: Int,
    val uAns: Int,
    val borrowedFromTens: Boolean,
    val d1: Int,
    val d2: Int,
    val dAns: Int,
    val borrowedFromHundreds: Boolean,
    val c1: Int,
    val c2: Int,
    val cAns: Int,
    val isThreeDigits: Boolean
)

data class HintStep(
    val title: String,
    val displayText: String,
    val spokenText: String,
    val visualType: HintVisualType = HintVisualType.NONE,
    val columnAddition: ColumnAdditionData? = null,
    val columnSubtraction: ColumnSubtractionData? = null
)

data class ProgressiveHintSet(
    val step1: HintStep,
    val step2: HintStep,
    val step3: HintStep
)

// -------------------------------------------------------------
// Generador Inteligente y Comprensivo de Pistas
// -------------------------------------------------------------

private fun generateProgressiveHints(exercise: Exercise): ProgressiveHintSet {
    val a = exercise.operand1
    val b = exercise.operand2
    val ans = exercise.correctAnswer
    val isCompletion = exercise.missingTarget != MissingTarget.RESULT || exercise.didacticStrategy == DidacticStrategy.MISSING_NUMBER

    if (isCompletion) {
        return generateCompletionHints(exercise)
    }

    return when (exercise.operationType) {
        OperationType.ADDITION -> generateAdditionHints(a, b, ans)
        OperationType.SUBTRACTION -> generateSubtractionHints(a, b, ans)
        OperationType.MULTIPLICATION -> generateMultiplicationHints(a, b, ans)
        OperationType.DIVISION -> generateDivisionHints(a, b, ans)
    }
}

private fun generateAdditionHints(a: Int, b: Int, ans: Int): ProgressiveHintSet {
    val isMultiDigit = a >= 10 || b >= 10 || ans >= 10

    if (isMultiDigit) {
        // Cálculo del algoritmo vertical por columnas
        val u1 = a % 10
        val u2 = b % 10
        val uSum = u1 + u2
        val carryTens = uSum / 10
        val uAns = uSum % 10

        val d1 = (a / 10) % 10
        val d2 = (b / 10) % 10
        val dSum = d1 + d2 + carryTens
        val carryHundreds = dSum / 10
        val dAns = dSum % 10

        val isThreeDigits = a >= 100 || b >= 100 || ans >= 100
        val c1 = a / 100
        val c2 = b / 100
        val cAns = c1 + c2 + carryHundreds

        val data = ColumnAdditionData(
            op1 = a,
            op2 = b,
            ans = ans,
            u1 = u1,
            u2 = u2,
            uAns = uAns,
            carryTens = carryTens,
            d1 = d1,
            d2 = d2,
            dAns = dAns,
            carryHundreds = carryHundreds,
            c1 = c1,
            c2 = c2,
            cAns = cAns,
            isThreeDigits = isThreeDigits
        )

        val spoken1 = "Paso uno: Comenzamos siempre sumando las unidades de la derecha: $u1 más $u2 es $uSum." +
            if (carryTens > 0) " Escribimos $uAns y nos llevamos $carryTens decena arriba." else " Escribimos $uAns."

        val spoken2 = "Paso dos: Ahora sumamos las decenas: $d1 más $d2" +
            (if (carryTens > 0) " más la llevada" else "") +
            " es ${dSum}." +
            if (carryHundreds > 0) " Escribimos $dAns y llevamos $carryHundreds centena arriba." else " Escribimos $dAns."

        val spoken3 = if (isThreeDigits) {
            "Paso tres: Sumamos las centenas: $c1 más $c2" +
                (if (carryHundreds > 0) " más uno de la llevada" else "") +
                " es $cAns. ¡El resultado final es $ans!"
        } else {
            "Paso tres: ¡Excelente trabajo! El resultado final de $a más $b es $ans."
        }

        return ProgressiveHintSet(
            step1 = HintStep(
                title = "Paso 1: Sumamos las Unidades (U) 🟠",
                displayText = "Comenzamos por las unidades: $u1 + $u2 = $uSum",
                spokenText = spoken1,
                visualType = HintVisualType.VERTICAL_ADDITION,
                columnAddition = data
            ),
            step2 = HintStep(
                title = "Paso 2: Sumamos las Decenas (D) 🔵",
                displayText = "Sumamos decenas: $d1 + $d2${if (carryTens > 0) " + 1" else ""} = $dSum",
                spokenText = spoken2,
                visualType = HintVisualType.VERTICAL_ADDITION,
                columnAddition = data
            ),
            step3 = HintStep(
                title = if (isThreeDigits) "Paso 3: Sumamos las Centenas (C) 🟢" else "Paso 3: ¡Resultado Final! 🏆",
                displayText = if (isThreeDigits) "Centenas: $c1 + $c2${if (carryHundreds > 0) " + 1" else ""} = $cAns. ¡Total: $ans!" else "$a + $b = $ans",
                spokenText = spoken3,
                visualType = HintVisualType.VERTICAL_ADDITION,
                columnAddition = data
            )
        )
    } else {
        // Números pequeños (<= 10): Conteo estructurado y directo
        val steps = (1..b).map { a + it }
        val countingStr = "$a ➜ " + steps.joinToString(" ➜ ")

        return ProgressiveHintSet(
            step1 = HintStep(
                title = "Paso 1: Punto de partida",
                displayText = "💡 Comienza en el número $a y agrega $b pasos.",
                spokenText = "Ayuda: comienza en el número $a y cuenta $b números más.",
                visualType = HintVisualType.SIMPLE_COUNTING
            ),
            step2 = HintStep(
                title = "Paso 2: Conteo paso a paso",
                displayText = countingStr,
                spokenText = "Vamos contando: " + steps.joinToString(", ") { "$it" },
                visualType = HintVisualType.SIMPLE_COUNTING
            ),
            step3 = HintStep(
                title = "Paso 3: ¡Resultado final!",
                displayText = "$a + $b = $ans",
                spokenText = "¡Excelente! $a más $b es igual a $ans.",
                visualType = HintVisualType.SIMPLE_COUNTING
            )
        )
    }
}

private fun generateSubtractionHints(a: Int, b: Int, ans: Int): ProgressiveHintSet {
    val isMultiDigit = a >= 10 || b >= 10

    if (isMultiDigit) {
        val u1 = a % 10
        val u2 = b % 10
        val borrowedFromTens = u1 < u2
        val effectiveU1 = if (borrowedFromTens) u1 + 10 else u1
        val uAns = effectiveU1 - u2

        val rawD1 = (a / 10) % 10
        val d1 = if (borrowedFromTens) rawD1 - 1 else rawD1
        val d2 = (b / 10) % 10
        val borrowedFromHundreds = d1 < d2
        val effectiveD1 = if (borrowedFromHundreds) d1 + 10 else d1
        val dAns = effectiveD1 - d2

        val isThreeDigits = a >= 100 || b >= 100
        val rawC1 = a / 100
        val c1 = if (borrowedFromHundreds) rawC1 - 1 else rawC1
        val c2 = b / 100
        val cAns = (c1 - c2).coerceAtLeast(0)

        val data = ColumnSubtractionData(
            op1 = a,
            op2 = b,
            ans = ans,
            u1 = u1,
            u2 = u2,
            uAns = uAns,
            borrowedFromTens = borrowedFromTens,
            d1 = rawD1,
            d2 = d2,
            dAns = dAns,
            borrowedFromHundreds = borrowedFromHundreds,
            c1 = rawC1,
            c2 = c2,
            cAns = cAns,
            isThreeDigits = isThreeDigits
        )

        return ProgressiveHintSet(
            step1 = HintStep(
                title = "Paso 1: Restamos las Unidades (U) 🟠",
                displayText = if (borrowedFromTens) "Como $u1 < $u2, pedimos prestada 1 decena: $effectiveU1 − $u2 = $uAns" else "$u1 − $u2 = $uAns",
                spokenText = if (borrowedFromTens) "Paso uno: A $u1 no le podemos quitar $u2, así que pedimos prestada una decena. Ahora $effectiveU1 menos $u2 es $uAns." else "Paso uno: Restamos las unidades: $u1 menos $u2 es $uAns.",
                visualType = HintVisualType.VERTICAL_SUBTRACTION,
                columnSubtraction = data
            ),
            step2 = HintStep(
                title = "Paso 2: Restamos las Decenas (D) 🔵",
                displayText = "Restamos decenas: $d1 − $d2 = $dAns",
                spokenText = "Paso dos: Ahora restamos las decenas: $d1 menos $d2 es $dAns.",
                visualType = HintVisualType.VERTICAL_SUBTRACTION,
                columnSubtraction = data
            ),
            step3 = HintStep(
                title = "Paso 3: ¡Resultado final! 🏆",
                displayText = "$a − $b = $ans",
                spokenText = "Paso tres: ¡Completamos la resta! $a menos $b es igual a $ans.",
                visualType = HintVisualType.VERTICAL_SUBTRACTION,
                columnSubtraction = data
            )
        )
    } else {
        val steps = (1..b).map { a - it }
        return ProgressiveHintSet(
            step1 = HintStep(
                title = "Paso 1: Punto de partida",
                displayText = "💡 Comienza en $a y retrocede $b números.",
                spokenText = "Ayuda: comienza en $a y retrocede $b pasos.",
                visualType = HintVisualType.SIMPLE_COUNTING
            ),
            step2 = HintStep(
                title = "Paso 2: Retrocede paso a paso",
                displayText = "$a ➜ " + steps.joinToString(" ➜ "),
                spokenText = "Vamos hacia atrás: " + steps.joinToString(", ") { "$it" },
                visualType = HintVisualType.SIMPLE_COUNTING
            ),
            step3 = HintStep(
                title = "Paso 3: ¡Resultado final!",
                displayText = "$a − $b = $ans",
                spokenText = "$a menos $b es igual a $ans.",
                visualType = HintVisualType.SIMPLE_COUNTING
            )
        )
    }
}

private fun generateMultiplicationHints(a: Int, b: Int, ans: Int): ProgressiveHintSet {
    val isMultiDigit = a > 10 || b > 10

    if (isMultiDigit) {
        val tens = (a / 10) * 10
        val units = a % 10
        val part1 = tens * b
        val part2 = units * b

        return ProgressiveHintSet(
            step1 = HintStep(
                title = "Paso 1: Descomposición fácil 🌿",
                displayText = "Descomponemos $a en $tens + $units",
                spokenText = "Paso uno: Descomponemos $a en $tens más $units para calcular más fácil.",
                visualType = HintVisualType.MULTIPLICATION_STEPS
            ),
            step2 = HintStep(
                title = "Paso 2: Multiplicamos cada parte ⚡",
                displayText = "($tens × $b = $part1)  y  ($units × $b = $part2)",
                spokenText = "Paso dos: Multiplicamos: $tens por $b es $part1, y $units por $b es $part2.",
                visualType = HintVisualType.MULTIPLICATION_STEPS
            ),
            step3 = HintStep(
                title = "Paso 3: Sumamos los resultados 🏆",
                displayText = "$part1 + $part2 = $ans",
                spokenText = "Paso tres: Sumamos $part1 más $part2 y obtenemos $ans. ¡Excelente!",
                visualType = HintVisualType.MULTIPLICATION_STEPS
            )
        )
    } else {
        return ProgressiveHintSet(
            step1 = HintStep(
                title = "Paso 1: Grupos iguales 🧺",
                displayText = "💡 $a × $b significa juntar $a grupos de $b.",
                spokenText = "Recuerda que multiplicar es sumar grupos iguales: $a veces $b.",
                visualType = HintVisualType.MULTIPLICATION_STEPS
            ),
            step2 = HintStep(
                title = "Paso 2: Sumando los grupos",
                displayText = List(a.coerceAtMost(5)) { "$b" }.joinToString(" + ") + if (a > 5) " + ... = $ans" else " = $ans",
                spokenText = "Sumando $a grupos de $b llegamos a $ans.",
                visualType = HintVisualType.MULTIPLICATION_STEPS
            ),
            step3 = HintStep(
                title = "Paso 3: ¡Resultado final!",
                displayText = "$a × $b = $ans",
                spokenText = "¡Genial! $a por $b es igual a $ans.",
                visualType = HintVisualType.MULTIPLICATION_STEPS
            )
        )
    }
}

private fun generateDivisionHints(a: Int, b: Int, ans: Int): ProgressiveHintSet {
    return ProgressiveHintSet(
        step1 = HintStep(
            title = "Paso 1: Pensar en la multiplicación inversa 🔍",
            displayText = "💡 ¿Qué número multiplicado por $b da $a?",
            spokenText = "Paso uno: Piensa qué número multiplicado por $b es igual a $a.",
            visualType = HintVisualType.DIVISION_STEPS
        ),
        step2 = HintStep(
            title = "Paso 2: Comprobación con la tabla 💡",
            displayText = "$b × $ans = $a",
            spokenText = "Paso dos: En la tabla vemos que $b por $ans es $a.",
            visualType = HintVisualType.DIVISION_STEPS
        ),
        step3 = HintStep(
            title = "Paso 3: ¡Resultado del reparto! 🏆",
            displayText = "$a ÷ $b = $ans",
            spokenText = "Paso tres: Por lo tanto, $a entre $b es igual a $ans. ¡Muy bien!",
            visualType = HintVisualType.DIVISION_STEPS
        )
    )
}

private fun generateCompletionHints(exercise: Exercise): ProgressiveHintSet {
    val a = exercise.operand1
    val b = exercise.operand2
    val ans = exercise.correctAnswer

    return when (exercise.operationType) {
        OperationType.ADDITION -> {
            if (exercise.missingTarget == MissingTarget.FIRST_OPERAND) {
                ProgressiveHintSet(
                    step1 = HintStep(
                        title = "Paso 1: ¿Qué número sumamos?",
                        displayText = "Buscamos un número que al sumarle $b resulte en $a.",
                        spokenText = "Ayuda: qué número más $b es igual a $a."
                    ),
                    step2 = HintStep(
                        title = "Paso 2: Resta el total",
                        displayText = "$a − $b = $ans",
                        spokenText = "Restamos $a menos $b y obtenemos $ans."
                    ),
                    step3 = HintStep(
                        title = "Paso 3: ¡Número encontrado!",
                        displayText = "$ans + $b = $a",
                        spokenText = "¡Excelente! El número que falta es $ans."
                    )
                )
            } else {
                ProgressiveHintSet(
                    step1 = HintStep(
                        title = "Paso 1: ¿Cuánto falta?",
                        displayText = "Tienes $a y necesitas llegar a $b. ¿Cuánto le falta?",
                        spokenText = "Tienes $a y necesitas llegar a $b. Piensa cuánto debes sumar."
                    ),
                    step2 = HintStep(
                        title = "Paso 2: Resta para encontrar la diferencia",
                        displayText = "$b − $a = $ans",
                        spokenText = "Restamos $b menos $a y obtenemos $ans."
                    ),
                    step3 = HintStep(
                        title = "Paso 3: ¡Número encontrado!",
                        displayText = "$a + $ans = $b",
                        spokenText = "¡Excelente! El número que falta es $ans."
                    )
                )
            }
        }
        OperationType.SUBTRACTION -> {
            if (exercise.missingTarget == MissingTarget.FIRST_OPERAND) {
                ProgressiveHintSet(
                    step1 = HintStep(
                        title = "Paso 1: ¿Cuánto había al inicio?",
                        displayText = "Había una cantidad inicial. Se le quitaron $b y quedaron $a.",
                        spokenText = "Piensa qué cantidad teníamos si al quitarle $b quedaron $a."
                    ),
                    step2 = HintStep(
                        title = "Paso 2: Suma para recuperar el total",
                        displayText = "$a + $b = $ans",
                        spokenText = "Sumamos lo que quedó más lo que se quitó: $a más $b es $ans."
                    ),
                    step3 = HintStep(
                        title = "Paso 3: ¡Cantidad inicial encontrada!",
                        displayText = "$ans − $b = $a",
                        spokenText = "¡Muy bien! El número inicial era $ans."
                    )
                )
            } else {
                ProgressiveHintSet(
                    step1 = HintStep(
                        title = "Paso 1: ¿Cuánto se quitó?",
                        displayText = "Teníamos $a y después de restar quedaron $b. ¿Cuánto quitamos?",
                        spokenText = "Teníamos $a y quedaron $b. ¿Cuánto se restó?"
                    ),
                    step2 = HintStep(
                        title = "Paso 2: Resta lo que quedó",
                        displayText = "$a − $b = $ans",
                        spokenText = "Restamos $a menos $b y obtenemos $ans."
                    ),
                    step3 = HintStep(
                        title = "Paso 3: ¡Número encontrado!",
                        displayText = "$a − $ans = $b",
                        spokenText = "¡Genial! El número que se restó es $ans."
                    )
                )
            }
        }
        OperationType.MULTIPLICATION -> {
            ProgressiveHintSet(
                step1 = HintStep(
                    title = "Paso 1: Factor misterioso",
                    displayText = "¿Por cuánto multiplicamos a $a para obtener $b?",
                    spokenText = "Piensa por qué número debes multiplicar $a para llegar a $b."
                ),
                step2 = HintStep(
                    title = "Paso 2: Divide el total entre el factor",
                    displayText = "$b ÷ $a = $ans",
                    spokenText = "Dividimos $b entre $a y obtenemos $ans."
                ),
                step3 = HintStep(
                    title = "Paso 3: ¡Factor encontrado!",
                    displayText = "$a × $ans = $b",
                    spokenText = "¡Excelente! El factor misterioso es $ans."
                )
            )
        }
        OperationType.DIVISION -> {
            ProgressiveHintSet(
                step1 = HintStep(
                    title = "Paso 1: ¿Cuánto se repartió?",
                    displayText = "¿Qué cantidad total al dividirse entre $b da $a a cada uno?",
                    spokenText = "Buscamos el total que se repartió entre $b partes."
                ),
                step2 = HintStep(
                    title = "Paso 2: Multiplica las partes",
                    displayText = "$a × $b = $ans",
                    spokenText = "Multiplicamos $a por $b y obtenemos $ans."
                ),
                step3 = HintStep(
                    title = "Paso 3: ¡Total encontrado!",
                    displayText = "$ans ÷ $b = $a",
                    spokenText = "¡Fantástico! El total repartido era $ans."
                )
            )
        }
    }
}
