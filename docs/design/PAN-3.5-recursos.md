# PAN-3.5 — Voice / 05 — Conversation

## Referencia y alcance

- [Frame 1:1512](https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-1512), 390 × 844. Contexto y captura consultados por Figma MCP durante la planificación y reutilizados en la implementación.
- Rama feat/PAN-3.5-conversation desde main 42d9a66, con PAN-3.4 integrada por PR #51.
- El hilo inicial reproduce exclusivamente los cuatro mensajes del Figma y sus horarios 12:41, 12:42 y 12:43, precedidos por TODAY AT 12:40 PM.
- No se consultan servicios del coach ni se generan respuestas de IA. Sin Retrofit, Room, Firebase, audio, grabación, permisos de micrófono, historial persistido ni adjuntos.
- Los mensajes nuevos son únicamente texto del usuario; no se inventan respuestas ni horarios para ellos. El enlace Listen abre Speaking con su respuesta fija ya existente (sobre el almuerzo), sin generar ni reproducir audio contextual.

## Diseño y componentes

- Tokens BASE-01 y Manrope existentes: fondo #F7F8F5, verde #123D35, blanco #FFFFFF, borde #E4E9E6 y textos #17211F/#68736F/#9AA39F.
- Header blanco con borde inferior 1, padding horizontal 20/vertical 12, chevron existente 14 en círculo 32, separación 10, VORA Coach SemiBold 15, punto mint 6 y orbe 28 a la derecha.
- Thread: padding 20, separación 20. Fecha SemiBold 11 centrada en muted. Mensajes de usuario Regular 14, blanco sobre verde, ancho máximo 280, padding 14 y radio 16 salvo esquina inferior derecha 4. Gap 4 y horario Regular 10 blanco al 60 %, alineado a la derecha.
- Mensajes VORA: avatar 28 y separación 10; burbuja blanca con borde 1, ancho máximo 270, padding 14 y radio 16 salvo esquina inferior izquierda 4. Texto Regular 14/19,6; horario Regular 10 muted.
- Primera respuesta con separación 10, divisor y Listen to response (0:14) SemiBold 12, icono play 10 en círculo 24 sobre fondo de la app; toda la fila es pulsable.
- Panel blanco separado del hilo, borde superior 1, padding horizontal 16/vertical 12, gap 12. Botones + y enviar en círculos 40, iconos 20 y 18. Campo de altura mínima 44, radio 22, padding horizontal 16, texto/placeholder Regular 14, micrófono 18 muted.
- Burbujas, campo y panel son componentes privados de Conversation: VoraCard y VoraTextField no expresan las esquinas asimétricas y el compositor del diseño sin modificar sus APIs. Se reutilizan el tema, tokens, estilos e icono ic_voice_idle_chevron_left; sin cambios al tema global ni BASE-02.
- Los iconos play, plus, mic y send se convierten de los SVG originales del contexto de Figma a ic_voice_conversation_* conservando paths, viewport, trazo 2 y extremos redondeados. Las variantes existentes de micrófono y plus tienen otras proporciones de trazo.
- Orbe original nodo 1:1530, compartido por header y dos avatares: export transparente PNG mediante exportAsync contentsOnly, sin modificar nodos de Figma, sin edición de píxeles ni recomprensión. Se reemplazó el primer export, que incluía fondo blanco.
- Recurso final drawable-nodpi/voice_conversation_orb.png, 69 × 69 RGBA, alfa 0..255. Slot 28 × 28; bounds originales x=-20,5/y=-12,5. Image a 69 × 69 centrada horizontalmente y offset vertical 8 preserva el orbe y su sombra sobre ambas superficies.
- No hay URLs temporales en la aplicación ni se usa una captura de pantalla completa como recurso.
- Barras del sistema nativas y sin barra inferior. Conversation aplica iconos oscuros al entrar; no restaura ese valor al salir para evitar sobrescribir el estilo de las pantallas Voice oscuras entrantes.
- Previews privadas 390 × 844 y 360 × 640/fuente 1,3.

## Mensajes, scroll y teclado

