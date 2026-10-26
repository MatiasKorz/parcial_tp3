# Capas, estado observable y persistencia en VORA Coach (NUC-06, RF06, RF07)

Guía para la defensa oral: cómo viaja un dato desde el mock hasta la pantalla y desde el formulario hasta Room, por qué rotar no repite la consulta y por qué los datos sobreviven al cierre de la app. Cómo arma Hilt cada pieza está en `HILT.md`.

## Dónde está cada pieza

| La consigna pide | En el código |
|---|---|
| "DTO para JSON" (RF06) | `data/remote/dto/WorkoutDto.kt` (`WorkoutSummaryDto`) y `WorkoutDetailDto.kt` (`WorkoutDetailDto`, `ExerciseDto`): campos nullable con `@SerializedName` |
| "modelos para la app" | `model/WorkoutSummary.kt`, `model/WorkoutDetail.kt` (`WorkoutDetail`, `Exercise`), `model/Meal.kt` |
| "Entity para Room" | `data/local/WorkoutSummaryEntity.kt`, `WorkoutDetailEntity.kt` (`WorkoutDetailEntity`, `ExerciseEntity`), `MealEntity.kt` |
| "conviertan DTO a modelo y a Entity" | `WorkoutSummaryDto.toModel()` → `WorkoutSummary.toEntity()`; `WorkoutDetailDto.toModel()` → `WorkoutDetail.toEntity()` + `toExerciseEntities()`. La conversión a Entity pasa por el modelo: el DTO se valida una sola vez (sin id, se descarta) |
| "en comidas, separen Entity y modelo" | `MealEntity` (fecha como epoch day, tipo como `String`) ↔ `Meal` (`LocalDate`, `MealType`), con `toModel()` / `toEntity()` |
| "Un repositorio coordina las fuentes" | `WorkoutRepository(api, workoutDao, workoutDetailDao)`: baja de Retrofit, guarda en Room y la UI lee de Room. `MealRepository(mealDao)` para comidas |
| "la UI no consulta Retrofit ni DAO" | Ninguna clase de `ui/` importa `WorkoutApi`, un DAO ni un repositorio: las pantallas reciben `uiState` + lambdas |
| "estado observable" (RF07) | Cada ViewModel expone `val uiState: StateFlow<…>`; el NavHost lo lee con `collectAsState()` |
| "procesen sus eventos en ViewModel y usen coroutines" | `retry()`, `onAddToMeal()`, `deleteMeal()`, `onPreviousDay()`… lanzan en `viewModelScope` |
| "Inyecten repositorios por constructor" (RF08) | `@HiltViewModel class XxxViewModel @Inject constructor(repository: …)` |

Los ViewModels de los flujos funcionales son `WorkoutCatalogViewModel` (catálogo, en Weekly Plan), `WorkoutDetailsViewModel`, `ExerciseDetailsViewModel`, `NutritionDashboardViewModel`, `TodaysMealsViewModel`, `FoodConfirmationViewModel` (formulario) y `MealDetailsViewModel`. Todos se piden con `hiltViewModel()` en `ui/navigation/AppNavGraph.kt`; nadie los crea a mano.

## Recorrido 1: del mock a la pantalla (catálogo)

```text
mock  GET /v1/workouts  →  {"data": [{"id": "w01", …}, …]}
  │   OkHttp (NetworkModule) + Retrofit/Gson (RetrofitModule)
  ▼
WorkoutApi.getWorkouts(): WorkoutListResponseDto            ← DTO, refleja el JSON
  │   WorkoutRepository.refreshWorkouts()
  │     dto.toModel()?.toEntity()  →  workoutDao.upsertAll(…)
  ▼
Room, tabla workouts (WorkoutSummaryEntity)                  ← Entity, fila de la base
  │   workoutDao.observeAll(): Flow<List<Entity>>
  │   WorkoutRepository.observeWorkouts() → .map { it.toModel() }
  ▼
Flow<List<WorkoutSummary>>                                    ← modelo
  │   WorkoutCatalogViewModel: combine(cache, estado del refresh)
  │     .stateIn(viewModelScope, WhileSubscribed(5_000), …)
  ▼
StateFlow<WorkoutCatalogUiState>
  │   NavHost: collectAsState() → WeeklyPlanScreen(uiState, onWorkoutClick, onRetry)
  ▼
pantalla
```

Puntos para explicar:

1. **La UI lee solo de Room.** Retrofit no le devuelve datos a la pantalla: escribe en la base, y Room vuelve a emitir el `Flow`. Así, si el servidor se cae, el catálogo ya guardado sigue visible (RF02) y el error se muestra al lado (RF05).
2. **El ViewModel separa dos cosas:** lo que hay en la caché (`observeWorkouts()`) y cómo terminó la última descarga (`CatalogRefresh`: cargando, error, respuesta vacía). `combine` las junta en un único `UiState`.
3. **El reintento** (`retry()`) es un evento: la pantalla llama a la lambda, el ViewModel lanza otra descarga en `viewModelScope` y la ignora si ya hay una en curso.
4. **El detalle** hace lo mismo con `GET /v1/workouts/{workoutId}`: el id llega por la ruta y el ViewModel lo lee de `SavedStateHandle`; `refreshWorkoutDetail()` guarda el entrenamiento y sus ejercicios en una transacción (`WorkoutDetailDao.replace`).

