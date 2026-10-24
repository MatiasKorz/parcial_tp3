# PAN-3.2 — Voice / 02 — Listening

## Diseño y alcance

- Frame original [1:1372](https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-1372), 390 × 844. Contexto y captura obtenidos por Figma MCP durante la planificación y reutilizados en la implementación.
- Rama creada desde main 55f6e41, que integra PAN-4.7 mediante PR #32.
- Estado de demostración / simulado. La transcripción es fija: `"I had chicken, rice and some vegetables for lunch..."`.
- Sin micrófono real, permisos, grabación, reconocimiento, audio, IA remota, red, repositorios ni persistencia.
- PAN-3.1 y PAN-3.3 conservan sus implementaciones y rutas. Processing sigue siendo placeholder.

## Medidas y recursos

- Manrope y colores BASE-01 existentes: verde #123D35, blanco #FFFFFF y menta #65D6B0. Sin cambios al tema global, dependencias o versiones.
- Header: padding horizontal 20 y vertical 12; chevron 14 dentro de círculo 32, blanco al 8 % y borde al 12 %; título SemiBold 15. LIVE AUDIO SemiBold 12 y punto mint 8, separados por 6.
- Contenido: margen horizontal 24, padding superior 40 y separación vertical 40.
- Orbe: slot 170 × 170, onda exterior 195,5 × 195,5 centrada; ring 153 y core 128. `VoraListeningOrb` encapsula el recurso original en un componente con Modifier y preview, sin modificar el orbe de Idle.
- Nodo 1:1393 exportado por Figma MCP download_assets a PNG. Recurso local `app/src/main/res/drawable-nodpi/voice_listening_orb.png`, 196 × 196, 35.617 bytes. Se conserva sin edición ni recomprensión y se muestra a 195,5 dp, desbordando el slot de 170 como en Figma.
- El PNG exportado tiene fondo opaco #123D35: el componente está destinado a superficies de ese color. Incluye waveform, ripple, gradientes y sombra originales. No hay URLs temporales en la aplicación.
- Chevron y micrófono reutilizan `ic_voice_idle_chevron_left` e `ic_voice_idle_mic`, con tint blanco y verde respectivamente.
- LIVE TRANSCRIPT en mayúsculas SemiBold 12; tarjeta con radio 24, padding 20, separación 12, fondo blanco al 6 % y borde al 10 %; transcripción Medium 18 con interlineado 27 y comillas originales.
- Speak naturally. I'm listening.: Regular 13, blanco al 60 %, centrado.
- Micrófono mint 80 con icono 28. Cancel SemiBold 14 en mint, separación 20; footer con padding superior 16 e inferior 24.
- Se reutilizan fuentes, tokens e iconos existentes; los botones y la tarjeta de BASE-02 no ofrecen los estilos exactos de este frame sin cambios a su API. Las variantes visuales se limitan a Listening.
- Barras del sistema nativas Android e insets; no se dibujan reloj, indicadores iOS ni marco decorativo del mockup. Iconos claros durante Listening, con restauración al salir.
- Scroll en el contenido central para pantallas pequeñas o fuentes ampliadas; micrófono y Cancel permanecen visibles. Sin barra inferior de pestañas. Previews 390 × 844 y 360 × 640 con fuente 1,3.

## Navegación provisional

- Se conserva el callback de TAP TO TALK de Idle hacia `voice_listening`.
- Solo se sustituye el placeholder de Listening por `VoiceListeningScreen` en AppNavGraph. La pantalla recibe callbacks y no conoce NavController ni ViewModel.
- Micrófono activo: navigate a la ruta existente `voice_processing`, sin temporizadores ni procesamiento real.
- Cancel, chevron y atrás del sistema (BackHandler) comparten la acción popBackStack a `voice_idle`, no inclusive: se conserva Idle sin apilar copias.
- Las acciones se atienden únicamente si la entrada Listening está RESUMED, siguiendo la protección existente contra dobles toques.
- Se conservan las salidas actuales del placeholder Processing, incluida su acción de cerrar el coach; no se cambia PAN-3.3.

## Verificación y prueba manual

- Resultado: BUILD SUCCESSFUL en 38 s. 31 tests unitarios, sin fallos, errores ni omitidos.
- git diff --check y revisión de whitespace de los tres archivos de texto nuevos: sin errores.

- Comandos: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest` y `git diff --check`.
- Sin emuladores, ADB ni automatización visual. Los tests unitarios existentes no prueban render ni navegación Compose.
- Pendiente del integrante: comparar contra el frame en Android Studio, revisar orbe, tipografía, barras del sistema y scroll con fuente ampliada.
- Probar Home → Start voice session → TAP TO TALK → Listening; por separado Cancel, chevron y atrás del sistema deben volver a Idle. Repetir la entrada y comprobar que no se duplican destinos.
- Tocar el micrófono debe abrir Processing provisional. Revisar que al salir de Listening las barras del sistema recuperen el contraste correspondiente.
- Varios PRs abiertos comparten AppNavGraph, strings.xml y README: puede haber conflictos de integración. No se incorporaron ramas pendientes.

## Uso de IA

Codex se utilizó para inspeccionar Figma mediante MCP, reutilizar y descargar recursos originales, implementar la pantalla y su navegación simulada, documentar las decisiones y ejecutar verificaciones locales. La comparación visual y la prueba funcional corresponden al integrante. No se realizaron commits, pushes ni PRs.
