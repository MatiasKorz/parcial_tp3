# PAN-4.1 — Dashboard de entrenamiento

## Alcance y base

Rama feat/PAN-4.1-workout-dashboard creada desde main actualizada en fe083bf, con PAN-1.2 ya fusionado.
Pantalla de demostración; no implementa catálogo, métricas reales ni nuevas fuentes de datos.
No modifica Home, otras pantallas ni las APIs de componentes compartidos.

## Diseño y recursos

Frame oficial: Workout / 01 — Dashboard, nodo 1:213, 390 × 851.
Copia: https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-213

Se recuperaron una vez los metadatos y una captura del frame. No había PNG local de Workout Dashboard al iniciar el ticket.
Medidas verificadas: margen horizontal 20, separación entre secciones 20, cabecera 350 × 53, panel 350 × 189 con padding 20, coaching 350 × 118, actividad 350 × 112 y progreso 350 × 126. El contenido usa scroll y alturas adaptables en lugar de recortar texto.
La cabecera del calendario utiliza el recurso existente ic_home_calendar a 20 dp y el orbe reutiliza ic_home_orb a 20 dp. Son recursos originales incorporados por BASE/PAN previos; se conserva su conversión existente, incluida la aproximación de sombras del orbe.
Se agregó solo ic_workout_dashboard_check.xml, convertido del SVG original del nodo 1:260. Conserva viewBox 100 × 100, path, trazo de 2 unidades y terminación redonda; se representa en el área de 12 × 12 del frame. No se engrosa ni redibuja el check.
El SVG descargado se conserva fuera del repo en C:\dev\vora-referencias\PAN-4.1-originales\check.svg. No se incluyen enlaces temporales de exportación.

Se reutilizan Manrope, fondo #F7F8F5, colores y estilos de BASE-01. VoraPanel, VoraCard, VoraLimeButton, VoraOutlinedButton y VoraTextAction son de BASE-02.
Los tres indicadores son textos de demostración (4 Days, 3/5 complete, 8,420 kg), no anillos ni gráficos inventados. Los siete días y sus estados se copian del frame y no se calculan.
La barra inferior pertenece a AppNavigation/VoraBottomBar; no se duplica ni se modifica.

## Decisiones aprobadas y dependencias

- La rama de NUC-01 origin/feat/NUC-01-catalogo-entrenamientos registra que el catálogo vive en Workout / 02 — Weekly Plan. Al comenzar PAN-4.1 aún no estaba fusionada. La confirmación con el docente continúa en REF-05 según su README.
- El equipo/autor aprobó Dashboard como demostración. No se integra WorkoutRepository, Retrofit ni Room en esta pantalla.
- Se retiró únicamente la instanciación provisional de WorkoutConnectionCheckViewModel del destino Dashboard. La clase, el repositorio y el trabajo de DAT-01/NUC-01 se mantienen intactos.
- Start Workout abre PreWorkout.createRoute(DEMO_WORKOUT_ID).
- Details abre WorkoutDetails.createRoute(DEMO_WORKOUT_ID).
- Calendario abre WeeklyPlan.route.
- View progress abre WorkoutProgress.route, Progress / 03 — Workout Progress. Es una decisión aprobada para PAN-4.1; no existía esa conexión explícita desde Dashboard en NAV-01.
- Se conserva el identificador de demostración ya existente en AppNavGraph, sin inventar un id real ni presentarlo como válido para servicios funcionales. Su sustitución/integración queda para el núcleo y la coordinación del equipo.
- Los eventos usan la guarda RESUMED existente. No se intercepta Atrás del sistema: NAV-01 conserva el back stack; las pantallas de destino permiten regresar por sus recorridos existentes.
- Al entrar a Workout Progress, la pestaña activa cambia a Progress según AppDestination; al volver se recupera Workout. No se altera la lógica global.
- NUC-01 y PAN-4.1 tocan AppNavGraph.kt y la fila 4.1 del README. Al integrar ambos cambios se debe conservar el catálogo de Weekly Plan sin restaurar el diagnóstico de Dashboard.

## Diferencias y límites visuales

Se usan los tamaños tipográficos y componentes de BASE-01/02; los metadatos no aportan todos los valores tipográficos, sombras, Auto Layout ni opacidades. No se afirma equivalencia píxel a píxel.
WorkoutDashboardStyles deriva únicamente estilos locales: overlay del chip 12% (patrón existente), texto secundario sobre panel 70% y borde de coaching 35%. Estos últimos se aproximan visualmente a la captura, pendientes de aceptación manual.
Las tarjetas y botones conservan los radios y bordes compartidos. No se agrega una sombra inventada al panel.
Las métricas pueden envolver texto; las columnas crecen en altura. Los días reducen su ancho si no entran. Las barras de sistema son las reales de Android, no la decoración iOS del frame.

## Verificación

- Build final: BUILD SUCCESSFUL (assembleDebug y testDebugUnitTest, 13 s).
- 8 tests unitarios existentes aprobados, 0 fallos y 0 errores; incluyen 3 tests de MealEntityMapperTest, 1 de ExampleUnitTest y 4 de AppDestinationTest. No son pruebas automatizadas de la UI nueva.
- git diff --check: sin errores.
- Prueba visual y funcional: exclusivamente manual por el autor desde Android Studio. No se iniciaron emuladores, ADB ni scripts de automatización visual para este ticket.
- Recorrido manual: Sign In → pestaña Workout; comprobar calendario, Start Workout, Details y View progress, regreso desde cada destino, cambio de pestañas, scroll, tamaño de referencia y otro tamaño, fuentes ampliadas y horizontal.
- Comparar con el frame y revisar en particular check fino, calendario/orbe reutilizados y alturas adaptativas.
- No se agregan tests que repliquen composición ni reglas de métricas: la pantalla solo muestra datos estáticos.

## Uso de IA y propuestas para revisión

Herramienta: Codex.
Uso: lectura de instrucciones, revisión de ramas/dependencias, consulta del frame y check original, implementación Compose de demostración, conexiones a rutas existentes, documentación y verificaciones de build/tests.
Pendiente prueba humana y declaración de que el integrante entiende el código para la defensa oral.

Commit y título de PR propuestos: feat(PAN-4.1): agrega dashboard de entrenamiento de demostración.
Futuro PR: describir el reemplazo del placeholder, la reutilización de BASE-01/02, la retirada puntual del diagnóstico y las cuatro rutas; adjuntar resultados reales y capturas manuales; mantener sin marcar los criterios pendientes.
Comentario futuro de tarjeta: enlace al PR cuando exista, más la declaración de uso de Codex anterior. No se escribe en Trello desde esta tarea.