- Estado local Compose: borrador, lista de strings del usuario y aviso de adjuntos con rememberSaveable. Se conserva ante recreación de Activity/estado de navegación; no se guarda un historial en disco ni se consulta una base. Al salir definitivamente de Conversation, una nueva entrada comienza con el hilo original.
- Enviar o la acción Send del teclado comprueban isNotBlank, añaden el texto recortando espacios exteriores y limpian el campo. Una entrada vacía o de espacios no añade mensajes. No hay respuesta automática.
- LazyColumn con claves únicas para fecha, cuatro mensajes iniciales y cada mensaje agregado (prefijo local_user e índice append-only). LazyListState y LaunchedEffect dependiente de la cantidad de mensajes desplazan al último mensaje tras enviar.
- Header y panel fuera de la lista; la lista ocupa el espacio restante con weight. No hay scroll vertical anidado.
- La raíz maneja statusBarsPadding e imePadding, y el panel blanco navigationBarsPadding; los insets consumidos evitan duplicar la barra nativa. El manifest ya usa adjustResize y permanece intacto.
- El campo permite editar texto y hasta tres líneas visibles; el panel crece con el contenido y el resto del hilo continúa desplazable. El teclado permanece abierto al enviar.
- + muestra el aviso inglés solicitado: Demonstration / Attachments are not available in this demonstration. / OK. Estos textos adicionales están limitados al aviso y a etiquetas de accesibilidad; el hilo inicial no incorpora mensajes inventados.

## Navegación provisional

- Solo se reemplaza el placeholder de Conversation en AppNavGraph y se añade su import. Idle, Listening, Processing, Speaking y AppDestination permanecen intactos.
- Se conservan las entradas desde Idle: Type instead, LAST CONVERSATION y sugerencias.
- Chevron y BackHandler → popBackStack a voice_idle, no inclusive, sin duplicar Idle.
- Micrófono → navigate voice_listening con launchSingleTop. No se solicita permiso.
- Listen → se apilan Listening y Speaking en la misma acción, ambas con launchSingleTop; no se ejecuta el temporizador de Processing. Así Tap to Interrupt de Speaking encuentra Listening debajo y vuelve correctamente sin modificar PAN-3.4.
- Desde las entradas actuales, Conversation se abre directamente sobre Idle; por tanto este recorrido no crea otra Listening preexistente. Idle → Conversation → Listening → Speaking es la pila resultante. Volver desde Speaking a Idle elimina los estados posteriores según PAN-3.4.
- Las acciones solo se atienden mientras Conversation está RESUMED y es currentBackStackEntry. La primera navegación cambia inmediatamente la entrada activa y bloquea toques posteriores.
- PRs abiertos #39, #40, #44 y #48 comparten navegación, strings o README: pueden requerir integración posterior. No se incorporaron esas ramas.

## Verificación y prueba manual

- Resultado final local: BUILD SUCCESSFUL en 19 s; 114 tests aprobados, sin fallos, errores ni omitidos.
- git diff --check y revisión de whitespace en archivos nuevos: sin errores. Sin nombres de strings duplicados. Idle, Listening, Processing, Speaking y AppDestination permanecen idénticos a origin/main.

- Comandos solicitados: `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest` y `git diff --check`.
- Sin emuladores, ADB ni automatización visual. Los tests existentes no verifican teclado, render ni navegación Compose.
- Pendiente: comparar el frame, los cuatro textos completos, horarios, avatares transparentes y burbujas en Android Studio.
- Enviar texto, texto editado, mensajes largos y varios consecutivos; verificar limpieza y scroll. Enviar vacío/espacios no debe crear mensajes ni respuestas de IA.
- Abrir teclado y comprobar el panel, envío por teclado, scroll, fuente ampliada y dimensiones pequeñas.
- Probar + y cerrar el aviso; micrófono → Listening; Listen → Speaking → Tap to Interrupt → Listening; chevron y atrás del sistema → Idle, sin dobles navegaciones.

## Uso de IA

Codex se utilizó para inspeccionar Figma MCP, obtener recursos originales con transparencia, implementar Compose y estado local, conectar las rutas existentes, documentar decisiones y ejecutar verificaciones locales. La comparación visual y la prueba funcional quedan a cargo del integrante. Sin commit, push ni PR en esta etapa.
