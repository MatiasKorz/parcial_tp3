# PAN-3.4 — Voice / 04 — Speaking

## Referencia y alcance

- [Frame 1:1459](https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-1459), 390 × 844. Contexto y captura consultados por Figma MCP durante la planificación y reutilizados para implementar.
- Base main 12943cc, con PAN-3.3 integrada mediante PR #50. Rama feat/PAN-3.4-speaking.
- Estado simulado. Respuesta fija original: `"That sounds like a balanced lunch. I've estimated around 520 calories with approximately 38 grams of protein."`.
- Barra de reproducción y tiempos 0:08 / -0:14 fijos. Repetir, pausa y detener son controles visuales estáticos sin acción ni semántica de botón, conforme al alcance aprobado.
- No hay audio real, reconocimiento, grabación, micrófono, permisos, IA, Retrofit, consultas, temporizadores ni avance automático a Conversation.

## Diseño y reutilización

- Manrope y tokens BASE-01 existentes: fondo #123D35, blanco #FFFFFF, menta #65D6B0 y lima #C9E86A. Sin cambios al tema global, versiones o dependencias.
- Header: padding horizontal 20/vertical 12, chevron 14 dentro de círculo 32 con fondo blanco al 8 % y borde al 12 %, gap 10, título VORA Speaking SemiBold 15.
- Píldora SPEAKING: SemiBold 11 mint, fondo mint al 15 %, radio pill y padding horizontal 10/vertical 4.
- Contenido: padding superior 30/horizontal 24 y separación 32. Botón Interrupt a continuación del contenido con padding superior 16, horizontal 24 e inferior 24, sin anclarlo al fondo: se conserva la distribución vertical del frame.
- Orbe original nodo 1:1479: slot 140 × 140, glow 140, ripple 161, ring 126 y core 105. Los orbes de Listening y Processing tienen otras proporciones; se mantienen intactos.
- Export original Figma MCP download_assets: drawable-nodpi/voice_speaking_orb.png, 161 × 165, 27.181 bytes, copiado sin edición ni recomprensión. Incluye ondas y sombra; fondo opaco #123D35.
- El PNG se muestra a 161 × 165 dp dentro del slot 140, centrado horizontalmente y desplazado 2 dp respecto de su centro vertical: conserva la onda de 161 y el margen inferior extra de la sombra. No se usa la captura completa como recurso y no hay URLs temporales en la app.
- Respuesta Medium 16/24 blanca, tarjeta radio 20, padding 20, fondo blanco al 5 % y borde al 10 %; comillas y texto completo del diseño.
- Scrubber de alto 4, radio 2, track blanco al 15 % y fill mint. Proporción fija 180/342 del ancho disponible, igual al diseño de referencia y adaptable a otras anchuras.
- Gap 16 entre barra, tiempos y controles; tiempos Regular 11 blanco al 60 %.
- Controles visuales: repetir y detener, círculos 40 blancos al 8 % con iconos 18; pausa, círculo mint 56 e icono verde 20; separación 24.
- SVG originales de Figma: repeat-1 (1:1499), pause (1:1502), stop-circle (1:1505), convertidos a ic_voice_speaking_repeat/pause/stop en VectorDrawable conservando sus paths, viewports, colores y trazo 2 con extremos redondeados.
- Tap to Interrupt: fondo lima, texto Bold 15 verde, radio pill, padding horizontal 32/vertical 14, ancho disponible.
- Reutilización de icono ic_voice_idle_chevron_left, Manrope, estilos base, colores y VoraPillShape. Los componentes BASE-02 de píldora, tarjeta y VoraLimeButton tienen otras opacidades, tipografías o paddings; las variantes específicas quedan aisladas en Speaking sin modificar sus APIs.
- Pantalla desplazable, sin barra inferior de pestañas. Barras del sistema e insets nativos Android; sin marco decorativo, reloj ni home indicator iOS dibujados.
- Previews privadas 390 × 844 y 360 × 640/fuente 1,3.

## Navegación provisional

- Processing → Speaking permanece exactamente como en PAN-3.3: avance tras tres segundos activos y eliminación de Processing mediante popUpTo inclusive.
- Solo se reemplaza el placeholder de Speaking en AppNavGraph, agregando su import. Listening, Processing y AppDestination permanecen sin cambios.
- Tap to Interrupt: popBackStack(voice_listening, inclusive=false), elimina Speaking y cualquier entrada intermedia sin duplicar Listening.
- Chevron y BackHandler: popBackStack(voice_idle, inclusive=false), conserva Idle y elimina los estados posteriores.
- Salidas protegidas por una bandera remember de la entrada, estado RESUMED y coincidencia con currentBackStackEntry. La primera salida atendida gana; los siguientes toques se ignoran.
- La pantalla recibe callbacks, sin NavController ni ViewModel. La navegación utiliza únicamente rutas existentes.
- Conversation y Error continúan declaradas, sin implementar sus pantallas. No se añade salida automática a Conversation.

## Verificación y prueba manual

- Resultado local: BUILD SUCCESSFUL en 25 s; 114 tests aprobados, sin fallos, errores ni omitidos.
- git diff --check y revisión de whitespace en archivos nuevos: sin errores. Sin nombres de strings duplicados. Listening, Processing y AppDestination permanecen idénticos a origin/main.

- Comandos solicitados: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest` y `git diff --check`.
- Sin emuladores, ADB ni automatización visual. Los tests existentes no verifican render ni navegación Compose.
- Pendiente del integrante: comparar frame, proporciones y sombra del orbe, respuesta completa, barra fija, controles, botón, scroll y contraste de barras del sistema.
- Recorrido: Home → Start voice session → TAP TO TALK → micrófono activo → Processing → Speaking.
- Verificar Tap to Interrupt → Listening; repetir el recorrido y comprobar chevron y atrás del sistema → Idle. Probar toques rápidos y que no queden destinos duplicados.
- Dejar Speaking abierta: no debe avanzar, sonar, cambiar los tiempos ni mover la barra. Los tres controles son exclusivamente visuales; Tap to Interrupt es la acción visible.
- PRs abiertos #39, #40, #44 y #48 comparten navegación, strings o README: posibles conflictos posteriores de integración. No se incorporaron esas ramas.

## Uso de IA

Codex se utilizó para inspeccionar Figma MCP, obtener recursos originales, implementar Compose y navegación simulada, documentar recursos y decisiones, y ejecutar verificaciones locales. La comparación visual y la prueba funcional quedan a cargo del integrante. Sin commit, push ni PR en esta etapa.
