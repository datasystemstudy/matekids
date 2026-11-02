# ProGuard & R8 rules para MateKids

# 1. Preservar Entidades, DAOs y Database de Room para SQLite
-keep class com.educa.mathkids.data.local.db.entity.** { *; }
-keep class com.educa.mathkids.data.local.db.dao.** { *; }
-keep class com.educa.mathkids.data.local.db.MathKidsDatabase { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# 2. Preservar Modelos de Dominio y Enums
-keep class com.educa.mathkids.domain.model.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 3. Preservar DataStore Preferences
-keep class androidx.datastore.preferences.core.** { *; }

# 4. Preservar utilidades críticas de Audio, Voz y Háptica
-keep class com.educa.mathkids.presentation.utils.SoundManager { *; }
-keep class com.educa.mathkids.presentation.utils.HapticUtil { *; }
-keep class com.educa.mathkids.presentation.utils.TtsManager { *; }

# 5. Google Play Services / AdMob
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# 6. Kotlinx Coroutines
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# 7. Stacktraces y depuración en producción
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

