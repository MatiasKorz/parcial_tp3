# Conceptos para la defensa oral

Notas de estudio del equipo. Cada respuesta es corta y se tiene que poder decir en voz alta, sin mirar el código, y después mostrarse en el proyecto. Si una respuesta no coincide con lo que hace nuestro código, hay que corregir uno de los dos antes de la defensa.

## Índice
1. Inyección de dependencias y Hilt
2. Estado y flujos
3. Compose
4. Navegación
5. Capas y datos
6. Red
7. Room
8. Edge-to-edge

## 1. Inyección de dependencias y Hilt

**¿Qué es la inyección de dependencias (DI)?**
La clase recibe desde afuera lo que necesita, en general por constructor, en vez de crearlo. Ya es DI aunque no haya biblioteca: `ItemRepository(api, dao)`. La ventaja es que en un test se le puede pasar un **falso** (una implementación escrita a mano que simula la real) y que la clase no queda atada a una implementación concreta.

**¿Qué es Dagger?**
Una biblioteca de DI que **genera código al compilar**. A partir de las anotaciones (`@Inject`, `@Module`, `@Provides`, `@Component`) arma el **grafo de dependencias** (quién necesita a quién) y **lo valida**: si falta una **vinculación**, que es la receta para obtener un tipo, el proyecto no compila. Como el código ya está generado, en ejecución no usa reflexión para resolver el grafo.

