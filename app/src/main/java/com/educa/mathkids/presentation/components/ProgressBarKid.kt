package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary

@Composable
fun ProgressBarKid(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    trackColor: Color = Color(0xFFE2E8F0),
    progressColor: Color = GreenPrimary
) {
    val progressFraction = if (total > 0) (current.toFloat() / total).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        label = "progress_bar_anim"
    )

    val shape = RoundedCornerShape(height / 2)

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(progressColor, OrangeSecondary)
                    )
                )
        )
    }
}
