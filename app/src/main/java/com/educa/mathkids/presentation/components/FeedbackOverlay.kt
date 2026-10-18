package com.educa.mathkids.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.educa.mathkids.presentation.theme.CoralEncouragement
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.YellowStar

@Composable
fun FeedbackOverlay(
    visible: Boolean,
    isCorrect: Boolean,
    correctAnswer: Int,
    pointsEarned: Int,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.85f),
        exit = fadeOut() + scaleOut(targetScale = 0.85f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            val containerColor = if (isCorrect) GreenPrimary else CoralEncouragement
            val shape = RoundedCornerShape(32.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(SurfaceWhite)
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Mascot(
                        size = 120.dp,
                        mood = if (isCorrect) MascotMood.CELEBRATING else MascotMood.THINKING
                    )

                    Text(
                        text = if (isCorrect) "🎉 ¡Muy bien!" else "💪 ¡Casi!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = containerColor,
                        textAlign = TextAlign.Center
                    )

                    if (isCorrect) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🎯 +$pointsEarned Puntos",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57F17)
                            )
                        }
                    } else {
                        Text(
                            text = "La respuesta correcta era $correctAnswer.\n¡Cada intento te hace más sabio!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242),
                            textAlign = TextAlign.Center
                        )
                    }

                    KidButton(
                        text = "Continuar ➜",
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        primaryColor = containerColor,
                        darkColor = if (isCorrect) GreenPrimaryDark else Color(0xFFD84315),
                        height = 60.dp
                    )
                }
            }
        }
    }
}
