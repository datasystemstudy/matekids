package com.educa.mathkids.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Componente reutilizable para mostrar la racha de respuestas correctas consecutivas.
 */
@Composable
fun StreakDisplay(
    streak: Int,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    showLabel: Boolean = true,
    backgroundColor: Color = SurfaceWhite
) {
    val fireColor = if (streak > 0) Color(0xFFE65100) else Color(0xFF9E9E9E)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🔥",
                fontSize = fontSize,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = if (showLabel) "Racha: $streak" else "$streak",
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = fireColor
            )
        }
    }
}
