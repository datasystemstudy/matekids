package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import com.educa.mathkids.presentation.utils.HapticUtil

/**
 * Componente reutilizable para botones de acción con microinteracciones (Fase 9).
 * Centraliza:
 * - Reducción suave de escala al tocar (0.94f con resorte).
 * - Sombra reactiva tridimensional.
 * - Feedback táctil ligero al pulsar.
 * - Soporte de accesibilidad y modo de movimiento reducido.
 */
@Composable
fun AnimatedGameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    primaryColor: Color = GreenPrimary,
    darkColor: Color = GreenPrimaryDark,
    textColor: Color = Color.White,
    height: Dp = Dimens.ButtonHeightPrimary,
    fontSize: TextUnit = 24.sp
) {
    val context = LocalContext.current
    val hapticUtil = remember { HapticUtil(context) }
    val animationsEnabled = LocalAnimationsEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Disparar háptica ligera al detectar pulsación
    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            hapticUtil.vibrateClick()
        }
    }

    val targetScale = if (isPressed && enabled && animationsEnabled) 0.94f else 1.0f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = AnimationConfig.quickSpring(),
        label = "animated_game_btn_scale"
    )

    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (isPressed && enabled) 2.dp else 6.dp,
                shape = shape,
                spotColor = darkColor.copy(alpha = 0.45f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (enabled) {
                        listOf(primaryColor, darkColor)
                    } else {
                        listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E))
                    }
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = text,
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                fontFamily = FredokaFont,
                textAlign = TextAlign.Center
            )
        }
    }
}
