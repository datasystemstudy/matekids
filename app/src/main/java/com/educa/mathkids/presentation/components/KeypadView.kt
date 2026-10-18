package com.educa.mathkids.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Teclado numérico táctil interactivo integrado directamente en la app
 * (Fase 8: Entrada numérica con feedback y diseño tecnológico).
 */
@Composable
fun KeypadView(
    currentInput: String,
    onDigitPress: (Char) -> Unit,
    onBackspacePress: () -> Unit,
    onSubmitPress: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KeypadButton(text = "1", onClick = { onDigitPress('1') }, modifier = Modifier.weight(1f))
            KeypadButton(text = "2", onClick = { onDigitPress('2') }, modifier = Modifier.weight(1f))
            KeypadButton(text = "3", onClick = { onDigitPress('3') }, modifier = Modifier.weight(1f))
        }

        // Fila 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KeypadButton(text = "4", onClick = { onDigitPress('4') }, modifier = Modifier.weight(1f))
            KeypadButton(text = "5", onClick = { onDigitPress('5') }, modifier = Modifier.weight(1f))
            KeypadButton(text = "6", onClick = { onDigitPress('6') }, modifier = Modifier.weight(1f))
        }

        // Fila 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KeypadButton(text = "7", onClick = { onDigitPress('7') }, modifier = Modifier.weight(1f))
            KeypadButton(text = "8", onClick = { onDigitPress('8') }, modifier = Modifier.weight(1f))
            KeypadButton(text = "9", onClick = { onDigitPress('9') }, modifier = Modifier.weight(1f))
        }

        // Fila 4: Borrar ⌫, 0, Confirmar ✓
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KeypadButton(
                text = "⌫",
                onClick = onBackspacePress,
                textColor = SlateMuted,
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "0",
                onClick = { onDigitPress('0') },
                modifier = Modifier.weight(1f)
            )
            KeypadButton(
                text = "✓",
                onClick = onSubmitPress,
                backgroundColor = accentColor,
                textColor = Color.White,
                enabled = currentInput.isNotEmpty(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceWhite,
    textColor: Color = SlateDark,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "keypad_btn_scale"
    )

    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .height(44.dp)
            .shadow(
                elevation = if (isPressed) 1.dp else 3.dp,
                shape = shape,
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f))
            .border(1.5.dp, SlateBorder, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}