## Recorrido 2: del formulario a Room (comidas)

```text
FoodConfirmationScreen  (campos de texto, botón "Add to …")
  │   lambdas onNameChange, onCaloriesChange…, onAddToMeal
  ▼
FoodConfirmationViewModel
  │   cada tecla actualiza el borrador: _uiState.update { it.copy(draft = …) }
  │   onAddToMeal(): validateMealForm(draft) → Meal (modelo) o errores por campo
  │   viewModelScope.launch { repository.addMeal(meal) }
  ▼
MealRepository.addMeal(meal) → mealDao.insert(meal.toEntity())
  ▼
Room, tabla meals (MealEntity)
  │   mealDao.observeByDate(epochDay) emite de nuevo porque cambió la tabla
  ▼
TodaysMealsViewModel / NutritionDashboardViewModel → .map { … } → .stateIn(…)
  ▼
la lista y el total del día se actualizan solos
```

Puntos para explicar:

1. **El borrador vive en el ViewModel:** lo tipeado sobrevive a la rotación sin `rememberSaveable`.
2. **La validación es síncrona y pura** (`MealFormValidation.kt`, con tests). Solo si es válida se lanza la escritura.
3. **Guardar es asíncrono:** el ViewModel marca `isSaving`, y cuando Room confirma pone `savedDate`. El NavHost ve ese valor, navega a la lista de ese día y llama a `onSavedHandled()`. Un segundo toque mientras guarda no inserta dos veces.
4. **Eliminar** (`MealDetailsViewModel.deleteMeal()`) espera a que Room confirme antes de volver: si la pantalla hiciera `popBackStack()` primero, se cancelaría `viewModelScope` a mitad del borrado.
5. **Nadie "refresca" la lista a mano:** como la lista observa un `Flow` de Room, cualquier inserción o borrado la vuelve a emitir.

## Rotación: por qué no se repite la consulta

- Al rotar, Android destruye y vuelve a crear la Activity, y Compose recompone todo. **El ViewModel no se destruye:** lo guarda el `NavBackStackEntry` del destino, y `hiltViewModel()` devuelve la misma instancia.
- La descarga se dispara en el `init` del ViewModel, que corre **una vez** por instancia. No hay `LaunchedEffect` en la pantalla que la vuelva a lanzar al recomponer.
- `stateIn(…, SharingStarted.WhileSubscribed(5_000), …)` mantiene viva la suscripción a Room durante 5 s sin observadores: la rotación tarda menos, así que ni siquiera se vuelve a consultar la base, y el `StateFlow` conserva el último valor.
- En el listado de comidas, el día elegido está en `SavedStateHandle` (`TodaysMealsViewModel`), así que sobrevive también a la muerte del proceso.

## Persistencia: por qué los datos sobreviven al cierre

- `DatabaseModule` crea la base con `Room.databaseBuilder(context, AppDatabase::class.java, "app.db")`: es un archivo SQLite en el almacenamiento interno de la app, no una base en memoria.
- Al reabrir la app, `observeWorkouts()`, `observeWorkoutDetail()` y `observeMealsByDate()` leen ese archivo, así que el catálogo, los detalles ya abiertos y las comidas aparecen antes de que responda el servidor (o aunque no responda).
- Los cambios de esquema vienen con migraciones (`MIGRATION_1_2`, `MIGRATION_2_3`), sin `fallbackToDestructiveMigration`: actualizar la app no borra los datos.
- Solo se pierden si se desinstala la app o se borran sus datos desde Ajustes.

## Cómo verificarlo en el emulador

Con el mock corriendo (`python mock/server.py`) y la consola del mock a la vista: cada request que recibe queda registrada ahí, con su método y su path.

1. **Rotación en el catálogo:** abrir Workout → Weekly Plan. En el log del mock aparece **un** `GET /v1/workouts`. Rotar el emulador (Ctrl+← / Ctrl+→) varias veces: no aparecen requests nuevas y la lista sigue igual.
2. **Rotación en el detalle:** tocar un entrenamiento. Aparece **un** `GET /v1/workouts/w01` (o el id elegido). Rotar: no hay requests nuevas.
3. **Rotación con el formulario a medio llenar:** Nutrition → Add Food → Log manually, completar algunos campos y rotar: lo escrito sigue.
4. **Persistencia:** guardar una comida, abrir un par de detalles de entrenamiento y cerrar la app desde Recientes (deslizar hacia arriba). Detener el mock y volver a abrir la app: las comidas siguen en Today's Meals y el catálogo y los detalles abiertos se ven desde la caché, con el error de conexión al lado.

## Cómo está probado

- `data/repository/WorkoutRepositoryTest`: con una API y DAOs falsos, comprueba que la descarga escribe en Room y la UI lee de ahí, que una falla o una respuesta vacía no borran la caché, que una respuesta sin `data` es error y que un detalle nuevo reemplaza los ejercicios que el servidor ya no manda.
- `data/repository/MealRepositoryTest`: alta, filtro por fecha, total del día y borrado.
- `data/local/*MapperTest`: conversiones DTO → modelo → Entity → modelo.
- `viewmodel/*Test`: la lógica pura que arma cada `UiState`.

Se corren con `.\gradlew.bat :app:testDebugUnitTest`. No usan Android ni Hilt: los repositorios reciben sus dependencias por constructor, así que alcanza con pasarles falsos escritos a mano.
