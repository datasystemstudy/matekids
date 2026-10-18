package com.educa.mathkids.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.DialogProperties
import com.educa.mathkids.domain.model.Exercise
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.BluePrimaryDark
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Diálogo modal pedagógico (Fase 9: Retroalimentación constructiva)
 * "💡 Vamos a resolverlo juntos."
 * Muestra el procedimiento visual paso a paso sin penalizar al alumno.
 */
@Composable
fun PedagogicalFeedbackDialog(
    exercise: Exercise,
    accentColor: Color,
    onContinue: () -> Unit
) {
    Dialog(
        onDismissRequest = onContinue,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val shape = RoundedCornerShape(26.dp)

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(220)) + scaleIn(
                initialScale = 0.88f,
                animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .shadow(16.dp, shape)
                    .clip(shape)
                    .background(SurfaceWhite)
                    .border(2.5.dp, OrangeSecondary.copy(alpha = 0.5f), shape)
                    .padding(22.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Cabecera con foco constructivo
                    Text(
                        text = "💡 Vamos a resolverlo juntos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangeSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Badge de Estrategia Didáctica
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.08f))
                            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = exercise.didacticStrategy.iconEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = exercise.didacticStrategy.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // La operación original con la solución
                    val solvedEquation = exercise.displayEquation?.replace("?", "${exercise.correctAnswer}")
                        ?: "${exercise.operand1} ${exercise.operationType.symbol} ${exercise.operand2} = ${exercise.correctAnswer}"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(accentColor.copy(alpha = 0.08f))
                            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = solvedEquation,
                            fontSize = if (solvedEquation.length > 16) 20.sp else 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Apoyo visual del ejercicio
                    VisualRepresentationView(
                        visual = exercise.visualRepresentation,
                        accentColor = accentColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Explicación en texto claro y amigable
                    Text(
                        text = exercise.explanation.ifEmpty {
                            "La respuesta correcta es ${exercise.correctAnswer}. ¡Sigue practicando y lo dominarás!"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón "¡Entendido! Continuar"
                    AnimatedGameButton(
                        text = "¡Entendido! Continuar ➜",
                        onClick = onContinue,
                        primaryColor = BluePrimary,
                        darkColor = BluePrimaryDark,
                        height = 50.dp,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
