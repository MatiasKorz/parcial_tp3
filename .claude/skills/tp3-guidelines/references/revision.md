# Checklist de revisión de un PR

Lo que tiene que cumplir un PR antes de aprobarse. El proceso (bajar la rama, compilar, probar en el emulador, comentar en GitHub, cómo se mergea) está en `docs/onboarding/CONVENCIONES.md`, y la plantilla del PR en `.github/pull_request_template.md`. Acá está **qué** mirar, capa por capa.

Cómo usarla:
- Revisá solo las secciones que el PR toca, más la sección "General".
- Cada observación lleva el archivo y la línea, la regla que no se cumple y una sugerencia concreta. "Esto está mal" no ayuda.
- Distinguí lo que **bloquea** (no compila, rompe una regla de capas, pierde datos, sube un secreto) de lo que es **sugerencia** (nombres, estilo). Lo que bloquea se corrige antes de aprobar; una sugerencia puede quedar como tarjeta nueva.
- Si el PR se aparta de esta guía porque la tarjeta lo pide, tiene que decirlo en la descripción.
- Que el código existente haga algo distinto no justifica un desvío: gana la guía. Lo que el PR toca se alinea; lo que no toca queda para su propia tarjeta.

## General
- [ ] `:app:assembleDebug` compila sin errores y `:app:testDebugUnitTest` pasa.
- [ ] El PR hace lo que pide su tarjeta y nada más: sin refactors, formateos ni movimientos de archivos ajenos.
- [ ] No cambia versiones ni agrega dependencias, salvo que sea una tarjeta `chore`. Si lo hace, la dependencia está en `gradle/libs.versions.toml` y se usa con `libs.…`.
- [ ] No sube secretos ni archivos generados (`local.properties`, `build/`, `.idea/`, tokens, claves).
- [ ] No queda código muerto, `TODO` sin tarjeta, `println`/`Log` de depuración ni `!!` sin justificar.
- [ ] Los nombres siguen los sufijos (`Screen`, `UiState`, `ViewModel`, `Repository`, `Api`, `Dto`, `Entity`, `Dao`, `Module`) y los archivos están en el paquete que corresponde.

## Arquitectura
- [ ] Cada capa habla solo con la de abajo: UI → ViewModel → Repository → Retrofit/Room.
- [ ] Ningún composable recibe `navController`, un ViewModel, la API, un DAO o el repositorio. Las pantallas reciben estado + lambdas.
- [ ] `MainActivity` solo tiene `enableEdgeToEdge()`, el tema y el NavHost.
- [ ] DTO, modelo y Entity están separados, con mappers `toModel()` / `toEntity()`. La UI solo ve modelos.
- [ ] Ningún objeto con dependencias se crea a mano fuera de un módulo de Hilt (salvo en tests).

## Navegación
- [ ] Las rutas nuevas están en `AppDestination` (`data object`). No hay strings sueltos en `navigate(...)`.
- [ ] Cada destino obtiene su ViewModel con `hiltViewModel()` dentro del `composable(...)`, o desde la entrada del grafo padre si se comparte.
- [ ] Los argumentos van en la ruta (solo ids, codificados con `Uri.encode` si pueden traer caracteres especiales) y el ViewModel los lee de `SavedStateHandle`.
- [ ] Después del login, logout u onboarding, el botón atrás no vuelve a esas pantallas (`popUpTo` + `inclusive = true`, `launchSingleTop`).
- [ ] "Volver" usa `popBackStack()`, no un `navigate()` a la pantalla anterior, y no se ofrece en el destino inicial.
- [ ] Los eventos de navegación se atienden solo si el destino que los emite está `RESUMED`. Probado: tocar dos veces rápido un ítem o "volver" no apila dos veces el destino ni deja la pantalla en blanco.
- [ ] Barra inferior: `popUpTo(primeraPestaña) { saveState = true }`, `launchSingleTop`, `restoreState`. Probado en el emulador: cambiar de pestaña varias veces y apretar atrás no recorre las pestañas, y cada pestaña conserva su estado.

## ViewModel y estado
- [ ] `@HiltViewModel` + `@Inject constructor`.
- [ ] `MutableStateFlow` privado, expuesto con `asStateFlow()`; se modifica con `update { it.copy(…) }`.
- [ ] La carga inicial va en `init`, no en un `LaunchedEffect` de la pantalla. Probado: al rotar no se repite la consulta.
- [ ] Los Flows de Room se exponen con `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), …)`.
- [ ] Solo `viewModelScope`: nada de `GlobalScope`, `runBlocking` ni scopes propios.
- [ ] La UI lee con `collectAsStateWithLifecycle()` (o `collectAsState()` si la dependencia todavía no está en el catálogo), de forma consistente.

## UiState
- [ ] La pantalla distingue cargando, contenido, vacío y error.
- [ ] Vacío y error son estados distintos: una respuesta válida sin datos no muestra "error", y una falla no muestra "no hay datos".
- [ ] El error ofrece reintentar (`onRetry`), y reintentar vuelve a pasar por cargando.
- [ ] Un error con contenido ya cargado no lo tapa: el contenido sigue visible y el error va aparte.
- [ ] El estado guarda un `UiError` (el `sealed interface` compartido, un caso por tipo de error con su `@StringRes`), no `e.message` ni `localizedMessage`. Un caso nuevo se agrega ahí, con su texto en `strings.xml`.

