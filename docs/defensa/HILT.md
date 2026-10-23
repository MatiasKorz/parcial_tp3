# Hilt en VORA Coach (DAT-03, RF08)

Guía para la defensa oral: qué arma Hilt en **esta** app, cuánto vive cada objeto y cómo llega un ViewModel a una pantalla. Los conceptos generales están en `.claude/skills/tp3-guidelines/references/conceptos.md`.

## Dónde está cada pieza

| RF08 pide | En el código |
|---|---|
| "Usen Hilt" | `@HiltAndroidApp` en `ExamenTP3Application`, registrada con `android:name` en el manifest |
| "Entry Point" | `@AndroidEntryPoint` en `MainActivity` |
| "Hilt View Model" | `@HiltViewModel` + `@Inject constructor` en los ViewModels de `viewmodel/`, pedidos con `hiltViewModel()` en el NavHost |
| "repositorios por constructor" | `@Singleton class MealRepository @Inject constructor(mealDao)` (y `WorkoutRepository`, de DAT-01) |
| "Retrofit, Room y DAO mediante módulos" | `di/NetworkModule`, `di/RetrofitModule`, `di/DatabaseModule`: `@Module @InstallIn(SingletonComponent::class) object` con `@Provides` |

## Qué objetos construye Hilt y cuánto viven

```text
SingletonComponent (vive lo que el proceso de la app)
├── HttpLoggingInterceptor  @Singleton  NetworkModule   BODY en debug, NONE en release
├── OkHttpClient            @Singleton  NetworkModule   ← usa el interceptor
├── Retrofit                @Singleton  RetrofitModule  ← usa OkHttpClient
├── WorkoutApi              @Singleton  RetrofitModule  ← retrofit.create(...) (DAT-01)
├── AppDatabase             @Singleton  DatabaseModule  ← Room.databaseBuilder(@ApplicationContext)
├── MealDao                 sin scope   DatabaseModule  ← db.mealDao()
├── MealRepository          @Singleton  @Inject constructor(MealDao)
└── WorkoutRepository       @Singleton  @Inject constructor(WorkoutApi) (DAT-01)

ActivityComponent (vive lo que MainActivity)
└── inyecta MainActivity (@AndroidEntryPoint): habilita hiltViewModel() en su contenido

ViewModelComponent (vive lo que el ViewModel = lo que su destino en el back stack)
├── MealRoomCheckViewModel          ← MealRepository (Nutrition)
└── WorkoutConnectionCheckViewModel ← WorkoutRepository (Workout, DAT-01)
```

- **`@Singleton`** hace que Hilt cree una sola instancia y la reuse. Instalar un módulo en `SingletonComponent` **no** alcanza: sin `@Singleton`, cada pedido crea una instancia nueva. Por eso OkHttp, Retrofit, la API, la base y los repositorios llevan la anotación: son caros de crear o tienen que ser únicos (una sola conexión a la base, un solo pool de conexiones HTTP).
- **`MealDao` no lleva scope:** cada pedido vuelve a llamar a `db.mealDao()`, pero la base es única y Room ya cachea el DAO. Además, el único que lo pide es `MealRepository`, que es singleton.
- **Los ViewModels** no son singletons. Hilt los crea cuando un destino de navegación los pide por primera vez, sobreviven a la rotación y se destruyen cuando el destino sale del back stack.

## Por qué constructor y por qué módulo

- **Lo nuestro va con `@Inject constructor`:** Hilt lee el constructor, ve qué necesita y sabe construirlo solo, sin escribir más código. Además, en un test unitario se le pasa un falso por constructor, sin Hilt.
- **Lo que no es nuestro va en módulos:** Retrofit, OkHttp y Room se arman con builders y la interfaz de la API o el DAO las implementa código generado. No podemos ponerle `@Inject` a esas clases, así que le explicamos a Hilt cómo crearlas con una función `@Provides`.
- **Nadie hace `new`:** fuera de los módulos no hay `Retrofit.Builder()`, `Room.databaseBuilder(...)` ni `XxxRepository(...)`. Si mañana cambia la base URL o el nombre de la base, se toca un solo lugar.

## Hilt y Dagger

- **Dagger** es el framework de inyección de dependencias que, en tiempo de compilación, genera el código que crea los objetos y verifica el grafo. Si falta una dependencia, el error aparece al compilar y no en la app ya corriendo.
- **Hilt está construido sobre Dagger** y le agrega lo específico de Android: componentes ya definidos atados al ciclo de vida (`SingletonComponent`, `ActivityComponent`, `ViewModelComponent`…), las anotaciones `@HiltAndroidApp`, `@AndroidEntryPoint` y `@HiltViewModel`, y la integración con ViewModel y Navigation. Con Dagger puro habría que escribir esos componentes a mano y conectarlos con la Application y las Activities.
- **Por qué no hay un grafo de Dagger puro:** Hilt ya genera el grafo de Dagger de la app. La consigna lo aclara: "no se exige un segundo grafo de Dagger puro" (SPEC p.1). Eso sería el extra EXT-07.
- **Al compilar**, KSP corre el compilador de Hilt (`ksp(libs.hilt.compiler)`): genera `Hilt_ExamenTP3Application`, `Hilt_MainActivity`, las fábricas de cada `@Provides` y `@Inject` y los componentes, y valida el grafo. Esas son las tareas `hiltJavaCompileDebug` y `hiltAggregateDepsDebug` del build.

## Cómo llega un ViewModel a una pantalla de Compose

1. `MainActivity` es `@AndroidEntryPoint`, así que tiene acceso al grafo de Hilt.
2. En el NavHost (`AppNavGraph.kt`), cada `composable(...)` hace `val viewModel: MealRoomCheckViewModel = hiltViewModel()`.
3. `hiltViewModel()` usa la `NavBackStackEntry` de ese destino como dueña del ViewModel y una fábrica de Hilt. La primera vez crea el ViewModel y le inyecta el repositorio (que a su vez trae el DAO, que trae la base). Las siguientes veces, por ejemplo después de una rotación, devuelve el mismo ViewModel.
4. El NavHost lee `viewModel.uiState` y le pasa a la pantalla el **estado y lambdas**. La pantalla no conoce el ViewModel ni el repositorio.
