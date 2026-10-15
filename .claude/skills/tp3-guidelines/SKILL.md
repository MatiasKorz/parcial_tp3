---
name: tp3-guidelines
description: 'Reglas y convenciones de desarrollo del equipo para el repo del grupo de Taller de Programación 3 (TP3): app Android en Kotlin + Jetpack Compose con Material 3, Navigation Compose, ViewModel + StateFlow, Hilt con KSP, Retrofit + Gson y Room. Usala siempre que escribas, revises o expliques código de este repo, aunque no la nombren: pantallas, componentes, tema, navegación, ViewModels, UiState, módulos de Hilt, red, persistencia, tests unitarios o la revisión de un PR. También para preparar la defensa oral: qué construye Hilt y su relación con Dagger, StateFlow vs LiveData vs Flow, recomposición, navigate vs popBackStack. Keywords: Android, Kotlin, Jetpack Compose, Material 3, NavHost, hiltViewModel, StateFlow, UiState, Retrofit, OkHttp, Room, DAO, DTO, Entity, edge-to-edge, code review, PR checklist, layered architecture, team conventions, arquitectura por capas, convenciones del equipo. No la uses fuera del repo del grupo de TP3.'
---

# TP3: guías de desarrollo del equipo

Reglas que el equipo adopta para escribir, revisar y explicar el código de la app. No definen **qué** construir: eso está en la tarjeta de Trello y en el diseño, y los nombres de los ejemplos (`Item`, `Home`, `Login`) son ilustrativos. Si una tarjeta o el diseño contradicen esta guía, gana la tarjeta; anotá el desvío en el PR.

- **Precedencia sobre el código existente:** si el código que ya está en el repo no cumple estas guías, ganan las guías, y ese código no se copia como modelo. Se alinea solo lo que toca la tarjeta en curso, sin refactors fuera de alcance.
- Ramas, commits y PRs: `docs/onboarding/CONVENCIONES.md`. Reglas para agentes: `AGENTS.md`.
- Idioma: identificadores en inglés; textos visibles en `strings.xml`, con el copy del diseño; comentarios de código como defina `CONVENCIONES.md` (mientras no lo defina, en inglés y solo para explicar el porqué).

## 1. Stack y versiones

Kotlin · Jetpack Compose + Material 3 · Navigation Compose · ViewModel + StateFlow + coroutines · Hilt con KSP · Retrofit + Gson (OkHttp) · Room.

- Las versiones las fija `gradle/libs.versions.toml`: leelas de ahí y no las repitas en código. La única copia en la documentación es la lista de `AGENTS.md`, que se actualiza en la misma tarjeta `chore`. Toda dependencia va al catálogo y se usa con `libs.…`.
- Cambiar una versión o agregar una dependencia es una tarjeta `chore` aparte, nunca parte de una pantalla. Nada de alpha, beta ni RC si hay una versión estable. Kotlin y el plugin de Compose comparten versión (`version.ref = "kotlin"`), y KSP y Hilt generan código sobre ese compilador: se suben juntos, en una sola tarjeta `chore`, y se compila.
- AGP 9 trae Kotlin integrado: no se aplica `org.jetbrains.kotlin.android`, el plugin `kotlin-kapt` no funciona con el Kotlin integrado (usamos `ksp(...)`, no `com.android.legacy-kapt`) y el compilador se configura con `kotlin { compilerOptions { … } }`, no con `kotlinOptions`. Un `build.gradle.kts` escrito para AGP 8 no sirve tal cual.
- `hiltViewModel()`: con `androidx.hilt` 1.2.x se importa de `androidx.hilt.navigation.compose`. Desde 1.3.0 está en el artefacto `androidx.hilt:hilt-lifecycle-viewmodel-compose` y se importa de `androidx.hilt.lifecycle.viewmodel.compose`. Si cambia la versión, cambian la dependencia y el import.

## 2. Arquitectura por capas

```text
MainActivity    @AndroidEntryPoint · enableEdgeToEdge() · tema + NavHost, nada más
NavHost         rutas · hiltViewModel() por destino · traduce eventos en navegación
XxxScreen       recibe uiState + lambdas; no conoce ViewModel, navController ni datos
XxxViewModel    @HiltViewModel · expone StateFlow<XxxUiState> · trabaja en viewModelScope
XxxRepository   única puerta a los datos · decide la fuente · mapea DTO/Entity ↔ modelo
Fuentes         Retrofit (XxxApi + DTO) · Room (Entity + Dao + Database) · Firebase Auth, si se usa
```

