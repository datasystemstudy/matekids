package com.educa.mathkids.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.presentation.theme.SurfaceWhite

import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage

/**
 * Componente reutilizable para mostrar puntos acumulados con la moneda oficial de estrellas.
 */
@Composable
fun ScoreDisplay(
    points: Int,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    showLabel: Boolean = true,
    backgroundColor: Color = SurfaceWhite
) {
    val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES
    val labelText = if (showLabel) {
        if (isEnglish) "$points pts" else "$points puntos"
    } else "$points"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🪙",
                fontSize = (fontSize.value + 2).sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = labelText,
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFF57F17)
            )
        }
    }
}
