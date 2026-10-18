package com.educa.mathkids.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.OperationStats
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted

/**
 * Tarjeta de progreso por operación matemática (Fase 8).
 * Muestra nombre, símbolo, total de ejercicios, correctas, porcentaje y barra real.
 */
@Composable
fun OperationProgressCard(
    stats: OperationStats,
    modifier: Modifier = Modifier
) {
    val op = stats.operationType
    val opColor = Color(op.colorHex)
    val progressFraction = (stats.accuracy / 100f).coerceIn(0f, 1f)

    KidCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(opColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = op.symbol,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = opColor
                        )
                    }

                    Text(
                        text = op.displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Text(
                    text = "${stats.accuracy}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = opColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progreso que refleja realmente la precisión
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = opColor,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${stats.exercisesCompleted} ejercicios (${stats.correctAnswers} correctos)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                if (stats.bestStreak > 0) {
                    Text(
                        text = "🔥 Racha: ${stats.bestStreak}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}