- Cada capa habla solo con la de abajo: la UI no toca Retrofit, el DAO ni el repositorio. Las pantallas reciben estado y emiten eventos (`onItemClick: (String) -> Unit`, `onRetry: () -> Unit`). No reciben `navController` ni el ViewModel: así se pueden previsualizar, testear y reutilizar, y la navegación queda en un solo lugar.
- Hay tres tipos de dato separados: el **DTO** refleja el JSON, el **modelo** es lo que usa la app y la **Entity** es la fila de Room. El repositorio convierte con extensiones `toModel()` / `toEntity()`, definidas junto al DTO o a la Entity. La UI solo ve modelos.
- El repositorio es una clase concreta `@Singleton class XxxRepository @Inject constructor(...)`. Interfaz + implementación (`@Binds`) solo si hay dos implementaciones reales.
- Firebase Auth: usarlo y configurarlo (`google-services.json`, plugin `com.google.gms.google-services`) es una decisión del equipo, con su propia tarjeta `chore`. Si se usa, es una fuente más: `FirebaseAuth` se provee desde un módulo de Hilt y solo lo usa un repositorio (`AuthRepository`). Ni la UI ni el ViewModel lo llaman directo.

## 3. Paquetes

Bajo el paquete raíz. Respetá la estructura que ya existe y no muevas archivos fuera de tu tarjeta.

```text
di/                    módulos de Hilt (red: OkHttp y Retrofit; DatabaseModule)
data/remote/           XxxApi (Retrofit), dto/, interceptor/
data/local/            XxxEntity, XxxDao, AppDatabase
data/repository/       XxxRepository
model/                 modelos de la app
viewmodel/             XxxViewModel y su XxxUiState
ui/navigation/         AppDestination y el NavHost
ui/screens/<feature>/  XxxScreen y sus partes privadas
ui/components/         componentes reutilizables entre pantallas
ui/theme/              Color, Type, Theme (tokens del diseño)
```

Sufijos: `Screen`, `UiState`, `ViewModel`, `Repository`, `Api`, `Dto`, `Entity`, `Dao`, `Module`. Las interfaces no llevan prefijo `I`. No se crea un módulo de Hilt nuevo para lo que ya cubre uno existente.

## 4. Navegación

- Las rutas se definen en `sealed class AppDestination(val route: String)`, con un `data object` por destino, en minúscula o snake_case. No hay strings sueltos en `navigate("…")`. Usamos rutas string, no type-safe: una sola convención en todo el repo. El NavHost va en su propio archivo y recibe `navController: NavHostController = rememberNavController()`.
- En cada `composable(...)`: `val viewModel: XxxViewModel = hiltViewModel()`, leer el estado y pasarle a la pantalla estado + lambdas. Un ViewModel compartido entre destinos se pide con la entrada del grafo padre: `hiltViewModel(remember(entry) { navController.getBackStackEntry(parentRoute) })`. Ahí `entry` es el parámetro del lambda de `composable` y `parentRoute` es la ruta de un `navigation(...)` anidado que contiene a ambos destinos.
- Los argumentos van en la ruta, y solo el id, nunca el objeto entero. El ViewModel lo lee de `SavedStateHandle`, que Hilt inyecta:

```kotlin
data object ItemDetail : AppDestination("item_detail/{itemId}") {
    fun createRoute(itemId: String) = "item_detail/${Uri.encode(itemId)}"
}
// NavHost: composable(ItemDetail.route, listOf(navArgument("itemId") { type = NavType.StringType }))
// ViewModel: private val itemId: String = checkNotNull(savedStateHandle["itemId"])
```

- Para entrar sin dejar el login atrás: `navigate(Home.route) { popUpTo(Login.route) { inclusive = true }; launchSingleTop = true }`. Para cerrar sesión: `popUpTo(navController.graph.id) { inclusive = true }` vacía el back stack. Para volver: `navController.popBackStack()`; un `navigate()` hacia la pantalla anterior apila un duplicado. `popBackStack()` saca incluso el último destino y deja la pantalla en blanco, así que "volver" no se ofrece en el destino inicial.
- Doble toque: un evento de navegación se atiende solo si el destino que lo emite sigue activo (`entry.lifecycle.currentState == Lifecycle.State.RESUMED`). Así dos toques rápidos no apilan dos veces el detalle ni hacen dos `popBackStack()` que vacíen el back stack.
- Barra inferior: cada pestaña navega con estas opciones. La barra solo se muestra en los destinos de pestaña, y el ítem activo sale de `currentBackStackEntryAsState()` + `destination.hierarchy`.

