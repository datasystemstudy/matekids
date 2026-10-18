package com.educa.mathkids.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted

/**
 * Diálogo modal para celebrar el desbloqueo de una recompensa (Fase 9).
 * Incluye animación de entrada de escala con resorte, confeti festivo y botón de cierre fácil.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardUnlockDialog(
    reward: Reward,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            tonalElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Confeti de fondo
                ConfettiCelebration(visible = true)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Encabezado
                    Text(
                        text = "🎉 ¡NUEVA RECOMPENSA!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangeSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contenedor del icono grande de la recompensa
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF9C4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = reward.icon,
                            fontSize = 48.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Título de la recompensa
                    Text(
                        text = reward.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Descripción del logro
                    Text(
                        text = reward.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón para continuar
                    AnimatedGameButton(
                        text = "CONTINUAR",
                        onClick = onDismiss,
                        primaryColor = GreenPrimary,
                        darkColor = GreenPrimaryDark,
                        height = 60.dp,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
