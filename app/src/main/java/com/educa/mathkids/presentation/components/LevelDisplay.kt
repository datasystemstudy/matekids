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
import com.educa.mathkids.domain.model.ScoringConfig
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted

/**
 * Componente reutilizable para mostrar el nivel del jugador y su avance hacia el siguiente nivel.
 */
@Composable
fun LevelDisplay(
    level: Int,
    totalPoints: Int,
    modifier: Modifier = Modifier,
    showProgressBar: Boolean = true,
    backgroundColor: Color = SurfaceWhite
) {
    val inLevel = ScoringConfig.pointsInCurrentLevel(totalPoints)
    val needed = ScoringConfig.POINTS_PER_LEVEL
    val fraction = ScoringConfig.levelProgressFraction(totalPoints)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏆",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "Nivel $level",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                }

                if (showProgressBar) {
                    Text(
                        text = "$inLevel / $needed XP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }

            if (showProgressBar) {
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GreenPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }
    }
}
