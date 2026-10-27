# VORA Coach

> README en construcción. Las demás secciones (instrucciones de ejecución, versiones utilizadas, uso de IA, etc.) las agrega ENT-01.
> 
> ## Inventario de pantallas

El inventario toma los nombres reales de los 37 frames de Figma (SPEC p.1, PPTX s.4) y es la referencia de cobertura. Se actualiza en el mismo PR que cambia el estado de una pantalla.

- **Destino en la app:** ruta de navegación definida en `ui/navigation/AppDestination.kt`.
- **Estado:** `funcional` (usa datos reales del núcleo) o `demostración` (navegación y datos de ejemplo, sin servicio real). `A definir` marca las filas que dependen de una pregunta abierta del equipo (REF-05).
- **Maqueta:** `Provisoria` es la pantalla de NAV-01 (nombre del frame y botones de recorrido); `Hecha` es el frame maquetado según Figma.
- Los estados del coach (Voice) pueden representarse dentro de una misma pantalla (SPEC p.8); hoy cada estado tiene su propio destino.
- Las funciones simuladas no usan micrófono ni cámara ni servicios reales (NAV05).

| Cód. | Frame de Figma | Destino en la app | Se llega desde | Estado | Maqueta | Qué se simula |
|---|---|---|---|---|---|---|
| 1.1 | Login - 01 — SignIn | `sign_in` | Inicio de la app | funcional con Firebase / demostración sin él (EXT-01) | Hecha (validación visual pendiente) | Con `google-services.json`, "Sign In" inicia sesión en Firebase Authentication y muestra los errores de credenciales o de red; sin él, acceso de demostración. Los controles opcionales (contraseña olvidada, proveedores, biometría) informan modo demostración |
| 1.2 | Login - 02 — SignUp | `sign_up` | SignIn ("Sign up here") | funcional con Firebase / demostración sin él (EXT-01) | Hecha (validación visual pendiente) | Con `google-services.json`, "Sign Up" crea la cuenta (Full Name como nombre visible) y abre Home; sin él, aviso de registro de demostración sin crear cuenta. Regreso a SignIn |
| 2.1 | Home / 01 — Dashboard | `home` | SignIn; pestaña Home | demostración | Hecha | Datos de ejemplo |
| 2.2 | Home / 02 — Notifications | `notifications` | Home | demostración | Hecha | Notificaciones de ejemplo |
| 3.1 | Voice / 01 — Idle | `voice_idle` | Home ("Start voice session") | demostración | Hecha | Estado del coach simulado; sin micrófono; los chips y "LAST CONVERSATION" llevan a destinos de ejemplo |
| 3.2 | Voice / 02 — Listening | `voice_listening` | Voice / 01 ("TAP TO TALK") | demostración / simulado | Hecha | Transcripción fija del Figma; sin micrófono real; micrófono activo abre Processing; Cancel y Atrás vuelven a Idle. [Recursos y navegación](docs/design/PAN-3.2-recursos.md) |
| 3.3 | Voice / 03 — Processing | `voice_processing` | Voice / 02 | demostración / simulado | UI hecha (validación manual pendiente) | Transcripción fija; sin consultas a servicios; avance simulado a Speaking tras 3 s activos; Cancel vuelve a Idle y Atrás a Listening. [Recursos y navegación](docs/design/PAN-3.3-recursos.md) |
| 3.4 | Voice / 04 — Speaking | `voice_speaking` | Voice / 03 | demostración / simulado | UI hecha (validación manual pendiente) | Respuesta fija; barra y controles de reproducción estáticos; sin audio real. Interrupt vuelve a Listening; chevron y Atrás a Idle. [Recursos y navegación](docs/design/PAN-3.4-recursos.md) |
| 3.5 | Voice / 05 — Conversation | `voice_conversation` | Voice / 01 (Type instead, LAST CONVERSATION y sugerencias) | demostración / simulado | UI hecha (validación manual pendiente) | Hilo inicial fijo del Figma; mensajes nuevos locales, sin historial persistido ni servicios del coach. Micrófono y audio simulados; + informa modo demostración. [Recursos y navegación](docs/design/PAN-3.5-recursos.md) |
| 3.6 | Voice / 06 — Error | `voice_error` | Mantener presionado el micrófono activo de Voice / 02 — Listening | demostración / simulado | UI hecha (validación manual pendiente) | Error y permisos simulados; Allow Access solo muestra un aviso. Try again vuelve a Listening; Type instead a Conversation; Not Now y Atrás a Idle. El toque normal del micrófono sigue hacia Processing. [Recursos y navegación](docs/design/PAN-3.6-recursos.md) |
| 4.1 | Workout / 01 — Dashboard | `workout` | Pestaña Workout | demostración | Hecha (validación visual pendiente) | Datos del Figma; catálogo en Weekly Plan según NUC-01 (confirmación docente en REF-05); rutas existentes con id de demostración |
| 4.2 | Workout / 02 — Weekly Plan | `weekly_plan` | Workout / 01 | mixta: plan de demostración y catálogo funcional | Integrada (validación manual pendiente) | Conserva PAN-4.2; catálogo GET /v1/workouts leído desde Room (NUC-01), con IDs reales hacia Details; carga, vacío y error con reintento (NUC-03) |
| 4.3 | Workout / 03 — Workout Details | `workout_details/{workoutId}` | Workout / 01 y 02 | funcional | Hecha (validación manual pendiente) | Nombre, nivel, duración y ejercicios de GET /v1/workouts/{workoutId} guardados en Room (NUC-02): se muestra lo de Room y se refresca al abrir; sin servidor, un detalle ya visto sale de Room con un aviso y "Retry", y uno nunca descargado muestra el error con "Retry" (NUC-03). Carga mientras dura la consulta; un id inexistente (404) muestra "Workout not found" sin reintento. "VORA COACH NOTE", "Edit workout" y el menú siguen de demostración; desde Workout / 01 abre un id de demostración (muestra "Workout not found") |
| 4.4 | Workout / 04 — Pre Workout | `pre_workout/{workoutId}` | Workout / 01 y 03 | demostración | Hecha (validación manual pendiente) | Preparación con textos del Figma; conserva workoutId hacia Active Workout; recomendación y micrófono con avisos; sin historial real |
| 4.5 | Workout / 05 — Active Workout | `active_workout/{workoutId}` | Workout / 04 | demostración | Hecha (validación manual pendiente) | Series, carga y descanso simulados en Compose; sin cronómetro real ni persistencia; Finish Workout abre el destino existente PAN-4.7 |
| 4.6 | Workout / 06 — Exercise Details | `exercise_details/{workoutId}/{exerciseId}` | Workout / 03 | funcional | UI hecha (validación manual pendiente) | Nombre, grupo muscular, instrucciones ("Performance Steps") y tip ("PRO FORM CUES") del ejercicio guardado en Room (NUC-02); la ruta lleva los dos ids porque los ids de ejercicio se repiten entre entrenamientos. La imagen original de Figma y "Ask Vora" siguen de demostración |
| 4.7 | Workout / 07 — Workout Complete | `workout_complete` | Workout / 05 | demostración | UI hecha (validación manual pendiente) | Métricas Figma fijas y valoración local no persistida; Done/Atrás vuelven a Dashboard y limpian el flujo terminado |
| 5.1 | Nutrition / 01 — Dashboard | `nutrition` | Pestaña Nutrition | funcional | UI hecha (validación visual pendiente) | Calorías y macros consumidas y comidas de hoy desde Room (una fila por comida; tipo vacío con "+ Add"); objetivos (2,100 kcal, 140/220/65 g), porcentaje sobre esos objetivos y consejo son de ejemplo del Figma; calendario → DatePicker → 5.2 en esa fecha; "View History" → 5.2; fila → 5.8; "+ Add" → 5.3; "Voice" → 5.4 y "Photo" → 5.6 sin pedir permisos; "Search" abre la búsqueda de alimentos en 5.7 (`food_confirmation/search`, EXT-05) |
| 5.2 | Nutrition / 02 — Today's Meals | `todays_meals` | Nutrition / 01 | funcional | Hecha | Comidas de la fecha desde Room agrupadas por tipo, total del día, flechas y DatePicker para cambiar la fecha |
| 5.3 | Nutrition / 03 — Add Food | `add_food` | Nutrition / 01 y 02 | funcional | UI hecha (validación visual pendiente) | Voz y foto llevan a 5.4 y 5.6 sin pedir permisos; "Search Database" abre la búsqueda de alimentos en 5.7 (`food_confirmation/search`, EXT-05) con el tipo de comida elegido; "Log Manually" abre el registro manual de RF03 en 5.7 (`food_confirmation/manual`) |
| 5.4 | Nutrition / 04 — Voice Food Logging | `voice_food_logging` | Nutrition / 03 ("Start Voice Logging") | demostración | Hecha | Escucha simulada: no usa micrófono ni pide permisos (NAV05); transcripción de ejemplo fija del Figma; "Done Speaking" abre el análisis (5.5); "Type instead" abre el registro manual (5.7 `manual`); la X y atrás vuelven al origen |
| 5.5 | Nutrition / 05 — Food Analysis | `food_analysis` | Nutrition / 04 | demostración | UI hecha (validación visual pendiente) | Análisis simulado, sin IA ni `POST /v1/nutrition/analyses`: entrada capturada fija del Figma y 3 pasos que avanzan solos (~3 s); al terminar abre 5.7 en modo `estimate` sin guardar nada y sale de la pila; "Cancel analysis", la X y atrás vuelven a 5.4 sin guardar |
| 5.6 | Nutrition / 06 — Photo Analysis | `photo_analysis` | Nutrition / 03 ("Snap a Photo") | demostración | UI hecha (validación visual pendiente) | Análisis de foto simulado: foto y estimación de ejemplo del Figma (520 kcal), sin cámara ni permisos; "Review & Add" abre la revisión en 5.7 (`food_confirmation/estimate`); "Retake Photo" informa modo demostración |
| 5.7 | Nutrition / 07 — Food Confirmation | `food_confirmation/{mode}` | Nutrition / 03 ("Log Manually", "Search Database"), 01 ("Search"), 05 y 06 | funcional | UI hecha (validación visual pendiente) | `manual`: formulario de RF03 (nombre, fecha sin futuras, tipo, calorías y macros) con validación, guarda en Room y abre el listado en la fecha de la comida; `estimate`: estimación fija del Figma (3 ingredientes, 520 kcal), "Add to Lunch" la guarda en Room para hoy; `-`, `+`, "Remove", "+ Add Ingredient" y "Save without logging" informan modo demostración; `search` (EXT-05): alimentos buscados en `GET /v1/foods` con `-`/`+` de a 10 g, "Remove", "+ Add Ingredient" (abre la búsqueda) y MEAL TOTALS calculados; "Add to …" los guarda en Room. Ver [EXT-05](#búsqueda-de-alimentos-y-cálculo-nutricional-extra-ext-05) |
| 5.8 | Nutrition / 08 — Meal Details | `meal_details/{mealId}` | Nutrition / 01 y 02 | funcional (Room) | Hecha | Tipo, fecha, calorías y macros de la comida guardada; eliminar con confirmación; anillo y consejo de ejemplo; Edit/Duplicate en modo demostración |
| 6.1 | Progress / 01 — Today's Overview | `todays_overview` | Home / 01 | demostración | Hecha | Datos de ejemplo |
| 6.2 | Progress / 02 — Dashboard | `progress` | Pestaña Progress | demostración | Hecha | Datos de ejemplo |
| 6.3 | Progress / 03 — Workout Progress | `workout_progress` | Progress / 02 | demostración | Hecha | Datos de ejemplo |
| 6.4 | Progress / 04 — Nutrition Progress | `nutrition_progress` | Progress / 02 | demostración | Hecha | Datos de ejemplo |
| 6.5 | Progress / 05 — Health Trends | `health_trends` | Progress / 02 | demostración | Hecha | Datos de ejemplo |
| 6.6 | Progress / 06 — Weekly Review | `weekly_review` | Progress / 02 | demostración | Hecha | Datos de ejemplo; "Get dinner ideas" informa modo demostración |
| 7.1 | Profile / 01 — Dashboard | `profile` | Pestaña Profile | mixta: nombre del perfil (Firestore con sesión, EXT-02) y resto de ejemplo | Hecha | El nombre sale del perfil guardado; snapshot, personalización y filas son datos de ejemplo |
| 7.2 | Profile / 02 — Edit Profile | `edit_profile` | Profile / 01 ("Edit profile") | funcional con sesión de Firebase / demostración sin ella (EXT-02) | Hecha | Con sesión, los seis campos se guardan en Firestore (`users/{uid}`) y se recuperan al reabrir; sin sesión, edición local durante la sesión. "Change photo" informa modo demostración sin cámara |
| 7.3 | Profile / 03 — Goals & Preferences | `goals_preferences` | Profile / 01 ("Your Goals") | demostración (datos locales) | Hecha | Selección local de objetivos (varios), frecuencia y tipo de entrenamiento; "Balanced" fija; lo guardado se mantiene durante la sesión y volver sin guardar lo descarta |
| 7.4 | Profile / 04 — Nutrition Preferences | `nutrition_preferences` | Profile / 01 ("Your Preferences") | demostración (datos locales) | Hecha | Selección local de tipo de alimentación (una), alimentos preferidos y prioridades (varios); los ingredientes a evitar se agregan con el campo y se quitan tocando el chip; "Meal Preferences" informa modo demostración; lo guardado se mantiene durante la sesión y volver sin guardar lo descarta |
| 7.5 | Profile / 05 — Voice & AI Settings | `voice_ai_settings` | Profile / 01 ("Review preferences") | demostración (datos locales) | Hecha | Estilo de coaching, estilo de respuesta y cinco interruptores que se mantienen durante la sesión; "Preview" informa modo demostración sin audio ni micrófono; "Privacy & permissions" abre Settings |
| 7.6 | Profile / 06 — Settings | `settings` | Profile / 01 (engranaje, "Connected Data", "Account") | demostración (datos locales) | Hecha | Notifications on/off se mantiene durante la sesión; Units, Appearance y el resto de las opciones informan modo demostración; "Alex Morgan" abre Edit Profile; "Log out" cierra la sesión de Firebase (si hay), vuelve a SignIn y vacía la pila |

## Estados de las consultas remotas y escenarios del mock (RF05)

El catálogo de entrenamientos (sección "Workout catalog" de Workout / 02 — Weekly Plan) y Workout / 03 — Workout Details muestran sus estados dentro del mismo frame, sin pantallas nuevas.

**Catálogo** (`GET /v1/workouts`):

| Estado | Cuándo | Qué se ve |
|---|---|---|
| Carga | mientras dura `GET /v1/workouts` | indicador "Loading workouts…"; si ya hay catálogo guardado, el indicador aparece encima de la lista |
| Contenido | la consulta trae entrenamientos | la lista del catálogo |
| Vacío | el servidor responde una lista vacía | "No workouts yet" |
| Error con reintento | 503, JSON inválido, timeout o servidor detenido | si no hay catálogo guardado, un error con el botón "Retry"; si lo hay, la lista guardada con un aviso y "Retry" |

**Detalle** (`GET /v1/workouts/{workoutId}`):

| Estado | Cuándo | Qué se ve |
|---|---|---|
| Carga | mientras dura la consulta | indicador "Loading workout…"; si el detalle ya está guardado, aparece encima del contenido |
| Contenido | la consulta trae el entrenamiento, o ya estaba en Room | nombre, nivel, duración y ejercicios |
| Vacío | no aplica: `empty` solo vacía listas, el detalle responde normal | — |
| Error con reintento | 503, JSON inválido, timeout o servidor detenido | si el detalle no está guardado, "Couldn't load this workout" con "Retry"; si lo está, el detalle guardado con un aviso y "Retry" |
| No encontrado | el servidor responde 404 (id inexistente) | "Workout not found", sin reintento porque repetir la consulta no lo cambia |

**Política de caché:** el catálogo y los detalles viven en Room y ninguna respuesta los borra. Una respuesta vacía muestra el estado vacío pero no vacía la caché, así que si después se detiene el servidor vuelve a verse el catálogo guardado junto con el error. Un error nunca toca Room: el catálogo, los detalles guardados y las comidas registradas siguen disponibles.

### Cómo provocar los escenarios en la demo

El mock solo cambia de escenario con los headers `X-Mock-Scenario` y `X-Mock-Delay-Ms`. En los builds **debug** la app los agrega desde un interceptor de OkHttp, y se cambian en vivo desde una terminal con `adb` (incluido en el SDK de Android, en `platform-tools`), sin recompilar. Los builds release no envían esos headers.

```bash
# Escenario: empty, error o malformed. delay en milisegundos (0 a 10000)
adb shell am broadcast -n com.example.examentp3/.debug.MockScenarioReceiver --es scenario error
adb shell am broadcast -n com.example.examentp3/.debug.MockScenarioReceiver --es scenario empty
adb shell am broadcast -n com.example.examentp3/.debug.MockScenarioReceiver --es scenario malformed
adb shell am broadcast -n com.example.examentp3/.debug.MockScenarioReceiver --ei delay 3000
# Volver al escenario normal
adb shell am broadcast -n com.example.examentp3/.debug.MockScenarioReceiver
```

Después de cambiar el escenario, tocá "Retry" o volvé a abrir Weekly Plan o el detalle. El escenario se mantiene hasta que se cambia o se cierra la app. Para el paso 4 (sin conexión), detené el mock con Ctrl+C y levantalo de nuevo antes de tocar "Retry".

## Firebase Authentication (extra EXT-01)

Registro, inicio y cierre de sesión con email y contraseña. **Es opcional:** sin configurar Firebase, la app compila igual y el acceso funciona en modo demostración (SPEC p.1).

### Cómo funciona sin y con Firebase

- **Sin `app/google-services.json`** (un clon limpio): `app/build.gradle.kts` no aplica el plugin `com.google.gms.google-services`, no se crea ninguna `FirebaseApp` y `FirebaseModule` provee `FirebaseAuth` como `null`. "Sign In" abre Home como antes, "Sign Up" muestra el aviso de registro simulado y "Log out" solo vuelve a SignIn.
- **Con `app/google-services.json`**: se aplica el plugin y la app usa Firebase Authentication. `AuthRepository` es la única clase que llama a `FirebaseAuth`; las pantallas y los ViewModels no lo conocen.
- No hay endpoints de login en el mock ni en ningún servidor propio (SPEC p.3): la app habla solo con Firebase.

### Configurarlo

1. Entrá a la [consola de Firebase](https://console.firebase.google.com/) y creá un proyecto (Google Analytics no hace falta).
2. **Build → Authentication → Comenzar → Sign-in method:** habilitá **Correo electrónico/contraseña**.
3. **Configuración del proyecto → Tus apps → Agregar app → Android**, con el nombre de paquete `com.example.examentp3`. El SHA-1 no hace falta para email y contraseña.
4. Descargá `google-services.json` y copialo en `app/google-services.json`.
5. Compilá (`.\gradlew.bat :app:assembleDebug`) y corré la app.

**`google-services.json` no se sube al repo** (está en `.gitignore`): el repo es público y cada integrante usa el archivo de su proyecto, o el que el equipo comparta por un canal privado. El archivo tiene la configuración del cliente (id del proyecto y una API key de cliente), no claves administrativas, pero igual queda fuera de git. Nunca subas una cuenta de servicio (`*-firebase-adminsdk-*.json`) ni contraseñas.

### Reglas de acceso

Firebase Authentication no tiene reglas propias: las reglas de seguridad se escriben para Firestore, que usa el perfil (EXT-02), y están en `firestore.rules` (EXT-03). En la consola conviene dejar habilitado **solo** el proveedor Correo electrónico/contraseña y, en **Authentication → Configuración**, mantener activada la protección contra la enumeración de correos (viene activada en los proyectos nuevos).

### Probarlo (paso 5 de la demo)

1. **Registro:** SignIn → "Sign up here". Completá Full Name, un email nuevo y una contraseña de 8 caracteres o más → "Sign Up". Abre Home, y la cuenta aparece en la consola en **Authentication → Usuarios**.
2. **Cierre:** Profile → engranaje → "Log out". Vuelve a SignIn, y el botón atrás sale de la app en vez de volver a Home.
3. **Inicio:** en SignIn, el mismo email y contraseña → "Sign In". Abre Home; atrás no vuelve a SignIn.
4. **Errores:** una contraseña incorrecta muestra "Incorrect email or password."; registrar el mismo email otra vez muestra que la cuenta ya existe; con el emulador en modo avión, un mensaje de conexión. Ninguno cierra la app.

## Perfil en Firestore (extra EXT-02)

Los seis campos de **Profile / 02 — Edit Profile** (First Name, Last Name, Date of Birth, Height, Weight y Gender) se guardan en Firestore para la cuenta que inició sesión y se recuperan al volver a abrir la app. El nombre se ve en Edit Profile y en la tarjeta de **Profile / 01 — Dashboard**.

### Cómo funciona

- **Dónde se guarda:** en el documento `users/{uid}`, donde `{uid}` es el id de la cuenta en Firebase Authentication. Las reglas de EXT-03 (`firestore.rules`) solo dejan que cada cuenta lea y escriba su propio documento. Se guardan como texto, igual que en el formulario. Objetivos y preferencias (7.3 a 7.5) siguen siendo locales.
- **Quién habla con Firestore:** solo `ProfileRepository`, que recibe `FirebaseFirestore` desde `FirebaseModule` y el `uid` desde `AuthRepository`. Las pantallas reciben estado y lambdas; los ViewModels piden el perfil al repositorio.
- **Guardar:** "Save changes" actualiza la copia en memoria al instante y manda la escritura a Firestore sin esperar la respuesta. Sin conexión, Firestore la guarda en su caché local y la envía cuando vuelve la red, así que la pantalla nunca se bloquea.
- **Recuperar:** Edit Profile y el dashboard piden el perfil a Firestore una vez por ViewModel (no se repite al rotar). Si la cuenta todavía no guardó nada o no hay conexión, se ve lo que hay en memoria.
- **Sin sesión o sin `google-services.json`:** se usa el perfil de demostración en memoria (SPEC p.3), y las pantallas de Perfil siguen navegables. Al cerrar sesión o entrar con otra cuenta, los datos de la cuenta anterior se descartan.

### Configurarlo

Además de los pasos de Firebase Authentication: en la consola, **Firestore Database → Crear base de datos**, con la base `(default)`, edición estándar y **modo de producción**. Después, desplegá las reglas del repo (sección "Reglas de Firestore"). Sin las reglas, el modo producción rechaza todas las lecturas y escrituras.

### Probarlo (paso 5 de la demo)

1. Iniciá sesión con una cuenta (EXT-01) → Profile → "Edit profile". Cambiá los campos → "Save changes". El dashboard muestra el nombre nuevo.
2. En la consola, **Firestore Database → Datos**: aparece `users/<uid>` con los seis campos. El `uid` es el que muestra **Authentication → Usuarios** para esa cuenta.
3. Cerrá la app (desde Recientes), volvé a abrirla e iniciá sesión con la misma cuenta: Edit Profile y el dashboard muestran lo guardado.
4. "Log out" y entrá con otra cuenta: no ve el perfil de la primera.
5. Con el emulador en modo avión, Profile y Edit Profile siguen navegables y "Save changes" no bloquea la pantalla.

## Reglas de Firestore (extra EXT-03)

Las reglas de seguridad aíslan los datos de cada cuenta: el perfil vive en `users/{uid}` (EXT-02), y solo la cuenta con ese `uid` puede leerlo o escribirlo. Sin sesión no se accede a nada, y cualquier otra ruta se rechaza.

| Archivo | Qué es |
|---|---|
| [`firestore.rules`](firestore.rules) | Las reglas. Es la única copia: lo que se despliega sale de acá |
| [`firebase.json`](firebase.json) | Le dice a la Firebase CLI dónde están las reglas y en qué puerto corre el emulador |
| [`.firebaserc`](.firebaserc) | El proyecto de Firebase del equipo (`vora-coach-tp3-659c9`). El Project ID no es secreto |
| [`firestore-rules-tests/`](firestore-rules-tests/) | Tests de las reglas con dos cuentas, sobre el emulador de Firestore |
| [`docs/evidencia/EXT-03-reglas-firestore.md`](docs/evidencia/EXT-03-reglas-firestore.md) | **Evidencia de la prueba con dos cuentas** |

### Requisitos

- [Node.js](https://nodejs.org/) 20 o más nuevo.
- Firebase CLI: `npm install -g firebase-tools`, y después `firebase login` con una cuenta que sea miembro del proyecto.
- Java 21 o más nuevo para el emulador. Sirve el JDK de Android Studio: en PowerShell, `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"; $env:Path = "$env:JAVA_HOME\bin;$env:Path"`.

### Correr los tests

Desde la raíz del repo:

```powershell
npm install --prefix firestore-rules-tests
firebase emulators:exec --only firestore --project demo-vora-coach "npm test --prefix firestore-rules-tests"
```

La primera vez, la CLI descarga el emulador de Firestore (unos 130 MB, en `~/.cache/firebase`). El proyecto `demo-vora-coach` es local: el prefijo `demo-` hace que el emulador no se conecte a ningún proyecto real. Los 11 tests tienen que pasar.

### Aplicar las reglas al proyecto

```powershell
firebase deploy --only firestore:rules
```

Sube `firestore.rules` al proyecto de `.firebaserc`. Para comprobar que lo desplegado es lo mismo que el repo, abrí en la consola **Firestore Database → Reglas** y comparalo con `firestore.rules`. Cada despliegue queda en el historial de esa pestaña.

No cambies las reglas desde la consola: el cambio no queda en el repo y la próxima vez que alguien despliegue se pierde. Se cambia `firestore.rules`, se corren los tests y se despliega.


## Búsqueda de alimentos y cálculo nutricional (extra EXT-05)

"Search Database" de `Nutrition / 03 — Add Food` y "Search" de `Nutrition / 01 — Dashboard` abren `Nutrition / 07 — Food Confirmation` en el modo `search`. El Figma no tiene un frame de búsqueda, así que el buscador es una hoja dentro de Food Confirmation que se abre con "+ Add Ingredient". Al entrar sin alimentos se abre sola.

| Paso | Qué pasa |
|---|---|
| Buscar | `GET /v1/foods` con `q` (se omite si está vacío, así la hoja arranca con todos los alimentos), `page` y `pageSize=10`. Espera 300 ms después de la última tecla |
| Más resultados | "Load more" pide la página siguiente mientras `page * pageSize < total` (`meta`) |
| Elegir | Cada alimento elegido aparece como tarjeta de ingrediente con 100 g. Elegir de nuevo el mismo suma 100 g |
| Cantidad | "-" y "+" mueven de a 10 g (mínimo 10 g). Calorías, proteínas, carbohidratos y grasas se calculan desde `nutritionPer100g`: valor × gramos / 100 (calorías enteras, macros con un decimal) |
| Revisar | MEAL TOTALS suma las porciones; "Remove" quita un alimento. Nada se guarda hasta tocar "Add to …" (SPEC p.10) |
| Guardar | "Add to …" guarda una comida en Room (RF03): nombre con los alimentos unidos por " · ", fecha de hoy, el tipo elegido (o Lunch) y los totales. Aparece en Today's Meals y en el total diario (RF04). Nada se manda al mock (SPEC p.4) |

**Estados** (los mismos que NUC-03): "Searching foods…" mientras carga; "No foods found" con una respuesta vacía o `X-Mock-Scenario: empty`; con `error`, `malformed` o el mock detenido, "Couldn't search foods" con "Retry", o un aviso con "Retry" debajo de los resultados que ya se veían. Guardar sin alimentos muestra "Add at least one food from the search.".

**Sin cambios:** el registro manual (`food_confirmation/manual`, NUC-04) y la estimación simulada (`estimate`) funcionan igual. "Save without logging" sigue informando modo demostración.

**Decisiones del equipo** (preguntas abiertas de la tarjeta): la búsqueda va dentro de Food Confirmation y no en una pantalla nueva; la cantidad arranca en 100 g y se ajusta de a 10 g.