```kotlin
navController.navigate(tab.route) {
    popUpTo(AppDestination.Home.route) { saveState = true } // first tab, always at the bottom of the stack
    launchSingleTop = true
    restoreState = true
}
```

  La documentación usa `popUpTo(navController.graph.findStartDestination().id)`, pero eso funciona solo si el destino inicial del grafo sigue en el back stack. Si es un login o un splash que ya sacaste con `inclusive = true`, el `popUpTo` se ignora y las pestañas se apilan. Por eso apuntamos a la primera pestaña.

## 5. Estado y ViewModel

```kotlin
@HiltViewModel
class ItemListViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ItemListUiState())
    val uiState: StateFlow<ItemListUiState> = _uiState.asStateFlow()
    init { load() }
    fun load() { viewModelScope.launch { _uiState.update { it.copy(isLoading = true, error = null) } /* … */ } }
}
```

- El `MutableStateFlow` es privado y se expone con `asStateFlow()`. Se modifica con `update { it.copy(…) }`, que es atómico, y no con `value = value.copy(…)`.
- La carga inicial va en `init`: el ViewModel sobrevive a la rotación, así que no repite la consulta. No la dispares desde la pantalla. Las corrutinas corren en `viewModelScope`: nada de `GlobalScope` ni scopes propios.
- Los Flows de Room se exponen con `.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)`: los 5 s mantienen la suscripción viva durante una rotación.
- En la UI se lee con `collectAsStateWithLifecycle()`, que es lo recomendado en Android y requiere `lifecycle-runtime-compose` en el catálogo; mientras esa dependencia no esté, se usa `collectAsState()`. El estado efímero que el ViewModel no necesita (un diálogo abierto, una pestaña interna) va en la pantalla con `rememberSaveable`. El estado de negocio va en el ViewModel.

## 6. UiState: estados explícitos

- Cada pantalla con datos distingue **cargando**, **contenido**, **vacío** y **error con reintento**. Vacío ≠ error: una respuesta válida sin datos es "vacío" y tiene su propio mensaje; una falla es "error" y ofrece reintentar. Un error no tapa el contenido ya cargado: si hay datos, se muestran, y el error va aparte (Snackbar o banner).
- Los mensajes salen de `strings.xml`. El estado guarda un `UiError`, que tiene un caso por tipo de error, cada uno con su `@StringRes`. La pantalla lo muestra con `stringResource(error.message)`, nunca con el texto de una excepción.

```kotlin
sealed interface UiError {
    @get:StringRes val message: Int
    data object NoConnection : UiError { override val message = R.string.error_no_connection }
    data object Timeout : UiError { override val message = R.string.error_timeout }
    data object Server : UiError { override val message = R.string.error_server }
    data object InvalidResponse : UiError { override val message = R.string.error_invalid_response }
    data object Unknown : UiError { override val message = R.string.error_unknown }
}
```

```kotlin
data class ItemListUiState(
    val isLoading: Boolean = false,
    val items: List<Item> = emptyList(),
    val error: UiError? = null,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && items.isEmpty()
}
```

La pantalla decide en este orden: si hay `items`, contenido (+ error aparte); si `isLoading`, indicador; si hay `error`, error con `onRetry`; si no, vacío. Si los estados son excluyentes de verdad (no hay contenido previo que conservar), usá un `sealed interface`.

## 7. Hilt

- `@HiltAndroidApp` va en la `Application`, registrada en el manifest con `android:name`, y `@AndroidEntryPoint` en `MainActivity`. Los ViewModels llevan `@HiltViewModel` + `@Inject constructor` y se obtienen con `hiltViewModel()`, con el alcance del destino de navegación.
- Las clases propias usan `@Inject constructor`. Lo que no es nuestro o se arma con un builder (OkHttp, Retrofit, la API, Room, los DAO) se provee desde un `@Module @InstallIn(SingletonComponent::class) object` con `@Provides`. Para atar una interfaz a su implementación: `@Binds`, en un módulo `abstract class`.
- Por defecto Hilt crea una instancia nueva en cada pedido. Lo que tiene que ser único (OkHttp, Retrofit, la API, la Database, los repositorios) lleva `@Singleton`; instalar un módulo en `SingletonComponent` no vuelve único lo que provee. Si hace falta un Context: `@ApplicationContext context: Context`, nunca una Activity guardada en un singleton. Nada se instancia a mano (`ItemRepository(...)`, `Room.databaseBuilder` fuera de un módulo), salvo en los tests unitarios, que pasan falsos por constructor.
- Para la defensa: al compilar, Hilt (que se apoya en Dagger, vía KSP) **genera** los componentes y las fábricas y **valida** el grafo; si falta una vinculación, no compila. En ejecución, esos componentes **crean** cada objeto cuando se lo pide, respetando su alcance. Desarrollo completo en `references/conceptos.md`.

