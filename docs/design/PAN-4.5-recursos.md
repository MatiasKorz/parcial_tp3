# PAN-4.5 — Active Workout

## Referencia y alcance
- Frame 1:554, Workout / 05 — Active Workout, 390 × 844.
- https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-554
- Rama creada desde main 74db4b3; PAN-4.4 y NUC-01 ya fusionados.
- Contexto de diseño, captura, propiedades y recursos recuperados durante la planificación. Sin consultas MCP repetidas.
- Interfaz de demostración del ejercicio 2 de 5; no ejecuta una sesión real ni consume el catálogo.
- Sin Room, repositorios, ViewModels, endpoints, Firebase, permisos de audio ni persistencia.

## Diseño y componentes
- Manrope; fondo #123D35, acentos #65D6B0 y #C9E86A.
- Margen 20, padding superior 12/inferior 24, separación principal 16.
- Cabecera con EXERCISE 2 OF 5, Seated Cable Row y Finish Workout.
- Panel de volumen: blanco 5 %, borde blanco 12 %, radio 16/padding 16; cifras 28 Bold; separador vertical de 32.
- LAST SESSION original 45 kg × 12 reps se mantiene fijo; el ajuste de carga es independiente.
- Tres series: ancho mínimo 110, padding 12, insignia 20; completa menta, actual lima y pendiente blanco 8 %. La serie actual tiene borde lima.
- Descanso: fondo menta 12 %, borde menta 25 %, padding 12, controles +30s y Skip.
- VORA COACH y Voice On con consejo original completo.
- CTA lima de 50 de alto de referencia: VoraPrimaryButton Large configurado solo en su subárbol para reutilizar la API existente; tipografía 16 Bold/22.
- Reutiliza tokens, formas, orbe, separador Material y derivaciones de estilos oscuros existentes.
- Nuevos estilos exclusivos en ActiveWorkoutStyles; sin modificaciones al tema global ni a PAN-4.4.
- LazyColumn raíz; FlowRow permite envolver controles de carga, descanso y series en ancho menor o fuente ampliada. Sin barra inferior.
- Previews 390 × 844 y 360 × 640/fuente 1.3. No equivalen a verificación en dispositivos.

## Estado simulado
- Inicio exacto: serie 1 completada, serie 2 actual, serie 3 pendiente; peso 45 kg, descanso 78 segundos (01:18).
- Complete Set completa la serie actual y avanza. Si queda otra serie, establece un descanso fijo de 78 segundos; no bloquea el CTA durante el descanso.
- Al completar la serie 3 se termina el descanso y aparece un aviso en inglés. Finish Workout permanece como salida explícita.
- Pulsar Complete Set después de terminar solo repite el aviso; no crea series adicionales.
- +30s suma 30 al descanso; después de Skip reinicia un descanso simulado de 30. Tras completar las tres series muestra el aviso sin reiniciar el descanso.
- Skip establece cero y muestra Rest complete; no completa ninguna serie ni cambia la carga.
- No hay reloj real, cuenta regresiva ni coroutines de tiempo.
- Carga representada en pasos enteros de 2,5 kg; mínimo cero. Cambiarla no modifica las series ni LAST SESSION.
- Estado en remember: local y no persistido. Una nueva composición de la pantalla (incluida recreación de Activity) vuelve al estado inicial.
- Voice On muestra aviso de demostración en inglés, sin micrófono ni permisos.
- Todo esto es una simulación UI, no un cálculo real de volumen o progresión entre ejercicios.

## Navegación
- Se reemplaza solo el placeholder ActiveWorkout en AppNavGraph.
- Pre Workout conserva workoutId en active_workout/{workoutId}; la pantalla no lo utiliza para cargar datos reales.
- Finish Workout abre WorkoutComplete.route con guarda RESUMED.
- WorkoutComplete no admite workoutId; no se modifica su contrato ni se agrega un argumento.
- Atrás del sistema conserva el back stack hacia Pre Workout; no se intercepta ni se navega creando duplicados.
- La guía de ejercicios sigue en su ruta existente, accesible desde Details; no se agregan controles que no estén previstos en Active Workout.
- Íconos claros de barras del sistema aplicados localmente y restaurados al salir; no se modifica PAN-4.4.

## Recursos y diferencias
- Orbe existente ic_home_orb.xml a 18 dp; conserva la limitación conocida de sombra suave omitida.
- Reloj original: nodo 1:607, export a37ef.svg del prefijo MCP 09ed0dda-40d1-4b5f-b81b-b413d8759b79.
- Original local: C:\dev\vora-referencias\PAN-4.5-originales\clock.svg.
- Convertido a ic_active_workout_clock.xml preservando viewport 100 × 100, trazado, color #65D6B0, trazo 2 y extremos redondos.
- El original estaba recortado dentro de una caja de 16; se escala completo a 16 dp, según la decisión aprobada, para mostrar el ícono.
- No se usan URLs temporales en ejecución. Las barras del sistema Android reemplazan el marco decorativo, reloj y señales iOS del frame.
- Las áreas y textos pueden crecer para evitar recortes con fuentes ampliadas.

## Verificación
- assembleDebug y testDebugUnitTest: BUILD SUCCESSFUL en 40 s.
- 31 tests aprobados: 9 nuevos de transiciones; 0 fallos, errores u omitidos.
- git diff --check sin errores; también se revisan los archivos nuevos con --no-index --check.
- Reloj validado contra SVG original; recursos XML sin duplicados por configuración; mismos 38 registros de destinos que la base.
- Nueve tests nuevos de transiciones del estado puro sin Android: inicio, avance, final, +30s, Skip, reinicio de descanso, límites finales y cambios de carga.
- Sin emuladores, ADB ni pruebas visuales automáticas.
- Pendiente manual: diseño, todos los controles, avisos, scroll, back stack a Pre Workout, Finish Workout y barras del sistema al entrar/salir.

## Uso de IA
Codex se utilizó para consultar Figma MCP en la planificación, implementar Compose y estado simulado, convertir el reloj original, agregar tests unitarios, integrar la ruta y preparar documentación y verificaciones locales. La revisión visual y funcional corresponde al integrante.
