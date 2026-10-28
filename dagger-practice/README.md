# Práctica de Dagger puro (EXT-07)

Una práctica de Dagger **sin Hilt**, aislada de la app. Arma a mano, en miniatura, el mismo grafo que Hilt arma solo en `:app`: un repositorio único, una API atada a su implementación y un objeto por pantalla que recibe el id de la ruta. Sirve para ver qué hace Hilt por nosotros (RF08).

## Por qué está aislada

- Es un **build de Gradle aparte**, con su propio `settings.gradle.kts`. El `settings.gradle.kts` de la raíz no la incluye, así que `:app` no la configura ni la compila, y su grafo de Hilt no cambia.
- Es Kotlin para la JVM, sin Android: no necesita emulador.
- Reusa el wrapper de Gradle y el catálogo `gradle/libs.versions.toml` del repo. Dagger va en la misma versión que Hilt (`hilt`), y el código lo genera KSP, igual que en la app.

## Cómo correrla

Desde la raíz del repo (necesita `JAVA_HOME`, igual que la app; ver `AGENTS.md`):

| Shell | Ejecutar | Tests |
|---|---|---|
| PowerShell | `.\gradlew.bat -p dagger-practice run` | `.\gradlew.bat -p dagger-practice test` |
| Git Bash | `./gradlew.bat -p dagger-practice run` | `./gradlew.bat -p dagger-practice test` |
| Linux, macOS o WSL | `./gradlew -p dagger-practice run` | `./gradlew -p dagger-practice test` |

Salida esperada de `run` (la hora cambia):

```text
Repository is a singleton: true
Workouts: Active Recovery, Upper Body Strength, Lower Body Strength (loaded at …)
Screen w02: Upper Body Strength
Screen w03: Lower Body Strength
Same presenter inside one screen: true
Different presenter between screens: true
Screens share the repository: true
```

## Qué hay en cada archivo

| Archivo | Qué muestra |
|---|---|
| `Data.kt` | Clases propias con `@Inject constructor` (`FakeWorkoutApi`, `WorkoutRepository`) y el calificador `@BaseUrl` |
| `Modules.kt` | `@Provides` para lo que no es nuestro (`Clock`) y `@Binds` para atar `WorkoutApi` a `FakeWorkoutApi`. Los módulos no llevan `@InstallIn` |
| `Components.kt` | El `@Component` raíz con su `Factory` y `@BindsInstance`, el `@Subcomponent` por pantalla y el scope propio `@ScreenScope` |
| `Main.kt` | Crea el grafo con la clase generada `DaggerPracticeComponent` y lo recorre |
| `PracticeComponentTest.kt` | Comprueba los scopes: `@Singleton` da siempre la misma instancia, `@ScreenScope` una por pantalla |

Para ver lo que genera Dagger, compilá y abrí `dagger-practice/build/generated/ksp/main/java/`. Ahí está `DaggerPracticeComponent.java`, junto con las fábricas `*_Factory`.

## Qué se escribe a mano con Dagger y Hilt genera o predefine

| Con Dagger puro (esta práctica) | Con Hilt (la app) |
|---|---|
| Se escribe el `@Component` raíz (`PracticeComponent`) con la lista de módulos | Hilt predefine `SingletonComponent` y lo genera a partir de `@HiltAndroidApp` |
| Cada componente enumera sus módulos: `@Component(modules = [...])` | Cada módulo dice dónde va con `@InstallIn(SingletonComponent::class)` |
| Se crea el componente en `main()` con `DaggerPracticeComponent.factory().create(...)` y se guarda la referencia | Hilt lo crea en la `Application` y lo guarda solo |
| Los valores de ejecución entran con `@BindsInstance` en la `Factory` (`baseUrl`) | Hilt ya provee el `Context` con `@ApplicationContext` |
| Se escribe un `@Subcomponent` por pantalla (`ScreenComponent`) y su scope (`@ScreenScope`) | Hilt predefine `ViewModelComponent` y `@ViewModelScoped` |
| El argumento de la pantalla entra con `@BindsInstance` (`workoutId`) | Hilt inyecta el `SavedStateHandle` con los argumentos de la ruta |
| Se pide cada objeto llamando al componente: `screenComponentFactory().create(id).presenter()` | `hiltViewModel()` en el destino de navegación; `@AndroidEntryPoint` inyecta en la Activity |
| Se exponen métodos en el componente (`workoutRepository()`) para sacar objetos | No hace falta: Hilt inyecta en las clases de Android anotadas |

**Lo que es igual en los dos:** `@Inject constructor`, `@Module`, `@Provides`, `@Binds`, `@Singleton` y los calificadores. Hilt es Dagger con esos componentes, scopes y puntos de entrada ya definidos para Android. Por eso el grafo se valida al compilar: si falta una vinculación, ni la práctica ni la app compilan.
