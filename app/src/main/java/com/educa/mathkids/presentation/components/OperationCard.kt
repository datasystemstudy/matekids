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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.TextDark

/**
 * Componente único y reutilizable para seleccionar cualquiera de las 4 operaciones.
 * Recibe título, símbolo, color, tipo de operación y callback onClick.
 */
@Composable
fun OperationCard(
    title: String,
    symbol: String,
    color: Color,
    operationType: OperationType,
    onClick: (OperationType) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "op_card_scale"
    )

    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .height(138.dp)
            .shadow(
                elevation = if (isPressed) Dimens.CardElevationPressed else Dimens.CardElevationDefault,
                shape = shape,
                spotColor = color.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(SurfaceWhite)
            .border(2.5.dp, color.copy(alpha = 0.35f), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = { onClick(operationType) }
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Contenedor circular con el símbolo de la operación
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = color
                )
            }

            // Título de la operación (SUMAR, RESTAR, MULTIPLICAR, DIVIDIR)
            Text(
                text = title,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FredokaFont,
                color = TextDark,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
