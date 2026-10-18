package com.educa.mathkids.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.R
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Barra de navegación inferior para pantallas de ejercicio y juego:
 * Contiene los 4 botones solicitados:
 * 1. INICIO: Regresa a la pantalla principal.
 * 2. ATRÁS: Retrocede a la pantalla previa.
 * 3. NIVELES: Acceso a la selección o mapa de niveles.
 * 4. SALIDA: Despliega diálogo amigable de confirmación para salir.
 */
@Composable
fun KidsGameNavDock(
    onInicioClick: () -> Unit,
    onAtrasClick: () -> Unit,
    onNivelesClick: () -> Unit,
    onSalidaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = Color.Black.copy(alpha = 0.14f))
            .clip(RoundedCornerShape(26.dp))
            .background(SurfaceWhite.copy(alpha = 0.98f))
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(26.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. INICIO
            KidsGameNavItem(
                label = strings.navHome,
                labelColor = Color(0xFF1E88E5),
                onClick = onInicioClick
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_nav_inicio),
                    contentDescription = strings.navHome,
                    modifier = Modifier.size(36.dp)
                )
            }

            // 2. ATRÁS
            KidsGameNavItem(
                label = strings.navBack,
                labelColor = Color(0xFF3949AB),
                onClick = onAtrasClick
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8EAF6))
                        .border(1.5.dp, Color(0xFF3F51B5).copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⬅️", fontSize = 18.sp)
                }
            }

            // 3. EJERCICIOS
            KidsGameNavItem(
                label = strings.navGames,
                labelColor = Color(0xFF43A047),
                onClick = onNivelesClick
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_nav_ejercicios),
                    contentDescription = strings.navGames,
                    modifier = Modifier.size(36.dp)
                )
            }

            // 4. SALIDA
            KidsGameNavItem(
                label = strings.navExit,
                labelColor = SoftRed,
                onClick = onSalidaClick
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                        .border(1.5.dp, SoftRed.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚪", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun KidsGameNavItem(
    label: String,
    labelColor: Color = SlateDark,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontFamily = FredokaFont,
            fontWeight = FontWeight.Black,
            color = labelColor,
            maxLines = 1,
            softWrap = false
        )
    }
}
