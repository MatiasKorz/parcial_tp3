# PAN-4.4 — Pre Workout

## Referencia y alcance
- Frame 1:510, Workout / 04 — Pre Workout, 390 × 844.
- https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-510
- Base main 9abe78f, con PAN-4.3 fusionado (PR #22).
- Estructura y contexto de diseño/captura consultados mediante Figma MCP durante la planificación; sin consultas repetidas para implementar.
- Interfaz de demostración: título, enfoque, sets previos y consejo son los textos originales del Figma, no datos del entrenamiento recibido ni historial real.
- Sin Retrofit, Room, ViewModels, repositorios, endpoints ni recomendaciones remotas.

## Diseño y reutilización
- Manrope; fondo primary #123D35, acentos secondary #65D6B0 y tertiary #C9E86A.
- Margen horizontal 20; padding superior 12 e inferior 24; separación principal 20.
- Título 26 sp/36; resumen 14 sp/19. Cabecera GET READY con close en espacio de 24.
- Tres tarjetas: radio 16, padding 16, separación interna 8, separación entre tarjetas 12.
- Fondos de tarjeta blancos al 4 %, bordes blancos al 12 %, texto secundario blanco al 70 %.
- TODAY'S FOCUS: Controlled Repetitions y texto completo.
- YOUR LAST SESSION: Bench Press Volume y los tres sets originales.
- VORA'S COACH TIP: texto entre comillas original.
- Bloque VORA: fondo blanco al 8 %, padding 16, separación 12, orbe a 32 y micrófono a 20 dentro de botón lima de 40.
- Start Workout: VoraPrimaryButton Large, tipografía de 16 sp/22 y padding vertical 14 (alto de referencia 50).
- Se configura el color y la tipografía del botón solo en su subárbol para obtener menta con texto verde. No se modifica el tema global ni la API compartida.
- Las tarjetas son variantes privadas con formas/tokens BASE-01: los componentes existentes no permiten expresar sus opacidades y separación exactas sin cambiar su API.
- Una sola LazyColumn, claves estables, scroll y alturas que crecen con fuente o texto. Sin barra inferior por la ruta existente.
- Íconos claros de barras del sistema solo durante este destino; se restauran los valores anteriores al salir. Sin modificar MainActivity ni otras pantallas.
- Previews 390 × 844 y 360 × 640/fuente 1.3; no equivalen a pruebas visuales en dispositivo.

## Recursos originales
- Orbe reutilizado: ic_home_orb.xml, colocado a 32 dp. Conserva la limitación conocida de sombra suave omitida en VectorDrawable.
- Originales guardados fuera del repo en C:\dev\vora-referencias\PAN-4.4-originales.
- close.svg: nodo 1:523, export 8c2ae.svg. Convertido a ic_pre_workout_close.xml; viewport 24 × 24, blanco, trazo 2, extremos redondos y clip originales conservados.
- microphone.svg: nodo 1:549, export 9d45a.svg. Convertido a ic_pre_workout_mic.xml; viewport 20 × 20, #123D35, trazo 2. El micrófono de Home tiene geometría distinta y no se sustituye por él.
- Prefijo de exportación MCP 572dbb7c-c0e3-418c-ba6e-c61712541653; descargados con curl. No hay URLs temporales en el código.
- Los SVG se validan como XML antes de convertir. Trazados, viewports, colores, opacidad y terminaciones se conservan; sin filtros ni conversiones de raster.
- Close mantiene el tamaño/desplazamiento de su trazado original dentro del viewport de 24.

## Navegación y comportamiento
- Se reemplaza únicamente el placeholder de PreWorkout en AppNavGraph.
- Recibe workoutId mediante NavArgs.WORKOUT_ID y lo conserva en ActiveWorkout.createRoute(workoutId).
- Close usa popBackStack; Atrás del sistema conserva el comportamiento NAV-01 y regresa al origen.
- El texto de recomendación y el micrófono muestran el mismo aviso en inglés, sin realizar acciones de voz o red.
- Eventos de navegación protegidos con isResumed.
- La UI recibe callbacks; no conoce NavController ni datos remotos.
- Active Workout continúa siendo el destino existente; no se cambia su implementación ni Details, Dashboard o Weekly Plan.

## Decisiones y diferencias pendientes
- Preparación con contenido fijo de demostración; conectar contenido real e historial requiere una decisión/ticket del núcleo.
- No se reproducen el marco decorativo verde, las señales iOS ni el reloj del frame: se utilizan barras nativas Android.
- Los textos largos pueden envolver y aumentar el alto de tarjetas en anchos menores; el contenido sigue siendo desplazable.
- La fidelidad visual, los colores de barras del sistema al entrar/salir y todos los controles requieren revisión manual del integrante.

## Verificación
- assembleDebug y testDebugUnitTest: BUILD SUCCESSFUL en 27 s.
- 8 tests aprobados; 0 fallos, 0 errores y 0 omitidos.
- git diff --check sin errores; también se revisan los cinco archivos nuevos con --no-index --check.
- SVG y VectorDrawable: XML válido; trazados, colores, ancho de trazo, terminaciones y viewports comprobados contra los originales.
- No se ejecutan emuladores, ADB ni automatización visual.
- Pendiente manual: entrar desde Home/Dashboard/Details según rutas existentes, close y Atrás hacia el origen, Start Workout, conservación del ID, ambos avisos, scroll y adaptación a tamaños.

## Uso de IA
Codex se utilizó para consultar Figma MCP, implementar la interfaz Compose con estilos aislados, convertir recursos originales, integrar rutas existentes y preparar documentación y verificaciones locales. La revisión visual y funcional manual corresponde al integrante.
