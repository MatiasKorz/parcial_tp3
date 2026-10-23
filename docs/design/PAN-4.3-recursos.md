# PAN-4.3 — Workout Details

## Referencias y alcance
- Frame oficial 1:424, Workout / 03 — Workout Details, 390 × 844.
- https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-424
- Base: main, 2427681, con PAN-4.2 fusionado (PR #20).
- Consulta puntual get_design_context del frame para estilos, assets y captura, reutilizando la estructura recuperada.
- Interfaz de demostración: siempre muestra Upper Body Strength y los cinco ejercicios originales del Figma. No representa aún el detalle real del workoutId recibido.
- NUC-01 (PR #18) no incorporado. NUC-02 y NUC-03 no disponibles en la base utilizada; no se agrega lógica provisional de datos.

## Diseño y recursos
- Manrope y tokens BASE-01: fondo #F7F8F5; primary #123D35; superficie blanca, borde #E4E9E6 y textos secundarios #68736F/#9AA39F.
- Margen horizontal 20; padding superior 12 e inferior 24. Separación principal 16.
- Cabecera de 24, título de 26 sp/36, metadatos de 13 sp.
- Nota VORA: radio 12, padding 12, separación 8, fondo mintSoft y orbe existente a 16.
- Cinco filas: padding 12, radio 12, separación 10; número en caja de 32 con radio 8; nombre de 15 sp y prescripción de 12 sp.
- Botón principal VoraPrimaryButton con tipografía de acción limitada a su subárbol; no se cambia el tema global ni su API.
- Botón secundario con Surface y tokens compartidos porque VoraOutlinedButton está diseñado para paneles oscuros.
- Una única LazyColumn con claves estables; metadatos con FlowRow para ancho reducido y filas que crecen con el texto.
- Sin barra inferior: se conserva la definición existente de AppDestination.WorkoutDetails sin pestaña.
- Previews 390 × 844 y 360 × 640/fuente 1.3. No equivalen a pruebas de dispositivo.

### Procedencia de íconos
- Originales descargados con curl en C:\dev\vora-referencias\PAN-4.3-originales.
- chevron-left.svg: nodo 1:436, export 854c5.svg; VectorDrawable ic_workout_details_back.xml, viewport 24 × 24.
- more-horizontal.svg: nodo 1:440, export da3dd.svg; ic_workout_details_more.xml, viewport 100 × 100.
- chevron-right.svg: nodo 1:467 (repetido en filas), export ab09c.svg; ic_workout_details_chevron.xml, viewport 100 × 100.
- Exportados desde el prefijo MCP 628e0a6e-a96a-4974-adae-bfc1d82bd018. No se usan enlaces temporales en ejecución.
- SVG validados como XML y convertidos conservando pathData, colores, strokeWidth, extremos redondos y viewports; clip de 24 × 24 conservado para Volver.
- Tamaños Android: Volver y menú 24 dp; chevron derecho 16 dp. Los SVG de 100 se escalan completos en esos espacios.
- Reutilizado ic_home_orb.xml; sin cambiar el recurso compartido.

## Comportamiento e IDs
- Volver llama popBackStack; Atrás del sistema conserva el back stack de NAV-01 y vuelve al origen.
- Start Workout crea la ruta PreWorkout con el mismo workoutId recibido, sin sustituirlo por demo-workout.
- Las cinco filas abren ExerciseDetails con DEMO_EXERCISE_ID ya existente. Su asociación con los IDs reales de cada ejercicio queda pendiente.
- Edit workout y menú muestran avisos de demostración en inglés, sin crear pantallas ni cambiar datos.
- Eventos de navegación protegidos por el estado RESUMED de la entrada.
- La UI solo recibe callbacks; no recibe NavController ni ViewModel.

## Integración pendiente
- NUC-02 debe aportar el detalle del workoutId, los ejercicios asociados y sus IDs, repositorio y caché.
- NUC-03 debe aportar loading, empty/error, retry y offline/contenido previo según la decisión del núcleo.
- Al conectar esos estados, reemplazar los datos Figma de presentación por el entrenamiento recibido y pasar el ID propio de cada ejercicio al callback.
- No se agregan Retrofit, Room, ViewModels, repositorios, endpoints ni modelos de negocio provisionales.
- Coordinar con NUC-02 la composición del destino Details en AppNavGraph; no se modifican Dashboard ni Weekly Plan.

## Diferencias y decisiones provisionales
- En el Figma, menú y chevrons derechos tienen contenidos de 100 × 100 recortados dentro de cajas de 24/16; por eso no aparecen en la captura. Se muestran los mismos trazados originales escalados al espacio disponible para ofrecer controles visibles. No se redibujan.
- Volver conserva el trazado pequeño y desplazado del SVG original de 24 × 24.
- El orbe compartido conserva la limitación conocida de sombra desenfocada omitida en VectorDrawable.
- Barras del sistema nativas de Android; no se dibujan el marco verde ni la barra iOS de presentación.
- El botón secundario conserva el área táctil mínima Material, que puede agregar espacio respecto de los 44 del frame.
- Fidelidad visual, navegación y controles pendientes de la prueba manual del integrante.

## Verificación
- assembleDebug y testDebugUnitTest: BUILD SUCCESSFUL (verificación final 20 s).
- 8 tests aprobados; 0 fallos, 0 errores, 0 omitidos.
- git diff --check sin errores; también se revisan los seis archivos nuevos con --no-index --check.
- SVG/VectorDrawable: XML válido; trazados, colores, ancho de trazo, extremos y viewports comprobados contra los originales.
- No se ejecutan emuladores, ADB ni automatización visual.
- Comprobaciones manuales pendientes: entrada desde Dashboard/Weekly Plan, regreso a ambos orígenes, cinco filas, Start Workout, avisos, scroll y tamaños de pantalla.

## Uso de IA
Codex se utilizó para consultar Figma MCP, adaptar la interfaz a Compose, convertir los íconos originales a VectorDrawable, integrar las rutas existentes y preparar documentación y verificaciones locales. La revisión visual y funcional manual corresponde al integrante.
