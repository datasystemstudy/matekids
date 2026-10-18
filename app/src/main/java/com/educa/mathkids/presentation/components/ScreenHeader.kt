package com.educa.mathkids.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.TextDark

/**
 * Encabezado de navegación consistente para pantallas secundarias (← Título o 🏠 Título).
 */
@Composable
fun ScreenHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHomeButton: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isHomeButton) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = OrangeSecondary.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_nav_inicio),
                    contentDescription = "Inicio",
                    modifier = Modifier.size(46.dp)
                )
            }
        } else {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextDark
                )
            }
        }

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FredokaFont,
            color = TextDark,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            textAlign = TextAlign.Center
        )

        // Botón de acción adicional (ej. Bocina) o espaciador balanceador
        if (trailingContent != null) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                trailingContent()
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}
