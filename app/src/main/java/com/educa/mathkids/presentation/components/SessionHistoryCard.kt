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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tarjeta de sesión completada en la actividad reciente (Fase 8).
 */
@Composable
fun SessionHistoryCard(
    record: GameSessionRecord,
    modifier: Modifier = Modifier
) {
    val op = record.operationType
    val opColor = Color(op.colorHex)

    val dateFormatted = formatSessionDate(record.timestamp)

    KidCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(opColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = op.symbol,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = opColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = op.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = " • $dateFormatted",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${record.correctCount}/${record.totalExercises} correctas (${record.accuracyPercentage}%)",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }

            // Puntos ganados en la sesión
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF3E0))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "+${record.pointsEarned} pts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE65100)
                )
            }
        }
    }
}

private fun formatSessionDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diffMillis = now - timestamp
    val oneDayMillis = 24 * 60 * 60 * 1000L

    return when {
        diffMillis < oneDayMillis -> "Hoy"
        diffMillis < 2 * oneDayMillis -> "Ayer"
        else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(timestamp))
    }
}
