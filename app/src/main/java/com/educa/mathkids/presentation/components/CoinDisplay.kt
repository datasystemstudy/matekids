package com.educa.mathkids.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Chip interactivo que muestra las monedas doradas acumuladas (🪙) y permite tocar para ir a la Tienda.
 */
@Composable
fun CoinDisplay(
    coins: Int,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 15.sp,
    showLabel: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFFFB300).copy(alpha = 0.25f))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFFDE7))
            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
            .then(clickableModifier)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🪙",
                fontSize = (fontSize.value + 3).sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (showLabel) "$coins Monedas" else "$coins",
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FredokaFont,
                color = Color(0xFFE65100)
            )
        }
    }
}
