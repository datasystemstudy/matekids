package com.educa.mathkids.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.components.KidButton
import com.educa.mathkids.presentation.components.KidCard
import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage
import com.educa.mathkids.presentation.localization.appStrings
import com.educa.mathkids.presentation.theme.BackgroundCream
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.BluePrimaryDark
import com.educa.mathkids.presentation.theme.CoralEncouragement
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.TextDark

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onWatchIntroVideo: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settings by viewModel.settingsState.collectAsState()
    val strings = appStrings()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.navBack,
                    tint = TextDark
                )
            }

            Text(
                text = strings.settingsTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ============================================================
        // SELECCIÓN DE IDIOMA / LANGUAGE SELECTOR
        // ============================================================
        KidCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌍", fontSize = 22.sp, modifier = Modifier.padding(end = 10.dp))
                    Text(
                        text = strings.languageSectionTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = settings.language.equals(lang.code, ignoreCase = true)
                        val btnColor = if (isSelected) GreenPrimary else Color(0xFFF1F5F9)
                        val textColor = if (isSelected) Color.White else TextDark
                        val borderColor = if (isSelected) GreenPrimary else Color(0xFFCBD5E1)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(btnColor)
                                .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.onLanguageChanged(lang.code)
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = lang.flagEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = lang.displayName,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Opciones de configuración de audio y efectos
        KidCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingSwitchItem(
                    title = strings.soundsTitle,
                    emoji = "🔊",
                    checked = settings.soundsGeneralEnabled,
                    onCheckedChange = { viewModel.onSoundsGeneralToggled(it) }
                )

                SettingSwitchItem(
                    title = strings.voiceTitle,
                    emoji = "🗣️",
                    checked = settings.voiceEnabled,
                    onCheckedChange = { viewModel.onVoiceToggled(it) }
                )

                SettingSwitchItem(
                    title = strings.musicTitle,
                    emoji = "🎵",
                    checked = settings.musicEnabled,
                    onCheckedChange = { viewModel.onMusicToggled(it) }
                )

                SettingSwitchItem(
                    title = strings.soundEffectsTitle,
                    emoji = "🔔",
                    checked = settings.soundEffectsEnabled,
                    onCheckedChange = { viewModel.onSoundEffectsToggled(it) }
                )

                SettingSwitchItem(
                    title = strings.hapticsTitle,
                    emoji = "📳",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { viewModel.onHapticsToggled(it) }
                )

                SettingSwitchItem(
                    title = strings.animationsTitle,
                    emoji = "✨",
                    checked = settings.animationsEnabled,
                    onCheckedChange = { viewModel.onAnimationsToggled(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Botón para volver a ver el video de introducción
        val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES
        KidButton(
            text = if (isEnglish) "🎬 Watch Intro Video" else "🎬 Ver video de bienvenida",
            onClick = onWatchIntroVideo,
            modifier = Modifier.fillMaxWidth(),
            primaryColor = BluePrimary,
            darkColor = BluePrimaryDark,
            height = 56.dp,
            fontSize = 17.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de restablecimiento
        KidButton(
            text = strings.resetProgressBtn,
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            primaryColor = CoralEncouragement,
            darkColor = SoftRed,
            height = 60.dp,
            fontSize = 18.sp
        )
    }

    // Diálogo de confirmación para restablecer progreso
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = strings.resetDialogTitle,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = strings.resetDialogMessage,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onResetProgressConfirmed()
                        com.educa.mathkids.core.game.AdventureProgressManager.resetAll(context)
                        com.educa.mathkids.core.shop.CoinShopManager.reset(context)
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftRed)
                ) {
                    Text(
                        text = strings.resetDialogConfirm,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(
                        text = strings.resetDialogCancel,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    emoji: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenPrimary
            )
        )
    }
}
