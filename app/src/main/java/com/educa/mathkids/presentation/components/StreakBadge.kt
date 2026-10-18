package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.OrangeSecondary

@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier
) {
    if (streak <= 0) return

    val infiniteTransition = rememberInfiniteTransition(label = "streak_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier
            .scale(if (streak >= 3) scale else 1.0f)
            .background(
                color = OrangeSecondary.copy(alpha = 0.18f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (streak >= 5) "🔥" else "⭐",
            fontSize = 18.sp,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = "x$streak",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = OrangeSecondary
        )
    }
}