## 8. Red: Retrofit

- La interfaz `XxxApi` usa paths relativos, sin `/` inicial (`@GET("items")`): un path con `/` inicial es absoluto y descarta el path de la `baseUrl`. Los métodos son `suspend` y son main-safe, así que `withContext(Dispatchers.IO)` sobra.
- La `baseUrl` termina en `/` (si tiene path y no termina en `/`, Retrofit lanza `IllegalArgumentException`) y sale de un recurso (`R.string.base_url`, `translatable="false"`) que el módulo lee con `@ApplicationContext`. Nunca va hardcodeada en el código. El permiso `android.permission.INTERNET` se declara explícito en el manifest.
- Gson con un DTO que lleva `@SerializedName` en cada campo. Gson ignora la nulabilidad de Kotlin: un campo ausente llega `null` aunque el tipo no lo admita. Por eso los campos opcionales del JSON van nullable en el DTO y el mapper decide el valor por defecto.
- HTTP en claro, solo para desarrollo: un Network Security Config con `<domain-config cleartextTrafficPermitted="true">` limitado al host de desarrollo (desde el emulador, la máquina host es `10.0.2.2`), puesto en el source set `debug` (`src/debug/res/xml/` + `src/debug/AndroidManifest.xml`). Nada de `usesCleartextTraffic="true"` global; `<debug-overrides>` no habilita HTTP.
- Los headers comunes van en un `Interceptor` provisto por Hilt, no repetidos en cada método. El logging va solo en debug: `HttpLoggingInterceptor` con `BODY` en debug y `NONE` en release. `BuildConfig.DEBUG` exige `buildFeatures { buildConfig = true }`. Si se loguean headers con credenciales, `redactHeader(...)`.
- Errores HTTP: preferí que el método devuelva el tipo directo, porque ante un no-2xx Retrofit lanza `HttpException`. Con `Response<T>` no lanza nada y hay que chequear `isSuccessful`. Un error HTTP nunca se convierte en lista vacía: eso lo disfraza de "vacío".
- Parseo y cancelación: atrapá también los errores de parseo. Con Gson, un JSON roto puede llegar como `MalformedJsonException` o `EOFException`, que son `IOException`: clasificalos antes que `IOException` o se van a mostrar como "sin conexión". No atrapes `CancellationException`. `runCatching` en una `suspend` la atrapa, así que si lo usás, relanzala.

```kotlin
fun Throwable.toUiError(): UiError = when (this) {
    is CancellationException -> throw this
    is HttpException -> UiError.Server
    is MalformedJsonException, is EOFException, is JsonParseException -> UiError.InvalidResponse
    is SocketTimeoutException -> UiError.Timeout
    is IOException -> UiError.NoConnection
    else -> UiError.Unknown
}
```

- No hay estado global mutable: ni `lateinit var` de configuración en un `object` ni clientes en `var`. Todo lo provee Hilt.

## 9. Room

- `@Entity` es la tabla, `@Dao` las operaciones y `@Database(entities = […], version = N)` expone los DAO. Lo que la UI observa se lee como `Flow`; las escrituras y las lecturas puntuales son `suspend`.
- `DatabaseModule` provee la Database con `@Provides @Singleton` (`Room.databaseBuilder(context, AppDatabase::class.java, "app.db")`) y cada DAO con `@Provides` desde la base. La Entity no sale del repositorio: `dao.observeAll().map { list -> list.map { it.toModel() } }`.
- La `@PrimaryKey` identifica la fila de forma única. Si un id no es único por sí solo, la clave es compuesta (`@Entity(primaryKeys = [...])`).
- Migraciones: subir `version` sin una `Migration` (o `AutoMigration`) lanza `IllegalStateException` al abrir la base, y cambiar una Entity sin subir `version` también crashea ("Room cannot verify the data integrity"). En desarrollo, con la versión 1 y sin usuarios, después de cambiar el esquema desinstalá la app o borrale los datos. `fallbackToDestructiveMigration(...)` borra todos los datos: no se agrega sin acordarlo en el grupo.

## 10. UI y Compose

