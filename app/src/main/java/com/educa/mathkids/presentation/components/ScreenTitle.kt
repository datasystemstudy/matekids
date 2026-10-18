package com.educa.mathkids.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted

/**
 * Componente reutilizable para títulos de pantalla y secciones en MateKids.
 */
@Composable
fun ScreenTitle(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleColor: Color = TextDark,
    fontSize: TextUnit = 26.sp,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            text = title,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = titleColor,
            textAlign = if (alignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start
        )

        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = if (alignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start
            )
        }
    }
}
