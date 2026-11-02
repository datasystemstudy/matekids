# Política de Privacidad — MateKids: Juegos Matemáticos

**Última actualización:** 2 de noviembre de 2026  
**Desarrollador:** Ponce App Studios  
**Aplicación:** MateKids: Juegos Matemáticos  
**Identificador de paquete (Package ID):** `com.poncestudios.matekids`  
**Contacto de privacidad:** ponceappstudios@gmail.com  

---

## 1. Introducción y Compromiso con la Infancia

En **Ponce App Studios**, la privacidad y seguridad de los niños y sus familias es nuestra máxima prioridad. **MateKids** es una aplicación móvil educativa diseñada para que niños de 4 a 10 años aprendan y practiquen matemáticas (sumas, restas, multiplicación y división) a través de juegos interactivos y motivadores.

Esta Política de Privacidad describe de manera clara y transparente cómo tratamos la información en relación con el uso de **MateKids**. Cumplimos rigurosamente con:
- La Ley de Protección de la Privacidad Infantil en Internet (**COPPA** - *Children’s Online Privacy Protection Act* de EE. UU.).
- El Reglamento General de Protección de Datos (**GDPR / RGPD**, especialmente disposiciones de menores en Europa).
- La **Política de Familias de Google Play** (*Google Play Families Policy*).

---

## 2. No Recopilación de Información Personal

**MateKids NO recopila, NO almacena, NO solicita ni comparte ninguna información de identificación personal (PII) de los menores ni de sus padres.**

Específicamente:
- ❌ **No requiere registro ni cuentas:** El niño no necesita registrarse, iniciar sesión ni proporcionar nombres reales, correos electrónicos, números de teléfono ni contraseñas.
- ❌ **Sin acceso a datos sensibles:** La aplicación no solicita acceso a contactos, ubicación GPS, cámara, micrófono ni archivos personales del dispositivo.
- ❌ **Sin funciones de chat ni interacción social:** No existen chats, mensajes entre usuarios, foros ni ninguna función que permita a los niños comunicarse con terceros.

---

## 3. Datos Almacenados Localmente en el Dispositivo

Toda la progresión del juego se almacena **exclusivamente de manera local en el propio dispositivo del usuario** mediante la base de datos interna SQLite (Room Database) y Jetpack DataStore:

- Progreso en operaciones matemáticas (niveles completados, precisión de respuestas).
- Puntos, estrellas y monedas virtuales obtenidas en el juego.
- Juguetes virtuales desbloqueados en el baúl.
- Racha diaria de aprendizaje (*Daily Streak*).
- Preferencias de la aplicación (idioma seleccionado, volumen de efectos de sonido, música y animaciones).

> **Nota:** Ninguno de estos datos de juego es transmitido a servidores externos ni a Ponce App Studios. Si el usuario desinstala la aplicación o borra los datos de la app en los ajustes de Android, estos registros locales se eliminarán por completo.

---

## 4. Permisos Solicitados y su Uso

MateKids solicita únicamente los permisos técnicos indispensables para su funcionamiento en Android:

| Permiso | Justificación Técnica |
| :--- | :--- |
| `android.permission.INTERNET` | Requerido para la conexión del SDK de anuncios certificados para familias y reproducción multimedia. |
| `android.permission.ACCESS_NETWORK_STATE` | Permite verificar si el dispositivo cuenta con conexión de red activa antes de intentar cargar recursos. |
| `android.permission.VIBRATE` | Proporciona retroalimentación háptica (vibración suave) cuando el niño presiona botones y responde ejercicios correctamente. Puede desactivarse en los Ajustes de la app. |

---

## 5. Publicidad y Cumplimiento con la Política de Familias de Google Play

MateKids integra anuncios a través de **Google AdMob**, operando bajo estricto cumplimiento del programa de **Familias de Google Play** y la normativa **COPPA**:

- ✅ **Etiquetado para Menores (COPPA):** Las solicitudes de anuncios se envían con la directiva `tagForChildDirectedTreatment(true)` y `tagForUnderAgeOfConsent(true)`.
- ✅ **Anuncios No Personalizados:** Está deshabilitada toda publicidad basada en intereses, rastreo de comportamiento o perfiles de usuario. No se recopilan identificadores publicitarios con fines de seguimiento.
- ✅ **Clasificación de Contenido Estricta:** La clasificación máxima de contenido publicitario se restringe a **G** (*General Audiences* / Para todo público).
- ✅ **Sin Anuncios Engañosos ni Disruptivos:** Los anuncios no interfieren en medio de la resolución de un problema matemático ni obligan al menor a interactuar con ellos mediante trampas visuales.

---

## 6. Proveedores y Servicios de Terceros

Los servicios de terceros integrados en MateKids son exclusivamente aquellos certificados y aprobados por Google para aplicaciones familiares:

- **Google Mobile Ads SDK (AdMob):** Red publicitaria certificada en el programa de familias de Google Play.  
  [Política de Privacidad de Google](https://policies.google.com/privacy)

---

## 7. Derechos de los Padres y Tutores

Los padres o tutores legales tienen pleno control sobre la experiencia del menor:
1. **Control de audio y vibración:** Pueden silenciar sonidos, música o vibración en cualquier momento desde la pantalla de Ajustes de la app.
2. **Restablecimiento de datos:** Pueden reiniciar todo el progreso y puntuaciones de la app desde el botón *"Restablecer Progreso"* en los Ajustes.
3. **Eliminación definitiva:** Desinstalando la aplicación se eliminan todos los datos locales sin dejar rastro.
4. **Consultas o dudas:** Si algún padre, madre o tutor tiene preguntas sobre esta política, puede comunicarse directamente con nosotros.

---

## 8. Seguridad

Implementamos medidas de seguridad estándar en la industria para garantizar que los datos locales estén aislados dentro del espacio de seguridad (*sandbox*) del sistema operativo Android, impidiendo que otras aplicaciones instaladas en el dispositivo accedan a ellos.

---

## 9. Cambios a esta Política de Privacidad

Nos reservamos el derecho de actualizar esta Política de Privacidad para reflejar cambios en las leyes aplicables o mejoras en la aplicación. Cualquier cambio será publicado en este mismo documento con la fecha de *"Última actualización"* actualizada.

---

## 10. Contacto

Si tienes preguntas, dudas o comentarios acerca de esta Política de Privacidad o las prácticas de seguridad de MateKids, puedes ponerte en contacto con nuestro equipo de desarrollo:

- **Estudio:** Ponce App Studios
- **Correo Electrónico:** ponceappstudios@gmail.com
- **Aplicación:** MateKids: Juegos Matemáticos
- **País:** Perú