- El tema propio vive en `ui/theme/`, con los tokens del diseño (colores, tipografía, formas) y las fuentes en `res/font/`. Va con `dynamicColor = false`, porque en Android 12+ el color dinámico reemplaza la paleta por la del fondo de pantalla. Si el diseño no define modo oscuro, el tema usa solo el esquema del diseño y no sigue `isSystemInDarkTheme()`. `MainActivity` envuelve todo en ese tema, no en un `MaterialTheme` pelado. En las pantallas, colores y textos salen de `MaterialTheme.colorScheme` y `MaterialTheme.typography`: un `Color(0x…)` suelto en una pantalla es un token que le falta al tema.
- Los componentes reutilizables (botones, campos, tarjetas, vistas de carga, vacío y error) van en `ui/components/`. Antes de crear uno, buscá si ya existe. Firma de una pantalla: `XxxScreen(uiState, onXxx…, modifier: Modifier = Modifier)`. `modifier` es el primer parámetro opcional y se aplica al elemento raíz.
- Cada pantalla y cada componente tienen una `@Preview(showBackground = true)` privada, envuelta en el tema del proyecto y con datos de ejemplo: una por estado relevante (contenido, vacío, error).
- Los textos van en `res/values/strings.xml` y se leen con `stringResource(...)`, con prefijo por pantalla (`item_list_title`). Los íconos llevan `contentDescription`, o `null` si son decorativos.
- Listas: una sola lista lazy raíz, con `item { }` para cabecera y pie, `items(list, key = { it.id })` para los elementos y `contentPadding` + `Arrangement.spacedBy(…)` para el espaciado. La `key` tiene que ser estable, única en esa lista y de un tipo que entre en un `Bundle` (`String`, `Int`, `Long`). Una `key` repetida crashea la app.
- Nunca pongas una lista lazy dentro de un `Column(Modifier.verticalScroll(...))` sin altura fija: lanza `IllegalStateException`. Si hay que mezclar contenido, todo va dentro de la misma lista lazy.
- Edge-to-edge: con `targetSdk` ≥ 35 es obligatorio en Android 15+, y con `targetSdk` ≥ 36 Android 16 ya no deja desactivarlo. Llamá a `enableEdgeToEdge()` en `MainActivity`.
- El `Scaffold` no aplica los insets: aplicá su `innerPadding` con `Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)`, o pasalo como `contentPadding` de la lista junto con `Modifier.consumeWindowInsets(innerPadding)`. En formularios, sumá después `Modifier.imePadding()` para que el teclado no tape campos ni botones, con `adjustResize` en la Activity. Sin el `consumeWindowInsets`, la barra de navegación se suma dos veces.
- Si el contenido puede no entrar (pantalla chica, horizontal, fuente grande), usá `Modifier.verticalScroll(rememberScrollState())`. El manifest no declara permisos que la app no usa.

## 11. Tests

- JUnit 4 en `app/src/test/`, sin Android, Robolectric ni Hilt. Se prueban los mappers DTO/Entity ↔ modelo, las validaciones, `toUiError()` y la lógica que decide qué estado mostrar. Las dependencias entran por constructor, así que alcanza con un falso escrito a mano: para eso está la DI.
- La lógica que se testea no usa clases de `android.*` (`Patterns`, `Uri`, `Log`, `TextUtils`): en un test local fallan con "Method … not mocked". Las validaciones se escriben con `Regex` de Kotlin, y no se usa `unitTests.returnDefaultValues`.
- Cada test prueba un solo comportamiento y su nombre lo dice (`dtoWithoutName_mapsToEmptyName`). Para correrlos: `./gradlew :app:testDebugUnitTest` (en PowerShell, `.\gradlew.bat :app:testDebugUnitTest`; ver la tabla de `AGENTS.md`).

## 12. Antes de aprobar un PR

Cómo se revisa (bajar la rama, compilar, probar en el emulador): `CONVENCIONES.md`. Qué se mira, capa por capa: `references/revision.md`. En resumen:

1. Compila (`:app:assembleDebug`), los tests pasan y no hay versiones, dependencias nuevas ni secretos fuera de una tarjeta `chore`.
2. Capas y estado: las pantallas reciben estado + lambdas, y la UI no ve `navController`, ViewModel, Retrofit ni DAO. StateFlow privado + `update {}`, carga en `init`, vacío ≠ error y el error no tapa el contenido.
3. Datos: ningún error termina en lista vacía, el parseo está atrapado, no se traga `CancellationException`, los mensajes salen de `strings.xml` y la Entity no llega a la UI. Todo cambio de esquema viene con versión y migración, o con el aviso de desinstalar.
4. UI: tema propio sin color dinámico, textos en `strings.xml`, `key` en las listas, insets resueltos y `@Preview`.

## references/

| Archivo | Leelo cuando… |
|---|---|
| `references/conceptos.md` | prepares la defensa oral o tengas que explicar un concepto (DI, Hilt y Dagger, flujos, recomposición, navegación, capas, Room, edge-to-edge) |
| `references/revision.md` | revises un PR o quieras chequear tu propio cambio antes de pedir revisión, capa por capa |
