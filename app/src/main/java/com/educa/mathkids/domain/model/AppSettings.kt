package com.educa.mathkids.domain.model

data class AppSettings(
    val soundsGeneralEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val language: String = "es"
)

