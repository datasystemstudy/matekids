package com.educa.mathkids.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.educa.mathkids.presentation.theme.BorderSoft
import com.educa.mathkids.presentation.theme.SurfaceWhite

@Composable
fun KidCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = SurfaceWhite,
    borderColor: Color = BorderSoft,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(elevation, shape, spotColor = Color.Black.copy(alpha = 0.08f))
            .clip(shape)
            .background(backgroundColor)
            .border(2.dp, borderColor, shape)
            .padding(16.dp),
        content = content
    )
}
