# PAN-4.7 — Workout Complete

## Referencia y alcance
- Frame 1:673, Workout / 07 — Workout Complete, 390 × 844.
- https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-673
- Base main e468944, con PAN-4.6 fusionado mediante PR #31.
- Contexto y captura del frame consultados en la planificación. Propiedades y textos del diseño conservados.
- Pantalla de demostración: duración 42 min, ejercicios 5/5, volumen 2,340 kg y mejora de 8 % son valores fijos del Figma, no cálculos de Active Workout.
- Sin historial, persistencia de métricas/valoración, endpoints, Room, Retrofit, repositorios ni ViewModels.

## Diseño y reutilización
- Manrope y tokens BASE-01: fondo #F7F8F5, texto primario #17211F, secundario #68736F y verde #123D35.
- Margen horizontal 24, padding superior 20/inferior 24, separación principal 20.
- COMPLETED 14 Bold/19 en menta.
- Orbe de victoria original 180 × 180, centrado.
- You did well! 24 SemiBold/33, mensaje 14/19, separación 6 y centrado.
- Métricas en VoraCard compartida: padding 16, radio 16, borde existente, cifras 18 Bold/25 y etiquetas 11/15 con separación 4.
- VORA INSIGHT: mintSoft, radio 16, padding 16, orbe compartido a 16, etiqueta 12 Bold/16 y texto completo original 13/18.
- Valoración: etiqueta 13/18, separación 10, opciones 13 SemiBold/18, padding horizontal 14/vertical 8, gap 8.
- Done reutiliza VoraPrimaryButton con tipografía 15 SemiBold/20 limitada al subárbol y alto mínimo 44.
- FlowRow permite envolver métricas y valoraciones con fuentes ampliadas. LazyColumn raíz y scroll; sin barra inferior.
- Estilos exclusivos WorkoutCompleteStyles; sin cambios al tema ni API de componentes.
- Previews 390 × 844 y 360 × 640/fuente 1.3, sin equivalencia con pruebas en dispositivos.

## Orbe original y transparencia
- Nodo 1:685 victory-orb-wrap, tamaño 180 × 180, incluye halo, gradiente, sombras e insignia central.
- SVG original 38f49.svg del prefijo MCP 20c1e6b6-9366-4b2e-a27b-4478b142f2f8, guardado fuera del repo en C:\dev\vora-referencias\PAN-4.7-originales.
- El SVG tiene filtro feGaussianBlur; VectorDrawable perdería esos efectos.
- La captura inicial del nodo incluía fondo opaco; se descartó como recurso de la app.
- Se exportó el nodo original con exportAsync PNG a escala 1 y contentsOnly, mediante una consulta de solo lectura. No se modificó el archivo Figma.
- Fuente final: victory-orb-transparent.png, RGBA 180 × 180, alfa de 0 a 255.
- Asset Android: drawable-nodpi/workout_complete_victory_orb.png, copia sin edición ni recomprensión.
- No se retocaron colores, no se borró fondo por aproximación ni se redibujó el ícono.
- El pequeño orbe del insight reutiliza ic_home_orb, con su limitación conocida de sombra desenfocada omitida.
- Barras de sistema nativas Android; no se dibuja el marco decorativo ni la barra iOS del frame.

## Valoración
- Good seleccionada inicialmente, fondo verde oscuro y texto blanco.
- Great, Good, Tough y Too Easy forman un grupo de selección única con rol RadioButton y estado seleccionado accesible.
- Estado local remember: no se envía ni persiste; al recrear la pantalla se restablece Good.

## Navegación y back stack
- AppDestination y sus argumentos permanecen sin cambios; WorkoutComplete.route no recibe workoutId.
- Al finalizar Active Workout, navigate a Complete elimina la entrada Active mediante popUpTo(entry.destination.id), inclusive=true.
- No se modifica ActiveWorkoutScreen: el ajuste está solo en su callback de AppNavGraph.
- Done y Atrás (BackHandler) usan la misma acción protegida por entry.isResumed.
- Si Dashboard existe, popBackStack(WorkoutDashboard.route, false) elimina las pantallas intermedias y Complete, sin crear otro Dashboard.
- Si la sesión comenzó desde Home y Dashboard no existe, se navega a WorkoutDashboard con popUpTo(Home.route), sin guardar el flujo finalizado y con launchSingleTop.
- Este fallback respeta la base NAV-01: Home permanece al fondo tras el acceso.
- Tras salir de Complete no deben quedar Active, Pre Workout o Details del flujo finalizado encima de Dashboard.
- No se altera navegación global de pestañas ni se agregan destinos.
- Al recibir ON_RESUME, Complete aplica íconos oscuros en las barras de sistema, después de la salida de Active. Ambas salidas van al Dashboard claro; no se restaura una configuración oscura del entrenamiento terminado.

## Verificación
- assembleDebug y testDebugUnitTest: BUILD SUCCESSFUL, verificación final 21 s.
- 31 tests existentes aprobados; 0 fallos, errores u omitidos.
- git diff --check sin errores; archivos nuevos revisados con --no-index --check.
- Orbe PNG 180 × 180 RGBA con alfa 0–255 e idéntico al export original; SHA-256 14e2ed23ad5ba8f8d71f1f3fe52283434a77ff8c0cfdf84cc216830c0f553af5.
- Recursos values sin duplicados por configuración; 38 registros de destinos conservados.
- Sin emuladores, ADB ni pruebas visuales automáticas.
- Pendiente manual: contraste con Figma, efectos/alfa del orbe, selección única y Good inicial, scroll, Done y Atrás.
- Probar dos recorridos: Dashboard → Pre Workout → Active → Complete; y Home → Pre Workout → Active → Complete.
- En ambos, Done y Atrás deben llegar a Dashboard y el siguiente Atrás no debe reabrir el entrenamiento finalizado.
- Verificar barras del sistema al pasar de Active oscuro a Complete claro.
- Tests unitarios existentes no verifican navegación Compose ni render.

## Uso de IA
Codex se utilizó para consultar Figma MCP, exportar el orbe original transparente sin modificar el diseño, implementar Compose con estilos aislados, completar el recorrido de navegación y preparar documentación y verificaciones locales. La revisión visual y funcional corresponde al integrante.
