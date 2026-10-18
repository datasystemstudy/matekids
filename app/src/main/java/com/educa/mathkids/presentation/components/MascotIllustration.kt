package com.educa.mathkids.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Deprecated("Utilizar el componente unificado Mascot", ReplaceWith("Mascot(modifier, mood, size)"))
@Composable
fun MascotIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    mood: MascotMood = MascotMood.HAPPY
) {
    Mascot(
        modifier = modifier,
        mood = mood,
        size = size
    )
}
