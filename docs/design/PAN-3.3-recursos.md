# PAN-3.3 — Voice / 03 — Processing

## Referencia y alcance

- [Frame 1:1419](https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-1419), 390 × 844. Contexto y captura obtenidos por Figma MCP durante la planificación y reutilizados para implementar.
- Base main 937aa75, con PAN-3.2 integrada mediante PR #49. Rama feat/PAN-3.3-processing.
- Demostración / simulado: transcripción fija `"I had chicken, rice and some vegetables for lunch."`.
- Checking your nutrition target... no consulta servicios ni representa una verificación real. Sin voz, audio, grabación, permisos de micrófono, IA remota, Retrofit, repositorios ni persistencia.
- El avance automático tras unos tres segundos activos es una decisión de demostración aprobada por el integrante. Speaking continúa siendo el placeholder de PAN-3.4.

## Diseño y recursos

- Manrope y tokens BASE-01 existentes: fondo #123D35, blanco #FFFFFF y menta #65D6B0. Sin cambios al tema global ni a dependencias.
- Header: padding horizontal 20/vertical 12; chevron 14 en círculo 32, fondo blanco al 8 % y borde al 12 %; separación 10 y título SemiBold 15.
- Contenido: padding horizontal 24/superior 50, separación principal 40.
- Orbe original de Processing, nodo 1:1437: slot 150 × 150, glow 150, ring 135 y core 113, sin ripple exterior. No se reutiliza VoraListeningOrb porque su forma y dimensiones no coinciden.
- Export PNG original por Figma MCP download_assets, sin edición ni recomprensión: drawable-nodpi/voice_processing_orb.png, 161 × 164, 26.653 bytes. El export incluye el área de sombra fuera del slot; se muestra a su tamaño natural centrado horizontalmente y alineado al borde superior del slot de 150 (offset vertical 7 respecto del centro). Se conservan gradientes, waveform y sombra.
- El PNG tiene fondo opaco #123D35, coincidente con esta superficie. No se usa la captura de pantalla completa como recurso ni quedan URLs temporales en la app.
- THINKING... en mayúsculas, SemiBold 14 mint, separación 16 respecto de la burbuja.
- Burbuja: ancho disponible, radio 16, padding 18, fondo blanco al 4 % y borde al 8 %. Transcripción Regular 15/22,5, blanco al 90 %, con comillas y punto final del diseño.
- loader-circle original de Figma, nodo 1:1452, convertido a VectorDrawable ic_voice_processing_loader conservando path, viewport 16 × 16, trazo 2 y extremos redondeados. Indicador estático, como la referencia visual, junto a texto Regular 13 blanco al 60 %, separación 8.
- Chevron reutilizado: ic_voice_idle_chevron_left, tint blanco. Cancel: SemiBold 14 blanco al 60 %, padding superior 16/inferior 24.
- Layout Compose con contenido central desplazable y Cancel visible en el pie; sin barra inferior de pestañas. Barras del sistema nativas e insets Android; sin reloj, home indicator ni marco decorativo iOS dibujados.
- Previews privadas 390 × 844 y 360 × 640/fuente 1,3. Los estilos exclusivos quedan en Processing, sin modificar Listening, Speaking ni componentes de otros tickets.

## Navegación y ciclo de vida

- Se conserva Listening → Processing mediante la ruta existente voice_processing. Solo se reemplaza el bloque de este destino y se agregan sus imports en AppNavGraph.
- La pantalla recibe onBack y onCancel; no conoce NavController, ViewModel ni el temporizador.
- LaunchedEffect(entry) ejecuta repeatOnLifecycle(RESUMED) con delay(3_000). Solo cuenta mientras esa entrada está activa. Ir a segundo plano o salir del destino cancela la espera; volver antes de finalizar inicia tres segundos nuevos. La recomposición normal no reinicia el efecto.
- Cancel vuelve a voice_idle con popBackStack no inclusive, quitando Listening y Processing sin duplicar Idle.
- Chevron y BackHandler vuelven a voice_listening con popBackStack no inclusive.
- Al vencer la espera, navigate a voice_speaking con popUpTo(voice_processing) inclusive y launchSingleTop. Processing sale de la pila; Listening permanece debajo de Speaking.
- Una bandera remember por entrada y las comprobaciones RESUMED/currentBackStackEntry protegen todas las salidas: la primera acción atendida gana y las siguientes se ignoran. Cancel y Atrás permanecen disponibles durante la espera, sin bloqueo del hilo principal.
- Todas las rutas de otros tickets, incluido VoiceError, permanecen declaradas; no se implementan Speaking ni el escenario de error.

## Verificación y prueba manual

- Resultado local: BUILD SUCCESSFUL en 25 s; 114 tests aprobados, sin fallos, errores ni omitidos.
- git diff --check y revisión de whitespace en archivos nuevos: sin errores. Sin nombres de strings duplicados. Listening, VoraListeningOrb y AppDestination permanecen idénticos a origin/main.

- Comandos solicitados: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest` y `git diff --check`.
- Sin emuladores, ADB ni automatización visual. Los tests unitarios existentes no verifican navegación Compose ni efectos visuales.
- Pendiente manual: comparar el frame, orbe/sombra, textos, scroll y barras del sistema; comprobar el avance tras aproximadamente tres segundos.
- Probar Cancel antes del plazo → Idle, chevron y atrás del sistema → Listening, y toques rápidos para verificar una única salida.
- Enviar la app a segundo plano antes de tres segundos y esperar: no debe navegar en segundo plano. Al regresar debe esperar tres segundos nuevos. Salir con Cancel/Atrás y esperar tampoco debe provocar una transición posterior.
- Al llegar a Speaking, atrás del sistema no debe reabrir Processing. La navegación propia del placeholder Speaking se mantiene sin cambios.
- Los PRs abiertos #39, #40, #44 y #48 comparten navegación, strings o README: pueden requerir integración posterior. No se incorporaron esas ramas.

## Uso de IA

Codex se utilizó para consultar Figma MCP, obtener el orbe y el indicador originales, implementar Compose y navegación simulada vinculada al ciclo de vida, preparar documentación y ejecutar las verificaciones locales. La comparación visual y la prueba funcional quedan a cargo del integrante. Sin commit, push ni PR en esta etapa.
