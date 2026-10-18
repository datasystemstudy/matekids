package com.educa.mathkids.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted

/**
 * Componente reutilizable para mostrar métricas individuales (Fase 8).
 * Ejemplo: ⭐ 350 Puntos, 🔥 8 Mejor racha, 📚 12 Sesiones, 🎯 80% Precisión.
 */
@Composable
fun StatCard(
    icon: String,
    value: String,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    accentColor: Color = TextDark,
    backgroundColor: Color = SurfaceWhite
) {
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .shadow(
                elevation = Dimens.CardElevationDefault,
                shape = shape,
                spotColor = Color.Black.copy(alpha = 0.06f)
            )
            .clip(shape)
            .background(backgroundColor)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )

            if (description != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
