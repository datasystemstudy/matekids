# ProGuard & R8 rules para MateKids (Fase 11)

# 1. Preservar Entidades y DAOs de Room para SQLite
-keep class com.educa.mathkids.data.local.db.entity.** { *; }
-keep class com.educa.mathkids.data.local.db.dao.** { *; }
-keep class com.educa.mathkids.data.local.db.MathKidsDatabase { *; }

# 2. Preservar Modelos de Dominio
-keep class com.educa.mathkids.domain.model.** { *; }

# 3. Preservar DataStore Preferences
-keep class androidx.datastore.preferences.core.** { *; }

# 4. Preservar utilidades de Audio y Háptica
-keep class com.educa.mathkids.presentation.utils.SoundManager { *; }
-keep class com.educa.mathkids.presentation.utils.HapticUtil { *; }
