package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import kotlin.math.roundToInt

enum class AnswerButtonState {
    DEFAULT,
    SELECTED_CORRECT,
    SELECTED_INCORRECT,
    HIGHLIGHT_CORRECT,
    DISABLED
}

/**
 * Botón 3D táctil e interactivo estilo arcade para las opciones de respuesta matemática.
 * Diseñado específicamente para dedos de niños:
 * - Depresión física 3D al pulsar (offset vertical con muelle).
 * - Profundidad inferior (bevel 3D).
 * - Paletas de color alegres y armoniosas por índice (A, B, C, D).
 * - Animaciones de éxito (pulso y "✓") y error (oscilación suave y "✕").
 * - Háptica integrada en cada toque.
 */
@Composable
fun AnswerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: AnswerButtonState = AnswerButtonState.DEFAULT,
    enabled: Boolean = true,
    buttonIndex: Int = 0,
    accentColor: Color? = null
) {
    val context = LocalContext.current
    val hapticUtil = remember { HapticUtil(context) }
    val animationsEnabled = LocalAnimationsEnabled.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Disparar háptica al presionar
    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            hapticUtil.vibrateClick()
        }
    }

    // Animación de oscilación horizontal (shake) en caso de error
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state == AnswerButtonState.SELECTED_INCORRECT && animationsEnabled) {
            shakeOffset.snapTo(0f)
            val offsets = listOf(7f, -7f, 5f, -5f, 2f, -2f, 0f)
            for (offset in offsets) {
                shakeOffset.animateTo(
                    targetValue = offset,
                    animationSpec = tween(durationMillis = 35)
                )
            }
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    // Micro-animación de escala en acierto
    val targetScale = when {
        state == AnswerButtonState.SELECTED_CORRECT && animationsEnabled -> 1.05f
        else -> 1.0f
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = AnimationConfig.quickSpring(),
        label = "btn_scale"
    )

    // Paleta de acento según la posición de la opción (A: Azul, B: Naranja, C: Verde, D: Púrpura)
    val optionColor = accentColor ?: when (buttonIndex % 4) {
        0 -> Color(0xFF0288D1) // Azul cielo
        1 -> Color(0xFFEA580C) // Naranja cálido
        2 -> Color(0xFF16A34A) // Verde esmeralda
        else -> Color(0xFF9333EA) // Púrpura diversión
    }
    val optionTintFace = when (buttonIndex % 4) {
        0 -> Color(0xFFF0F9FF)
        1 -> Color(0xFFFFF7ED)
        2 -> Color(0xFFF0FDF4)
        else -> Color(0xFFFAF5FF)
    }
    val optionBorderColor = when (buttonIndex % 4) {
        0 -> Color(0xFF7DD3FC)
        1 -> Color(0xFFFDBA74)
        2 -> Color(0xFF86EFAC)
        else -> Color(0xFFD8B4FE)
    }

    val cornerRadius = 20.dp
    val totalHeight = Dimens.ButtonHeightAnswer // 64dp
    val depthOffset = 5.dp

    // Desplazamiento táctil 3D hacia abajo al presionar
    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed && enabled && animationsEnabled) depthOffset - 1.dp else 0.dp,
        animationSpec = AnimationConfig.quickSpring(),
        label = "press_offset"
    )

    // Colores de la base inferior (3D Bevel) y del borde
    val baseDepthColor = when (state) {
        AnswerButtonState.DEFAULT -> optionColor
        AnswerButtonState.SELECTED_CORRECT, AnswerButtonState.HIGHLIGHT_CORRECT -> Color(0xFF15803D)
        AnswerButtonState.SELECTED_INCORRECT -> Color(0xFFB91C1C)
        AnswerButtonState.DISABLED -> Color(0xFFCBD5E1)
    }

    val faceBorderColor = when (state) {
        AnswerButtonState.DEFAULT -> optionBorderColor
        AnswerButtonState.SELECTED_CORRECT, AnswerButtonState.HIGHLIGHT_CORRECT -> Color(0xFF16A34A)
        AnswerButtonState.SELECTED_INCORRECT -> Color(0xFFDC2626)
        AnswerButtonState.DISABLED -> Color(0xFFE2E8F0)
    }

    // Relleno de la cara frontal (Face)
    val faceBrush = when (state) {
        AnswerButtonState.DEFAULT -> Brush.verticalGradient(
            listOf(SurfaceWhite, optionTintFace)
        )
        AnswerButtonState.SELECTED_CORRECT, AnswerButtonState.HIGHLIGHT_CORRECT -> Brush.verticalGradient(
            listOf(Color(0xFF4ADE80), Color(0xFF22C55E))
        )
        AnswerButtonState.SELECTED_INCORRECT -> Brush.verticalGradient(
            listOf(Color(0xFFF87171), Color(0xFFEF4444))
        )
        AnswerButtonState.DISABLED -> Brush.verticalGradient(
            listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
        )
    }

    val textColor = when (state) {
        AnswerButtonState.DEFAULT -> SlateDark
        AnswerButtonState.SELECTED_CORRECT, AnswerButtonState.HIGHLIGHT_CORRECT -> Color.White
        AnswerButtonState.SELECTED_INCORRECT -> Color.White
        AnswerButtonState.DISABLED -> SlateMuted.copy(alpha = 0.4f)
    }

    val stateIcon = when (state) {
        AnswerButtonState.SELECTED_CORRECT, AnswerButtonState.HIGHLIGHT_CORRECT -> "✓"
        AnswerButtonState.SELECTED_INCORRECT -> "✕"
        else -> null
    }

    val accessibilityLabel = when (state) {
        AnswerButtonState.SELECTED_CORRECT -> "Opción $text, respuesta correcta"
        AnswerButtonState.SELECTED_INCORRECT -> "Opción $text, respuesta incorrecta"
        AnswerButtonState.HIGHLIGHT_CORRECT -> "Opción $text, esta era la respuesta correcta"
        AnswerButtonState.DISABLED -> "Opción $text, deshabilitada"
        AnswerButtonState.DEFAULT -> "Opción $text"
    }

    Box(
        modifier = modifier
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .scale(scale)
            .fillMaxWidth()
            .height(totalHeight)
            .semantics { contentDescription = accessibilityLabel }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Capa inferior 3D (Base con color de profundidad)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .background(baseDepthColor)
        )

        // Cara frontal móvil que se hunde físicamente al presionar (3D tactile feel)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight - depthOffset)
                .offset(y = pressOffsetY)
                .clip(RoundedCornerShape(cornerRadius - 2.dp))
                .background(faceBrush)
                .border(2.5.dp, faceBorderColor, RoundedCornerShape(cornerRadius - 2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = text,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                if (stateIcon != null) {
                    Text(
                        text = " $stateIcon",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor
                    )
                }
            }
        }
    }
}
