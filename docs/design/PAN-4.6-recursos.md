# PAN-4.6 — Exercise Details

## Referencia y alcance
- Frame 1:628, Workout / 06 — Exercise Details, 390 × 844.
- https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-628
- Base main 3f2060c, PAN-4.5 fusionado mediante PR #30.
- Contexto de diseño y captura recuperados en la planificación, sin repetir consultas MCP para implementar.
- Guía exclusiva de demostración de Seated Cable Row, con la imagen, cinco pasos y dos consejos del Figma.
- Sin datos reales de ejercicios, Retrofit, Room, ViewModels, repositorios, endpoints, video, voz ni permisos.
- No se modifican Workout Details, Active Workout, AppDestination, argumentos compartidos ni tema global.

## Diseño y reutilización
- Manrope y tokens BASE-01: fondo #F7F8F5, texto #17211F/#68736F y verde #123D35.
- Margen horizontal 20; padding superior 12/inferior 24; separación principal 16.
- Cabecera de 24 con Volver de 20, Exercise Guide de 16 SemiBold/22 y help-circle de 24.
- Título 22 SemiBold/30; Target de 13/18; imagen en proporción 350:160, radio 16.
- Performance Steps: título 14 Bold/19, separación 10 antes de los cinco pasos, pasos separados por 6.
- PRO FORM CUES: blanco, borde BASE-01, radio 12, padding 12, separación 8 y etiqueta 12 Bold/16.
- Ask Vora: mintSoft, radio 12, padding 12, orbe 24 y separación 10.
- CTA compartido VoraPrimaryButton con tipografía de acción limitada a su subárbol y alto mínimo 44; sin cambiar la API ni los estilos globales.
- LazyColumn con claves estables, alturas adaptables y scroll; sin barra inferior.
- Previews 390 × 844 y 360 × 640/fuente 1.3. No equivalen a pruebas de dispositivos.

## Imagen original demo-area
- Nodo 1:649 exportado mediante Figma MCP como PNG de 350 × 160, incluyendo el recorte y esquinas originales.
- Fuente local: C:\dev\vora-referencias\PAN-4.6-originales\demo-area.png.
- Asset Android: drawable-nodpi/exercise_details_demo_area.png, copiado sin editar, sustituir ni recomprimir.
- La imagen fuente del fill también estaba disponible en get_design_context como a1c18.png; se utiliza el render exportado del nodo para conservar el recorte exacto.
- Imagen mostrada con proporción original y ContentScale.Fit; no se añade otro recorte.
- No se introduce una imagen genérica ni un placeholder.
- La foto original muestra una máquina de remo; se conserva tal como aparece en el frame, sin presentarla como una exportación para otros ejercicios.

## Íconos
- Prefijo de exportación del contexto: 784010b7-82cd-4132-8254-3adae636b89d. Originales fuera del repo en PAN-4.6-originales.
- Volver: nodo 1:640, c775b.svg; nuevo ic_exercise_details_back.xml. Viewport 20, #123D35, trazo 2 y extremos redondos.
- Ayuda: nodo 1:644, 3f88c.svg; nuevo ic_exercise_details_help.xml. Viewport 24, #123D35, trazo 2 y extremos redondos.
- Se agregan porque los existentes no coinciden exactamente en geometría o proporción del trazo.
- Chevron: nodo 1:669, dd9f3.svg; reutiliza ic_workout_details_chevron con el mismo trazado y viewport 100, aplicando tint primary para obtener el verde original.
- Como en PAN-4.3, el SVG original de 100 estaba recortado en un espacio de 16; se muestra completo escalado a 16. Diferencia respecto de la captura documentada.
- Orbe compartido ic_home_orb a 24; conserva su limitación conocida de sombra suave omitida.
- No se usan URLs temporales en ejecución.

## Navegación y limitación NAV04/RF01
- Contrato existente sin cambios: exercise_details/{exerciseId}; exerciseArgs declara solo exerciseId.
- Todas las filas actuales de Workout Details envían el mismo DEMO_EXERCISE_ID (demo-exercise); no transmiten workoutId ni la identidad propia de cada fila.
- El proyecto aún no dispone de modelos e instrucciones de ejercicios NUC-02. Los nombres de las filas no bastan para generar guías correctas.
- Esta pantalla siempre muestra la guía Figma de Seated Cable Row; no se afirma que corresponda al ejercicio seleccionado.
- NAV04 y RF01 NO se consideran completamente cumplidos por esta implementación.
- NUC-02 deberá aportar los ejercicios e instrucciones por identidad y, si sus IDs no son globalmente únicos, justificar workoutId u otro contexto en el contrato compartido.
- No se inventan IDs, instrucciones, imágenes o una capa de datos provisional.
- Volver y Back to Workout llaman al mismo callback popBackStack protegido con isResumed.
- Atrás del sistema conserva NAV-01: regreso al origen, sin rutas duplicadas ni pantallas nuevas.
- Ask Vora y help-circle muestran avisos de demostración en inglés; no realizan llamadas remotas ni capturan audio.
- La UI recibe solo el callback de regreso y mantiene localmente el aviso abierto.

## Verificación
- assembleDebug y testDebugUnitTest: BUILD SUCCESSFUL en 42 s.
- 31 tests existentes aprobados; 0 fallos, errores y omitidos.
- git diff --check sin errores y archivos nuevos revisados con --no-index --check.
- PNG de 350 × 160 idéntico byte a byte al export original; SHA-256: 909dcd5d82b280e597a5cef4d23479d942b9a97e0d4ed83d6aa82d31e9fb449a.
- Conversiones SVG verificadas, sin recursos values duplicados; 38 registros de destinos conservados y contrato compartido sin cambios.
- No se agregan tests que repliquen el layout o los textos; no hay nueva lógica de negocio.
- Sin emuladores, ADB ni automatización visual.
- Pendiente manual: imagen y recorte, textos, avisos, los tres mecanismos de regreso, scroll y adaptación visual.

## Uso de IA
Codex se utilizó para consultar Figma MCP durante la planificación, recuperar el recurso original, implementar Compose con estilos aislados, convertir íconos originales, integrar la ruta existente y preparar documentación y verificaciones locales. La revisión visual/funcional manual corresponde al integrante.
