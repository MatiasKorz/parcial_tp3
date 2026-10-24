# PAN-3.6 — Voice / 06 — Error

## Referencia y alcance

- [Frame 1:1579](https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-1579), 390 × 844. Contexto y captura obtenidos por Figma MCP durante la planificación y reutilizados para implementar.
- Rama feat/PAN-3.6-error desde main 0e9b0fb: incluye PAN-3.5 (PR #52), dashboard de nutrición (PR #48) y Food Analysis (PR #44). Se preservan esas incorporaciones.
- Todo es simulado: no se comprueba el estado del micrófono ni se solicitan permisos reales. Sin reconocimiento, síntesis, grabación, IA, Retrofit, Room para el coach ni detección real de fallos.
- La tarjeta de permisos reproduce el texto del diseño; no implica que la app de demostración esté grabando o escuchando.

## Acceso de demostración aprobado

1. Home → Start voice session → TAP TO TALK para abrir Listening.
2. Mantener presionado el micrófono activo de Listening abre Voice Error.
3. Un toque corto del mismo control sigue abriendo Processing → Speaking normalmente.

- Solo se cambia el callback y el control del micrófono de Listening, sus imports y los argumentos de preview necesarios. No cambia su aspecto, textos visibles, medidas ni las otras acciones.
- combinedClickable gestiona el reconocimiento de gestos: una pulsación prolongada atendida no ejecuta también onClick al soltar. No se superponen detectores de toque independientes.
- Semántica de botón con onClickLabel existente Finish simulated listening y onLongClickLabel Simulate voice error. TalkBack puede exponer ambas acciones, incluida la demostración, sin añadir botones visibles al Figma.
- El destino que solicita Error debe estar RESUMED y ser currentBackStackEntry. launchSingleTop evita duplicar Error. El flujo normal no depende de fallos reales, temporizadores adicionales ni permisos.
- El gesto no es evidente visualmente; queda documentado en este archivo y en el inventario README como entrada de demostración.

## Diseño y recursos

- Manrope y tokens BASE-01 existentes: fondo #F7F8F5, verde #123D35, blanco #FFFFFF, menta #65D6B0, borde #E4E9E6 y texto primario/secundario.
- Header: padding horizontal 20/vertical 12, círculo blanco 32 con borde 1, chevron 14 existente, gap 10 y System Alert SemiBold 15.
- Contenido desplazable: padding horizontal 24/superior 30, separación principal 32. Pie separado con padding superior 16/inferior 24 y gap 16, sin barra inferior de pestañas.
- Orbe original gris, nodo 1:1597: 120 × 120 con icono central blanco original. Export PNG por Figma MCP download_assets, drawable-nodpi/voice_error_orb.png, sin redibujar, editar píxeles ni recomprimir; se muestra a 120 dp.
- Título Sorry, I didn't catch that: Bold 22 centrado, sin punto final según el frame. Descripción exacta Regular 14, gap 12 y color secundario.
- Tarjeta de permisos: blanca, radio 20, borde 1, padding 20, gap 16. Icono de micrófono 18 en círculo rosa #FCDEDE de 36; gap 10 y título Microphone access needed SemiBold 15.
- El SVG de micrófono del frame tiene el mismo path y viewport que ic_voice_conversation_mic: se reutiliza ese recurso con tint MaterialTheme.colorScheme.error (#D95D5D).
- El rosa exclusivo se define en VoiceErrorStyles.kt mediante una extensión de ColorScheme; no se cambia la paleta global ni el tema.
- Texto explicativo original Regular 13/18,2. Allow Access Bold 13, fondo verde, texto blanco y padding horizontal 16/vertical 10. Not Now SemiBold 13, texto secundario, borde existente y el mismo padding. Ambos comparten el ancho disponible, separados por 12; pueden envolver texto con fuentes ampliadas.
- Try again Bold 15, fondo mint, texto verde, ancho disponible y padding horizontal 32/vertical 14.
- Type instead reutiliza VoraTextAction muted: SemiBold 13, secundario y subrayado. Se reutilizan Manrope, tokens, iconos y VoraPillShape. La tarjeta y botones específicos se aíslan en Error porque los componentes base no ofrecen esos paddings y variantes exactas sin cambiar su API.
- Barras del sistema Android e insets nativos, iconos oscuros para este fondo. No se dibujan reloj, indicadores iOS, home indicator ni marco decorativo.
- Previews privadas 390 × 844 y 360 × 640/fuente 1,3.

## Botones y pila de navegación

- VoiceErrorScreen recibe callbacks y no conoce NavController ni ViewModels. El aviso usa SnackbarHostState y un scope local de composición.
- Allow Access muestra un Snackbar con Microphone access is simulated in this demo. No navega ni solicita permisos. Cada pulsación cancela el Job anterior y vuelve a mostrar el aviso, sin acumular mensajes. El host se superpone por encima del pie sin cambiar el layout del Figma; Try again y Type instead siguen accesibles. Salir de la pantalla cancela el scope y el aviso pendiente. Se reemplazó el AlertDialog original tras la prueba manual del integrante.
- Try again: popBackStack(voice_listening, false), elimina Error y reutiliza Listening existente.
- Type instead: si Conversation ya existe debajo (Conversation → Listening → Error), popBackStack a esa entrada conserva el hilo local. Si no existe, navigate Conversation con popUpTo Idle no inclusive y launchSingleTop: elimina Error/Listening y deja Idle → Conversation, sin duplicados.
- Not Now, chevron y BackHandler comparten popBackStack(voice_idle, false): conservan Idle sin apilar otra instancia.
- Salidas protegidas por RESUMED y coincidencia con currentBackStackEntry. Tras la primera navegación, toques posteriores no actúan sobre el destino que salió.
- El resto de bloques de navegación y todas las rutas permanecen intactos. No se modifican Idle, Processing, Speaking ni Conversation.
- PRs abiertos #39 y #40 comparten navegación, strings y README: posibles conflictos posteriores. No se incorporaron esas ramas.

## Verificación y prueba manual

- Resultado local: BUILD SUCCESSFUL en 19 s tras corregir Allow Access; 127 tests aprobados, sin fallos, errores ni omitidos.
- git diff --check y revisión de whitespace en archivos nuevos: sin errores. Sin nombres de strings duplicados. Idle, Processing, Speaking, Conversation, AppDestination y manifest permanecen idénticos a origin/main.

- Comandos: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest` y `git diff --check`.
- Sin emuladores, ADB ni automatización visual. Los tests unitarios existentes no verifican reconocimiento de gestos, semántica TalkBack, render ni navegación Compose.
- Pendiente manual: comparar el frame, scroll y fuentes ampliadas; comprobar orbe, texto completo de permisos, filas de botones y contraste de barras del sistema.
- Toque corto del micrófono → Processing → Speaking. Mantener presionado → Error y, al soltar, debe permanecer en Error sin abrir Processing. Repetir con toques rápidos y verificar una sola navegación.
- Probar TalkBack: acción normal y Simulate voice error en el mismo control. La prueba de semántica accesible es manual en esta etapa.
- Desde Error probar Try again → Listening; Type instead → Conversation; Not Now, chevron y atrás del sistema → Idle. Allow Access debe mostrar solo el aviso, sin diálogo de permisos Android.
- Probar también Conversation con un mensaje local → micrófono → pulsación prolongada en Listening → Error → Type instead: el hilo anterior debe conservarse.

## Uso de IA

Codex se utilizó para inspeccionar Figma MCP, obtener el recurso original, implementar Compose y el acceso simulado por pulsación prolongada, conectar las rutas existentes, documentar decisiones y ejecutar verificaciones locales. La comparación visual, prueba de gestos y accesibilidad quedan a cargo del integrante. Sin commit, push ni PR en esta etapa.
