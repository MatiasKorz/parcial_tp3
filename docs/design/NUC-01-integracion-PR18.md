# PR #18 — Integración NUC-01 con main

## Base y conflictos
- Worktree: C:/dev/vora-integracion-nuc01; rama fix/NUC-01-conflictos-main.
- NUC-01 13410ed integra origin/main 9c86600. DAT-01 y DAT-03 ya están integrados (PR #16 y #17).
- README, AppNavGraph y strings resueltos combinando ambas responsabilidades; no se restauran placeholders.
- Se conservan todos los destinos y funcionalidades actuales de main.
- Sin commits, push ni cambios en el checkout PAN-4.4 o la rama main.

## Weekly Plan y catálogo
- Conserva calendario, tarjetas Figma de demostración, coach, avisos, scroll y barra inferior PAN-4.2.
- Identifica el plan de demostración y muestra el catálogo real en una sección diferenciada.
- No asigna entrenamientos del catálogo a días ni al entrenamiento de hoy arbitrariamente.
- Conserva la tarjeta NUC-01 como WorkoutCatalogCard; evita dos WeeklyPlanScreen desconectadas.
- AppNavGraph obtiene WorkoutCatalogViewModel con Hilt; la UI recibe estado y callbacks.
- Las tarjetas reales navegan con workout.id a WorkoutDetails.createRoute(workoutId).
- Las tarjetas Figma conservan el ID demo existente.
- Details sigue mostrando contenido de demostración aunque conserva el ID recibido hacia Pre Workout: detalle real pendiente de NUC-02.
- Loading, empty, error/retry y aviso offline siguen pendientes de NUC-03. El ViewModel actual conserva la caché y registra fallos, sin UI de error.
- DAO, entidad/mapper, repositorio, ViewModel y tests de mapper NUC-01 se conservan sin cambios lógicos.
- Se respeta la retirada del diagnóstico provisional WorkoutConnectionCheckViewModel, reemplazado por NUC-01.
- Sin endpoints nuevos, llamadas duplicadas ni cambios en otras pantallas.

## Room 1 → 2
- Main tenía meals en versión 1; el PR añadía workouts sin subir versión, cambiando la identidad del esquema.
- Versión 2 con AppDatabase.MIGRATION_1_2 registrada en DatabaseModule.
- Único SQL: CREATE TABLE IF NOT EXISTS workouts, con las columnas y clave de WorkoutSummaryEntity.
- No borra, recrea ni altera meals; sin migraciones destructivas.
- IF NOT EXISTS también admite instalaciones de desarrollo NUC-01 versión 1 con workouts ya creada, conservando esa caché.
- SQL probado con SQLite en memoria: preserva datos y esquema meals, y workouts existentes.
- Esquema resultante comparado con el CREATE TABLE generado por Room/KSP.
- Pendiente actualización real Android/Room desde una instalación versión 1: no se ejecutaron emuladores ni ADB.

## Validación
- assembleDebug y testDebugUnitTest: BUILD SUCCESSFUL en 1 min 51 s.
- 22 tests aprobados: 0 fallos, 0 errores, 0 omitidos.
- Los mismos 38 registros de destinos de main en AppNavGraph; ningún destino eliminado.
- Recursos values XML válidos, sin nombres duplicados por configuración.
- Revisión de git diff --check, staging y alcance frente a origin/main antes de entregar.
- Advertencia no bloqueante: Gradle empaquetó libandroidx.graphics.path.so y libdatastore_shared_counter.so sin quitar símbolos nativos.
- No se incluyen archivos generados, local.properties ni secretos. El SDK se configuró solo mediante variable de entorno del proceso.

## Pendientes
- Prueba manual de ambas secciones Weekly Plan, catálogo con/sin servidor y navegación con dos IDs distintos.
- Prueba manual de actualización Room con comidas existentes.
- Integración NUC-02 y estados NUC-03 fuera del alcance.
- Commit y publicación requieren autorización posterior.

## Uso de IA
Codex se utilizó para analizar/resolver conflictos, integrar el catálogo con PAN-4.2, implementar la migración no destructiva y ejecutar build, tests y comprobaciones de navegación, recursos y SQLite. No se modificó Trello.
