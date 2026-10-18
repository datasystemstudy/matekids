package com.educa.mathkids.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite

enum class KidsNavTab {
    INICIO,
    JUEGOS,
    PREMIOS,
    AJUSTES
}

/**
 * Dock de navegación inferior oficial para niños con iconos 3D y texto anti-desbordamiento.
 * Mantiene coherencia visual a través de todas las pantallas principales y de operaciones.
 */
@Composable
fun KidsBottomNavDock(
    currentTab: KidsNavTab = KidsNavTab.INICIO,
    onInicioClick: () -> Unit,
    onJuegosClick: () -> Unit,
    onPremiosClick: () -> Unit,
    onAjustesClick: () -> Unit,
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
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KidsNavItem(
                iconRes = R.drawable.ic_nav_inicio,
                label = strings.navHome,
                isSelected = currentTab == KidsNavTab.INICIO,
                onClick = onInicioClick
            )
            KidsNavItem(
                iconRes = R.drawable.ic_nav_ejercicios,
                label = strings.navGames,
                isSelected = currentTab == KidsNavTab.JUEGOS,
                onClick = onJuegosClick
            )
            KidsNavItem(
                iconRes = R.drawable.ic_nav_premios,
                label = strings.navRewards,
                isSelected = currentTab == KidsNavTab.PREMIOS,
                onClick = onPremiosClick
            )
            KidsNavItem(
                iconRes = R.drawable.ic_nav_ajustes,
                label = strings.navSettings,
                isSelected = currentTab == KidsNavTab.AJUSTES,
                onClick = onAjustesClick
            )
        }
    }
}

@Composable
private fun KidsNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontFamily = FredokaFont,
            fontWeight = FontWeight.Black,
            color = if (isSelected) OrangeSecondary else SlateDark,
            maxLines = 1,
            softWrap = false
        )
    }
}
