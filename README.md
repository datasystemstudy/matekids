# MateKids — Aplicación Educativa de Matemáticas para Niños

MateKids es una aplicación Android educativa de matemáticas para niños de 6 a 10 años, diseñada para aprender jugando las cuatro operaciones básicas de forma divertida, visual, accesible y motivadora.

---

## 🌟 Identidad y Configuración de Entrega

* **Nombre de la Aplicación:** MateKids (`@string/app_name`)
* **Application ID:** `com.educa.mathkids`
* **VersionCode:** `1`
* **VersionName:** `"1.0.0"`
* **Min SDK:** `26` (Android 8.0 Oreo)
* **Target SDK:** `35` (Android 15)
* **Compile SDK:** `35`
* **Orientación:** Vertical fija (`portrait`)
* **Icono:** Adaptable con la mascota Numi y los símbolos matemáticos (+, −, ×, ÷)

---

## 🚀 Tecnologías y Arquitectura

* **Lenguaje:** Kotlin 2.0+ (Java 21 bytecode target)
* **Interfaz:** Jetpack Compose con Material 3 y Canvas nativo
* **Navegación:** Jetpack Navigation Compose con control de back stack
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture por capas (Presentation → Domain → Data)
* **Concurrencia:** Kotlin Coroutines & StateFlow reactivo
* **Base de Datos Local:** Room Database + SQLite con migraciones manuales seguras (`MIGRATION_1_2`, `MIGRATION_2_3`, `MIGRATION_1_3`)
* **Preferencias:** Jetpack Preferences DataStore
* **Audio:** `ToneGenerator` nativo de Android con debouncing de 90ms (100% offline)
* **Testing:** JUnit 4, Kotlinx Coroutines Test, Compose Testing

---

## 🎮 Funcionalidades Principales

1. **Cuatro Operaciones:** Suma (+), Resta (−), Multiplicación (×) y División (÷).
2. **Cuatro Niveles de Dificultad:** `EASY`, `BASIC`, `INTERMEDIATE`, `ADVANCED`.
3. **Motor Matemático Robusto:**
   * Restas siempre no negativas (`correctAnswer >= 0`).
   * Divisiones siempre exactas sin restos ni decimales (`dividend = divisor * quotient`).
   * Divisor nunca igual a cero (`divisor > 0`).
   * Cuatro opciones de respuesta únicas con la respuesta correcta garantizada y 3 distractores plausibles.
4. **Sistema de Motivación y Progresión:**
   * Puntos por respuesta correcta (+10 puntos, +0 en error).
   * Protección anti-exploit (múltiples toques en el mismo ejercicio no duplican puntos).
   * Rachas consecutivas con bonificaciones especiales (+5 en racha 3, +10 en racha 5, +20 en racha 10).
   * Mejor racha histórica persistente.
   * Sistema de niveles: Nivel 1 (0–99 pts), Nivel 2 (100–199 pts), Nivel 3 (200–299 pts), etc.
   * Catálogo de recompensas virtuales con diálogo modal de desbloqueo.
5. **Panel de Progreso y Estadísticas:**
   * Resumen global de sesiones y ejercicios completados.
   * Porcentaje de precisión general y desglosado por cada operación.
   * Historial de las 5 sesiones más recientes con fecha relativa ("Hoy", "Ayer").
6. **Experiencia Infantil y Accesibilidad:**
   * Mascota Numi en Canvas nativo con 6 expresiones (`NORMAL`, `HAPPY`, `CELEBRATING`, `THINKING`, `SURPRISED`, `ENCOURAGING`).
   * Partículas ligeras de confeti auto-terminantes.
   * Microinteracciones táctiles (haptics) y efectos sonoros con debouncing.
   * Iconografía explícita ("✓" y "✕") para no depender únicamente del color.
   * Soporte completo para el modo de reducción de movimiento (`LocalAnimationsEnabled`).
   * Ajustes de Sonidos y Animaciones configurables (ON/OFF).

---

## 🔒 Privacidad y Funcionamiento 100% Offline

* **Sin conexión a Internet:** La aplicación NO solicita el permiso `android.permission.INTERNET`.
* **Sin servicios externos:** No utiliza Firebase, ni backend, ni APIs remotas, ni publicidad, ni compras in-app, ni recopilación de analíticas.
* **Permisos mínimos:** Únicamente `android.permission.VIBRATE` para respuesta táctil suave.
* **Privacidad total:** Todos los datos se almacenan exclusivamente en el dispositivo del usuario.

---

## 📦 Instrucciones para Generar el APK

### Método 1: Desde Android Studio (Recomendado)
1. Abre el proyecto en **Android Studio**.
2. Espera a que termine la sincronización de Gradle.
3. Para generar el **APK de Debug**:
   * Menú superior: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   * Ubicación generada: `app/build/outputs/apk/debug/app-debug.apk`.
4. Para generar el **APK de Release**:
   * Menú superior: **Build > Generate Signed Bundle / APK...**
   * Selecciona **APK** y haz clic en **Next**.
   * Selecciona o crea tu Keystore y pulsa **Next**.
   * Selecciona el build variant `release` y pulsa **Finish**.
   * Ubicación generada: `app/build/outputs/apk/release/app-release.apk`.

### Método 2: Por Línea de Comandos (Gradle Wrapper)
Si dispones de Gradle configurado en tu terminal:
```bash
# Generar APK de debug
./gradlew assembleDebug

# Generar APK de release
./gradlew assembleRelease
```
* Ruta del APK release: `app/build/outputs/apk/release/app-release.apk`
* Ruta del APK debug: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📲 Documentación de Instalación del APK en el Dispositivo

1. **Transferir el APK:** Copia el archivo `app-release.apk` (o `app-debug.apk`) a tu teléfono o tablet Android mediante cable USB, Google Drive local, o ejecútalo directamente desde Android Studio en un emulador/dispositivo conectado.
2. **Habilitar fuentes desconocidas:** Si Android lo solicita al abrir el archivo, activa la opción *"Permitir desde esta fuente"* en Ajustes de seguridad.
3. **Instalar:** Pulsa en **Instalar** y espera la confirmación.
4. **Abrir:** Pulsa en **Abrir** para iniciar MateKids.
5. **Comprobar Modo Avión:** Activa el modo avión del dispositivo y confirma que la aplicación funciona fluidamente con todas sus opciones, sonidos, gráficos y persistencia.