**¿Qué agrega Hilt y cuál es su relación con Dagger?**
Hilt está construido **sobre Dagger**: usa su generación de código y su validación. Lo que agrega son **componentes estándar de Android**, ya atados al ciclo de vida (`SingletonComponent` para la app, `ActivityRetainedComponent`, `ViewModelComponent`, `ActivityComponent`…), y las anotaciones para conectarlos (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@InstallIn`). Así no hay que escribir a mano los `@Component` ni el código que los crea y los destruye con cada Activity o ViewModel.

**¿Qué construye Hilt? Compilación vs ejecución**
- **Al compilar** (procesador de anotaciones vía KSP): Hilt genera los componentes de Dagger para cada clase de Android anotada, las fábricas de cada tipo y una `Application` base que es el contenedor de toda la app. También valida el grafo, y cualquier error aparece en el build, no en el teléfono.
- **En ejecución**: esos componentes **crean** los objetos cuando alguien los pide, respetando el alcance. En nuestra app:
  - `OkHttpClient`, `Retrofit` y la `XxxApi` son `@Singleton`: una instancia para toda la app;
  - la `RoomDatabase` es `@Singleton`; los DAO se piden a la base;
  - los repositorios se construyen por su `@Inject constructor`;
  - los ViewModels (`@HiltViewModel`) se crean en el `ViewModelComponent` cuando un destino llama a `hiltViewModel()`, y viven mientras esa entrada del back stack exista.
- Lo que sigue siendo nuestro: diseñar las clases, decidir qué depende de qué y escribir las pruebas.

**¿Para qué sirve cada anotación?**

| Anotación | Para qué |
|---|---|
| `@HiltAndroidApp` | En la `Application`: dispara la generación de código y crea el contenedor de la app |
| `@AndroidEntryPoint` | En la Activity: Hilt puede inyectarle dependencias y darle ViewModels a sus composables |
| `@HiltViewModel` + `@Inject constructor` | Hilt sabe construir el ViewModel con sus dependencias; `hiltViewModel()` lo obtiene |
| `@Inject constructor` | Le indica a Hilt qué constructor usar para nuestras clases |
| `@Module` + `@InstallIn(X::class)` | Un lugar para enseñarle a construir lo que no es nuestro, y en qué componente queda disponible |
| `@Provides` | Una función que devuelve la instancia (Retrofit, Room, la API, el DAO) |
| `@Binds` | Ata una interfaz a su implementación |
| `@Singleton` | Una sola instancia por app |
| `@ApplicationContext` | Pide el `Context` de la aplicación |

**¿`@Singleton` y `@InstallIn(SingletonComponent::class)` no son lo mismo?**
No. Por defecto, las vinculaciones de Hilt **no tienen alcance**: cada pedido crea una instancia nueva. `@InstallIn` dice **dónde** está disponible la vinculación. `@Singleton` dice que se cree **una vez** por instancia de ese componente. El alcance tiene que coincidir con el componente donde se instala.

**¿Por qué no creamos Retrofit dentro del ViewModel?**
- **Acoplamiento**: el ViewModel quedaría atado a una implementación concreta de la red.
- **Testabilidad**: no se le podría pasar un falso.
- **Ciclo de vida**: el ViewModel vive lo que vive una pantalla; el cliente HTTP tiene que vivir lo que vive la app.
- **Una sola instancia**: un único `OkHttpClient` comparte el pool de conexiones y la configuración. Por eso lo provee un módulo con `@Provides @Singleton`.

**¿Por qué no instanciamos el ViewModel a mano?**
Android crea el ViewModel a través de un `ViewModelStore` para que sobreviva a la rotación. Si lo creás con su constructor como propiedad de la Activity, se recrea con ella y repite la carga. Además, Hilt arma toda la cadena (repositorio → API, DAO) sin que el ViewModel sepa cómo se construye.

**kapt vs KSP**
kapt corre procesadores de anotaciones de Java generando antes *stubs* de Java del código Kotlin: es lento. KSP lee directamente los símbolos de Kotlin. Con AGP 9, que trae Kotlin integrado, el plugin `kotlin-kapt` es incompatible: la documentación indica migrar a KSP y deja `com.android.legacy-kapt` solo para procesadores que no soportan KSP. Hilt y Room soportan KSP. Por eso Hilt y Room se declaran con `ksp(...)`.

## 2. Estado y flujos

**StateFlow vs LiveData vs Flow**
- **Flow**: flujo **frío**. No hace nada hasta que alguien lo recolecta, no tiene "valor actual" y cada recolector dispara su propia ejecución. Ejemplo: el `Flow` que devuelve un DAO.
- **StateFlow**: flujo **caliente** que **siempre tiene un valor** (pide uno inicial) y se lo entrega a cada nuevo suscriptor. No reemite un valor igual al actual (compara con `equals`) y, si el recolector es lento, solo ve el último. Es Kotlin puro (corrutinas), así que se puede testear sin Android. Es lo que exponen nuestros ViewModels.
- **LiveData**: observable de Android **consciente del ciclo de vida**: solo notifica a observadores activos. Es la alternativa anterior, pensada para Views. En Compose usamos StateFlow, y `collectAsStateWithLifecycle()` le suma la conciencia del ciclo de vida.
- Para decirlo en una línea: Flow para secuencias que se observan, StateFlow para el estado de una pantalla, LiveData es lo que había antes.

**`suspend` vs `Flow`: ¿cuándo cada uno?**
Una función `suspend` devuelve **un** resultado: sirve para una request HTTP o para una escritura en Room. Un `Flow` emite **muchos valores en el tiempo**: sirve para lo que cambia, como una lectura de Room que la UI observa. En la app, la API se consulta con `suspend` desde el repositorio y los datos locales se observan con `Flow` hasta la UI.

**¿Qué hace `suspend`?**
Marca una función que puede **suspenderse sin bloquear el hilo** mientras espera, por ejemplo la red. Solo se llama desde una corrutina o desde otra función `suspend`. `suspend` no significa "en segundo plano": significa "pausa sin bloquear".

**¿Por qué el ViewModel sobrevive a la rotación?**
Al rotar, Android destruye y recrea la Activity, pero conserva su `ViewModelStore`, y con él los ViewModels. El ViewModel se limpia (`onCleared()`) cuando su pantalla se descarta de verdad. Por eso la carga en `init` no se repite. No sobrevive a la muerte del proceso: para eso está `SavedStateHandle`.

**`collectAsState` vs `collectAsStateWithLifecycle`**
Las dos convierten un `Flow` en un `State` de Compose, y la pantalla se recompone cuando cambia el valor. `collectAsStateWithLifecycle()` además deja de recolectar cuando la app no está visible, y es la forma recomendada en Android. `collectAsState()` es la versión multiplataforma.

**¿Por qué `WhileSubscribed(5_000)` en `stateIn`?**
El flujo de arriba (por ejemplo, la consulta de Room) se mantiene activo mientras haya alguien observando, y 5 segundos más. Durante una rotación la UI deja de observar un instante: con ese margen la consulta no se cancela, así que no hay que volver a lanzarla.

## 3. Compose

**¿Qué es la recomposición?**
Compose vuelve a ejecutar los composables que leyeron un `State` que cambió. Los que reciben los mismos parámetros se saltean: los estables se comparan con `equals` y, con *strong skipping* (activo por defecto), los inestables por referencia. Por eso el estado se lee lo más abajo posible y las listas llevan `key`.

**`remember` vs `rememberSaveable`**
`remember` conserva un valor entre recomposiciones, pero lo pierde si se recrea la Activity (rotación) o el proceso. `rememberSaveable` además lo guarda en el estado de instancia (un `Bundle`), así que sobrevive a la rotación y a la recreación del proceso. Solo acepta tipos que entran en un `Bundle`, o un `Saver`. Ninguno sobrevive si el usuario cierra la app desde recientes.

**¿Qué pasa si declaro el estado dentro del composable?**
Sin `remember`, se reinicia en cada recomposición. Con `remember`, se pierde al rotar. Y si es estado de negocio, la lógica queda atada a la UI: no se puede testear y la carga se repite cada vez que se recrea la pantalla. Por eso el estado de negocio va en el ViewModel y la pantalla solo lo recibe.

**¿Por qué una `key` estable en `items(...)`?**
Por defecto, Compose identifica cada ítem por su **posición**. Si se inserta, se borra o se reordena, el estado de cada ítem (lo recordado, las animaciones) "salta" al vecino y se recompone de más. Con `key = { it.id }`, cada ítem se identifica por su id. La `key` tiene que ser **única** dentro de la lista (una repetida crashea) y de un tipo que entre en un `Bundle`, para que `rememberSaveable` dentro del ítem se pueda restaurar.

**¿Por qué no una `LazyColumn` dentro de un `Column` con `verticalScroll`?**
La lista lazy necesita una altura acotada para saber cuántos ítems componer, y el padre con scroll le ofrece altura infinita: lanza `IllegalStateException`. La solución es una sola lista lazy, con `item { }` para lo que no es lista.

**¿Qué es el flujo unidireccional (UDF)?**
El estado **baja** del ViewModel a la pantalla y los eventos **suben** de la pantalla al ViewModel como lambdas. La pantalla no modifica datos: avisa qué pasó. Así el estado tiene un solo dueño y es predecible.

## 4. Navegación

**¿Qué diferencia hay entre `navigate` y `popBackStack`?**
`navigate(ruta)` **apila** un destino nuevo en el back stack. Acepta opciones: `popUpTo(X) { inclusive = true }` saca destinos (por ejemplo, el login después de entrar) y `launchSingleTop = true` evita apilar dos veces el mismo destino. `popBackStack()` **desapila** el destino actual y vuelve al anterior. Devuelve `false` cuando sacó el último destino: el back stack queda vacío y se ve una pantalla en blanco. Por eso el botón "volver" no se muestra en el destino inicial, y un doble toque no puede desapilar dos veces (el evento se atiende solo si el destino que lo emite está `RESUMED`).

**¿Por qué las pantallas no reciben el `navController`?**
Para desacoplarlas: la pantalla recibe estado y emite eventos (`onBackClick`, `onItemClick`). Así funciona en una `@Preview` y en un test sin grafo, se puede reutilizar en otro flujo y todas las decisiones de navegación quedan en un solo lugar, el NavHost.

**¿Qué hacen `saveState` y `restoreState` en la barra inferior?**
Al cambiar de pestaña, `popUpTo(primeraPestaña) { saveState = true }` saca lo que había arriba y **guarda** su estado. Al volver a esa pestaña, `restoreState = true` lo **restaura**: el usuario vuelve a encontrar la pestaña como la dejó. `launchSingleTop` evita duplicar la pestaña si se toca dos veces.

**¿Qué es el NavHost?**
El composable que define el grafo: los destinos (`composable(ruta)`) y el destino inicial. El `NavHostController`, creado con `rememberNavController()`, maneja el back stack.

## 5. Capas y datos

**¿Cómo están organizadas las capas y por qué?**
UI → ViewModel → Repository → fuentes (Retrofit, Room), y cada capa habla solo con la de abajo. La pantalla dibuja estado, el ViewModel prepara ese estado y maneja eventos, y el repositorio es la única puerta a los datos: decide si vienen de la red o de la base. Cambiar una fuente no toca la UI, y cada capa se testea por separado.

**¿Por qué DTO, modelo y Entity separados?**
El **DTO** refleja el JSON tal cual viene del servidor, el **modelo** es lo que usa la app y la **Entity** es la fila de Room. Si el servidor o la tabla cambian, solo cambian el DTO o la Entity y su mapper: la UI sigue igual. El repositorio convierte con `toModel()` / `toEntity()`, y la UI no ve ni DTO ni Entity.

**¿Qué diferencia hay entre "vacío" y "error"?**
Vacío es una respuesta **válida** sin datos: se muestra un mensaje de vacío. Error es una **falla** (sin conexión, error del servidor, respuesta inválida): se muestra un mensaje de error con reintento. Mezclarlos le miente al usuario: le dice "no hay nada" cuando en realidad no se pudo cargar.

## 6. Red

**¿Para qué sirve `@GET`?**
Declara el método HTTP y la ruta **relativa** del endpoint. Retrofit genera la implementación de la interfaz. Los parámetros van con `@Path` o `@Query`, y el converter (Gson) transforma la respuesta en el DTO.

**¿Qué pasa si llamo a la API desde el hilo principal?**
Una función `suspend` de Retrofit llamada desde `viewModelScope` (que corre en Main) **no bloquea**: suspende, y Retrofit hace la red en sus propios hilos. Lo que falla es una llamada **bloqueante** en el hilo principal (`call.execute()`): Android lanza `NetworkOnMainThreadException`.

**¿Por qué la `baseUrl` termina en `/` y los paths no empiezan con `/`?**
Retrofit resuelve cada path contra la `baseUrl` como lo hace un navegador con un link relativo. Si la `baseUrl` tiene path (`…/api`) y no termina en `/`, Retrofit la rechaza al construirse. Un path con `/` inicial es absoluto y descarta el path de la `baseUrl`.

**¿Cómo se manejan los errores de red?**
Retrofit lanza `HttpException` ante un no-2xx (si el método devuelve el tipo directo), `IOException` ante fallas de conexión y errores de Gson ante un JSON inválido. Algunos errores de JSON también son `IOException`, así que se clasifican antes. Cada caso se traduce a un mensaje claro de `strings.xml`, nunca al texto crudo de la excepción. `CancellationException` no se atrapa: es la forma en que una corrutina se cancela.

## 7. Room

**¿Qué son Entity, DAO y Database?**
La **Entity** es la tabla (una clase con `@Entity`), el **DAO** declara las operaciones (`@Query`, `@Insert`, `@Delete`) y la **Database** (`@Database`) es el punto de acceso que expone los DAO. Room genera el código al compilar y verifica las consultas SQL.

**¿Por qué el DAO devuelve `Flow`?**
Porque Room **vuelve a emitir** cada vez que cambia la tabla. El ViewModel lo expone con `stateIn` y la UI se actualiza sola al insertar o borrar, sin recargar a mano. `Flow` observa; `suspend` hace una operación puntual.

**¿Para qué sirve `@PrimaryKey`?**
Identifica cada fila de forma única; toda Entity necesita una. Permite reemplazar con `OnConflictStrategy.REPLACE`, borrar por id y consultar si una fila existe. Puede ser autogenerada (`autoGenerate = true`), un id que viene del servidor o compuesta (`primaryKeys = [...]`).

**¿Qué pasa al cambiar el esquema?**
Room guarda la versión de la base. Si se sube `version` sin una `Migration`, la app crashea al abrir la base; si se cambia una Entity sin subir la versión, también. `fallbackToDestructiveMigration` evita el crash borrando todos los datos.

**Room vs SharedPreferences / DataStore**
SharedPreferences guarda pares clave-valor sin esquema ni consultas, con una API sincrónica que puede bloquear el hilo principal; hoy se reemplaza por **DataStore**, que es asíncrono y expone `Flow`. Sirven para preferencias pequeñas. Room es una base SQLite con tablas, consultas verificadas al compilar y `Flow`: sirve para registros.

## 8. Edge-to-edge

**¿Qué es edge-to-edge y por qué nos afecta?**
La app dibuja detrás de la barra de estado y la de navegación, ocupando toda la pantalla. Desde Android 15 es obligatorio para las apps con `targetSdk` 35 o más, y con `targetSdk` 36, en Android 16 ya no se puede desactivar. Si no se manejan los **insets** (el espacio que ocupan las barras del sistema y el teclado), hay contenido tapado.

**¿Cómo lo manejamos?**
`enableEdgeToEdge()` en `MainActivity`. El `Scaffold` calcula los insets y los entrega como `innerPadding`, que se aplica al contenido o se pasa como `contentPadding` de la lista. El teclado es otro inset (`WindowInsets.ime`): en formularios, `Modifier.imePadding()` sube el contenido para que el teclado no tape campos ni botones. El `Scaffold` no consume los insets que entrega: si su padding se aplica con `padding(innerPadding)`, se marca como consumido con `consumeWindowInsets(innerPadding)`. Si no, `imePadding()` vuelve a sumar la barra de navegación y queda un hueco sobre el teclado.
