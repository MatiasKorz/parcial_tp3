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
| 1.1 | Login - 01 — SignIn | `sign_in` | Inicio de la app | demostración | Hecha (validación visual pendiente) | Acceso de demostración sin Firebase; los controles opcionales (contraseña olvidada, proveedores, biometría) informan modo demostración |
| 1.2 | Login - 02 — SignUp | `sign_up` | SignIn ("Sign up here") | demostración | Hecha (validación visual pendiente) | Aviso de registro de demostración sin crear cuenta; permanece en SignUp; regreso a SignIn |
| 2.1 | Home / 01 — Dashboard | `home` | SignIn; pestaña Home | demostración | Hecha | Datos de ejemplo |
| 2.2 | Home / 02 — Notifications | `notifications` | Home | demostración | Hecha | Notificaciones de ejemplo |
| 3.1 | Voice / 01 — Idle | `voice_idle` | Home ("Start voice session") | demostración | Hecha | Estado del coach simulado; sin micrófono; los chips y "LAST CONVERSATION" llevan a destinos de ejemplo |
| 3.2 | Voice / 02 — Listening | `voice_listening` | Voice / 01 | demostración | Provisoria | Escucha simulada |
| 3.3 | Voice / 03 — Processing | `voice_processing` | Voice / 02 | demostración | Provisoria | Procesamiento simulado |
| 3.4 | Voice / 04 — Speaking | `voice_speaking` | Voice / 03 | demostración | Provisoria | Respuesta simulada |
| 3.5 | Voice / 05 — Conversation | `voice_conversation` | Voice / 04 | demostración | Provisoria | Conversación de ejemplo |
| 3.6 | Voice / 06 — Error | `voice_error` | Voice / 03 | demostración | Provisoria | Error simulado |
| 4.1 | Workout / 01 — Dashboard | `workout` | Pestaña Workout | demostración | Hecha (validación visual pendiente) | Datos del Figma; catálogo en Weekly Plan según NUC-01 (confirmación docente en REF-05); rutas existentes con id de demostración |
| 4.2 | Workout / 02 — Weekly Plan | `weekly_plan` | Workout / 01 | mixta: plan de demostración y catálogo funcional | Integrada (validación manual pendiente) | Conserva PAN-4.2; catálogo GET /v1/workouts leído desde Room (NUC-01), con IDs reales hacia Details; detalle real y estados pendientes de NUC-02/NUC-03 |
| 4.3 | Workout / 03 — Workout Details | `workout_details/{workoutId}` | Workout / 01 y 02 | demostración | Hecha (validación manual pendiente) | Datos del Figma; conserva workoutId hacia Pre Workout; guía con id de demostración; detalle real y estados pendientes de NUC-02/NUC-03 |
| 4.4 | Workout / 04 — Pre Workout | `pre_workout/{workoutId}` | Workout / 01 y 03 | demostración | Hecha (validación manual pendiente) | Preparación con textos del Figma; conserva workoutId hacia Active Workout; recomendación y micrófono con avisos; sin historial real |
| 4.5 | Workout / 05 — Active Workout | `active_workout/{workoutId}` | Workout / 04 | demostración | Hecha (validación manual pendiente) | Series, carga y descanso simulados en Compose; sin cronómetro real ni persistencia; Finish Workout abre el destino existente PAN-4.7 |
| 4.6 | Workout / 06 — Exercise Details | `exercise_details/{exerciseId}` | Workout / 03 | demostración | UI hecha (validación manual pendiente) | Guía Figma fija de Seated Cable Row con imagen original; filas actuales usan demo-exercise; identificación e instrucciones reales pendientes de NUC-02; NAV04/RF01 incompletos |
| 4.7 | Workout / 07 — Workout Complete | `workout_complete` | Workout / 05 | demostración | Provisoria | Resumen de ejemplo |
| 5.1 | Nutrition / 01 — Dashboard | `nutrition` | Pestaña Nutrition | funcional | Provisoria | Muestra el total diario desde Room |
| 5.2 | Nutrition / 02 — Today's Meals | `todays_meals` | Nutrition / 01 | funcional | Provisoria | Listado por fecha desde Room |
| 5.3 | Nutrition / 03 — Add Food | `add_food` | Nutrition / 01 y 02 | funcional | Provisoria | Entrada al registro manual |
| 5.4 | Nutrition / 04 — Voice Food Logging | `voice_food_logging` | Nutrition / 03 | demostración | Provisoria | Registro por voz simulado; sin micrófono |
| 5.5 | Nutrition / 05 — Food Analysis | `food_analysis` | Nutrition / 04 | demostración | Provisoria | Análisis simulado |
| 5.6 | Nutrition / 06 — Photo Analysis | `photo_analysis` | Nutrition / 03 | demostración | Provisoria | Análisis de foto simulado; sin cámara |
| 5.7 | Nutrition / 07 — Food Confirmation | `food_confirmation` | Nutrition / 05 y 06 | A definir (REF-05) | Provisoria | Funcional solo si el registro manual la usa para confirmar |
| 5.8 | Nutrition / 08 — Meal Details | `meal_details/{mealId}` | Nutrition / 02 | demostración | Provisoria | Detalle de la comida elegida |
| 6.1 | Progress / 01 — Today's Overview | `todays_overview` | Home / 01 | demostración | Hecha | Datos de ejemplo |
| 6.2 | Progress / 02 — Dashboard | `progress` | Pestaña Progress | demostración | Hecha | Datos de ejemplo |
| 6.3 | Progress / 03 — Workout Progress | `workout_progress` | Progress / 02 | demostración | Hecha | Datos de ejemplo |
| 6.4 | Progress / 04 — Nutrition Progress | `nutrition_progress` | Progress / 02 | demostración | Hecha | Datos de ejemplo |
| 6.5 | Progress / 05 — Health Trends | `health_trends` | Progress / 02 | demostración | Provisoria | Datos de ejemplo |
| 6.6 | Progress / 06 — Weekly Review | `weekly_review` | Progress / 02 | demostración | Provisoria | Datos de ejemplo |
| 7.1 | Profile / 01 — Dashboard | `profile` | Pestaña Profile | demostración | Hecha | Datos de ejemplo |
| 7.2 | Profile / 02 — Edit Profile | `edit_profile` | Profile / 01 ("Edit profile") | demostración (datos locales) | Hecha | Edición local de los seis campos; los valores guardados se mantienen durante la sesión; "Change photo" informa modo demostración sin cámara |
| 7.3 | Profile / 03 — Goals & Preferences | `goals_preferences` | Profile / 01 ("Your Goals") | demostración (datos locales) | Hecha | Selección local de objetivos (varios), frecuencia y tipo de entrenamiento; "Balanced" fija; lo guardado se mantiene durante la sesión y volver sin guardar lo descarta |
| 7.4 | Profile / 04 — Nutrition Preferences | `nutrition_preferences` | Profile / 01 | demostración | Provisoria | Datos de ejemplo |
| 7.5 | Profile / 05 — Voice & AI Settings | `voice_ai_settings` | Profile / 01 | demostración | Provisoria | Ajustes de ejemplo |
| 7.6 | Profile / 06 — Settings | `settings` | Profile / 01 (engranaje, "Connected Data", "Account") | demostración (datos locales) | Hecha | Notifications on/off se mantiene durante la sesión; Units, Appearance y el resto de las opciones informan modo demostración; "Alex Morgan" abre Edit Profile; "Log out" vuelve a SignIn y vacía la pila |