## Hilt
- [ ] Lo que tiene que ser único (OkHttp, Retrofit, la API, la Database, los repositorios) lleva `@Singleton`.
- [ ] Lo que no es nuestro se provee con `@Provides` en un `@Module @InstallIn(SingletonComponent::class)`. Una interfaz con una sola implementación real no lleva interfaz (y si la lleva, va con `@Binds`).
- [ ] El `Context` se pide con `@ApplicationContext`; no se guarda una Activity en un singleton.
- [ ] No hay estado global mutable (`object` con `var` o `lateinit var` de configuración).
- [ ] No se crea un módulo nuevo para lo que ya cubre uno existente.
- [ ] Si se usa Firebase Auth: `FirebaseAuth` se provee desde un módulo y solo lo usa un repositorio; ni la UI ni el ViewModel lo llaman. Su configuración entró en una tarjeta `chore` acordada por el equipo.

## Red
- [ ] Paths relativos en `@GET`/`@POST`, sin `/` inicial. La `baseUrl` termina en `/` y sale de un recurso.
- [ ] Cada campo del DTO tiene `@SerializedName`. Los campos que pueden faltar en el JSON son nullable y el mapper define el valor por defecto.
- [ ] Un no-2xx se trata como error: nunca se convierte en lista vacía o en un modelo vacío. Con `Response<T>`, se chequea `isSuccessful`.
- [ ] Los errores de parseo (`JsonParseException`, `MalformedJsonException`, `EOFException`) se atrapan y se clasifican antes que `IOException`, para no mostrarlos como "sin conexión".
- [ ] No se atrapa `CancellationException`: no hay `catch (e: Exception)` ni `runCatching` en una `suspend` sin relanzarla.
- [ ] No hay `withContext(Dispatchers.IO)` alrededor de una llamada `suspend` de Retrofit (sobra).
- [ ] Los headers comunes van en un interceptor. El logging está solo en debug y no imprime credenciales (`redactHeader`).
- [ ] HTTP en claro solo en el source set `debug` y limitado al host de desarrollo. No hay `usesCleartextTraffic="true"` en el manifest principal.
- [ ] `INTERNET` está declarado; no se agregan permisos que no se usan.

## Room
- [ ] La Entity tiene una `@PrimaryKey` que identifica la fila de forma única (compuesta si un id no alcanza).
- [ ] El DAO lee con `Flow` lo que la UI observa, y escribe con `suspend`.
- [ ] La Database y los DAO se proveen desde `DatabaseModule`; la Database es `@Singleton`.
- [ ] La Entity no sale del repositorio: se mapea a modelo.
- [ ] Si cambia el esquema: o sube `version` con su `Migration`, o el PR avisa que hay que desinstalar la app (solo mientras no haya datos que conservar). No se agrega `fallbackToDestructiveMigration` sin acordarlo.

## UI y Compose
- [ ] Usa el tema del proyecto (`dynamicColor = false`), con colores y tipografía de `MaterialTheme`, sin `Color(0x…)` ni tamaños de fuente sueltos en la pantalla.
- [ ] Si el diseño no define modo oscuro, el tema no sigue `isSystemInDarkTheme()`. Probado: con el teléfono en modo oscuro, la app se ve igual que el diseño.
- [ ] Se parece al frame de Figma: la captura está en el PR, al lado del frame.
- [ ] Los textos visibles están en `strings.xml` y se leen con `stringResource`. Los íconos tienen `contentDescription` (o `null` si son decorativos).
- [ ] Reutiliza los componentes de `ui/components/`; un componente nuevo que sirve para más de una pantalla va ahí.
- [ ] La firma es `XxxScreen(uiState, onXxx…, modifier: Modifier = Modifier)` y el `modifier` se aplica a la raíz.
- [ ] Hay una `@Preview` privada con el tema del proyecto por cada estado relevante.
- [ ] Listas lazy con `key` estable y única. No hay una lista lazy dentro de un `verticalScroll` sin altura fija.
- [ ] Insets: el contenido no queda debajo de la barra de estado, la de navegación ni el teclado. El `innerPadding` del `Scaffold` se aplica con `consumeWindowInsets(innerPadding)` y los formularios usan `imePadding()`. Probado: con el teclado abierto no queda un hueco extra encima.
- [ ] Probado en pantalla chica y en horizontal: lo que no entra tiene scroll.

## Tests
- [ ] La lógica nueva no trivial (mappers, validaciones, clasificación de errores, cálculo del estado) tiene su test JUnit 4 en `app/src/test/`.
- [ ] Los tests no usan Android, Robolectric ni Hilt: las dependencias son falsos pasados por constructor.
- [ ] La lógica testeada no usa `android.*` (`Patterns`, `Uri`, `Log`, `TextUtils`) ni depende de `unitTests.returnDefaultValues`.
- [ ] Cada test prueba un comportamiento y su nombre lo dice.
