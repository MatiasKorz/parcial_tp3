# AGENTS.md

Instrucciones de proyecto para agentes de IA (Codex, Claude Code y otros). Es la **fuente única**: `CLAUDE.md` importa este archivo y solo agrega lo propio de Claude Code. Si cambiás una regla, cambiala acá.

## El proyecto

- **VORA Coach:** app Android en Kotlin + Jetpack Compose, para el parcial domiciliario de Taller de Programación 3 (ORT Argentina). Hito el 21/10/2026, entrega el 28/10/2026.
- **Stack:** Compose + Material 3, Navigation Compose, ViewModel + Hilt, Retrofit + Gson (OkHttp), Room. Firebase Auth está declarado pero no configurado.
- **Estructura:** un módulo `:app`, paquete `com.example.examentp3`, con `ui/screens`, `ui/navigation`, `ui/theme`, `viewmodel`, `di` y `data/remote`. Seguí las guías de la skill `tp3-guidelines`. Si el código existente no las cumple, ganan las guías: alineá solo lo que toca tu tarjeta, sin refactors fuera de alcance.
- **Versiones** (en `gradle/libs.versions.toml` y `gradle/wrapper/gradle-wrapper.properties`): AGP 9.3.3, Gradle 9.5.0, Kotlin 2.2.10, KSP 2.3.10, Hilt 2.60.1, Room 2.8.5, Retrofit 3.0.0, Compose BOM 2026.02.01, `compileSdk`/`targetSdk` 37, `minSdk` 26, bytecode Java 17. El daemon de Gradle pide JDK 25 (`gradle/gradle-daemon-jvm.properties`) y lo descarga si no lo encuentra.
- **No cambies versiones ni agregues dependencias** dentro de una tarjeta de pantalla. Eso va en su propia tarjeta y se acuerda en el grupo.

## Dónde está la consigna

- **Las tareas viven en el board de Trello:** <https://trello.com/b/fDQQV0fL/tp3-parcial-vora-coach>. Cada tarjeta es un spec y su nombre empieza con la key (`REF-01`, `NUC-02`, `PAN-4.3`…). Tiene descripción, checklist "Criterios de aceptación" y link al frame de Figma.
- **Tarjetas de referencia** (lista "Referencias"):
  - `REF-02`: la consigna.
  - `REF-03`: el Figma.
  - `REF-04`: el mock del servidor y su contrato.
  - `REF-05`: preguntas abiertas. Las dudas que la tarjeta no resuelve se anotan ahí.
  - `REF-06`: la definición de terminado de cualquier tarjeta.
- **Tarjetas de arranque** (lista "Pendientes"): `AMB-01` (ambiente de cada integrante) y `GIT-01` (acceso al repo, identidad e higiene).
- La consigna original en Word está en `docs/`. Para trabajar, usá las tarjetas.
- Si la tarjeta y el Figma no alcanzan para decidir algo, **preguntale al humano** y proponé anotar la duda en `REF-05`. No completes huecos inventando.

## Compilar y correr (Windows)

Requiere Android Studio con el SDK de API 37 (ver `docs/onboarding/README.md`). Desde la raíz del repo:

| Shell | Compilar |
|---|---|
| PowerShell | `.\gradlew.bat :app:assembleDebug` |
| CMD | `gradlew.bat :app:assembleDebug` |
| Git Bash | `./gradlew.bat :app:assembleDebug` |
| Linux, macOS o WSL con SDK | `./gradlew :app:assembleDebug` (si da *Permission denied*: `bash gradlew :app:assembleDebug`; el bit de ejecución y los finales de línea de `gradlew` se resuelven en `GIT-01`) |

- `gradlew.bat` necesita `JAVA_HOME`. Si no está definida (ni hay `java` en el PATH), falla con "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH": apuntala al JDK que trae Android Studio (`C:\Program Files\Android\Android Studio\jbr`).
- **Correr la app:** el botón Run de Android Studio con un emulador abierto, o `.\gradlew.bat :app:installDebug`.
- **Tests unitarios:** `.\gradlew.bat :app:testDebugUnitTest`.
- El primer build descarga Gradle, las dependencias y quizás el JDK 25: necesita internet y tarda.

## El mock del servidor

- **No está en el repo y no se sube.** Es el paquete `mock.zip` del docente: cada integrante lo descarga del material de la materia y lo descomprime **fuera** del repo.
- **Requiere Python ≥ 3.9**, sin dependencias.
- **Levantarlo:** `python mock/server.py` desde donde lo descomprimiste (en Windows también `py mock\server.py`). Escucha en `http://127.0.0.1:8000/`.
- **Desde el emulador** la URL es `http://10.0.2.2:8000/`.
- El contrato (endpoints, envelope, escenarios con el header `X-Mock-Scenario`) está en `/openapi.json` y en el Swagger de `/docs` del mock, y resumido en `REF-04`. **Es la única fuente de endpoints.**

## Reglas de trabajo para el agente

1. **Una tarjeta por vez.** Antes de tocar código, leé la tarjeta completa con `python tools/trello/trello.py show <KEY>`: descripción, criterios de aceptación y link al Figma.
2. **Guías del equipo.** Usá la skill `tp3-guidelines` (ver abajo) y seguí `docs/onboarding/CONVENCIONES.md`. Si no la encontrás, avisale al humano y seguí con este archivo.
3. **Plan antes de editar.** Proponé qué archivos vas a tocar y cómo, y esperá que el humano lo apruebe.
4. **No inventes** pantallas, rutas, endpoints, campos ni datos que no estén en el Figma (`REF-03`) o en el contrato del mock (`REF-04`). Si falta algo, preguntá.
5. **Alcance acotado.** Tocá solo lo que pide la tarjeta: nada de refactors, formateos ni cambios de versiones ajenos a la tarea. Si ves algo para mejorar, proponelo como tarjeta nueva.
6. **Compilá antes de dar algo por terminado** (`assembleDebug`) y mostrá el resultado. Si la tarea es de UI, pedile al humano que la compare con el frame de Figma en el emulador.
7. **No subas secretos:** `local.properties`, tokens de Trello, claves de Firebase admin, contraseñas. Nunca imprimas `TRELLO_KEY` ni `TRELLO_TOKEN`. El repo es **público**.
8. **No commitees ni pushees sin permiso explícito del humano.** Tampoco crees o borres ramas remotas, hagas force push a `main` ni mergees PRs.
9. **Al terminar, proponé** el mensaje de commit y el título del PR según `docs/onboarding/CONVENCIONES.md`, el texto del PR con la plantilla `.github/pull_request_template.md` y el comentario para la tarjeta (link al PR y uso de IA). Revisá la definición de terminado (`REF-06`).
10. **Explicá lo que hiciste** de forma que el humano lo entienda: cada integrante defiende el código en un oral.

## Aprobaciones configuradas en el repo

- **Claude Code:** `.claude/settings.json` arranca las sesiones de terminal en modo manual y pide confirmación para `git commit`, `git push` y las escrituras de `trello.py` en cualquier modo.
- **Codex:** `.codex/config.toml` usa sandbox `read-only` con aprobaciones `on-request`: lee y planifica solo, y pide permiso antes de editar o de correr comandos que escriban, usen la red o toquen `.git`. Codex carga ese archivo solo si confiás en el proyecto; si no, arrancalo con `codex --sandbox read-only --ask-for-approval on-request`.

## Uso de IA

El uso de IA generativa **se declara en el README de la entrega** (herramienta y para qué se usó). Cada PR lo registra en la sección "Uso de IA" de la plantilla, y ese texto también va como comentario en la tarjeta (lo pide la definición de terminado, `REF-06`), para que `ENT-04` arme la declaración final. El autor de cada commit es el integrante, sin trailers de coautoría de herramientas de IA.

## Flujo con Trello

Listas del board: **Referencias · Pendientes · Por hacer · En curso · Bloqueado · En revisión · Terminado**.

```text
Por hacer → En curso (asignada) → En revisión (PR abierto) → Terminado (PR mergeado)
                 ↘ Bloqueado (con un comentario que diga por qué)
En revisión → En curso (la revisión pidió cambios, con un comentario)
```

Script: `tools/trello/trello.py` (detalle en `docs/onboarding/TRELLO.md`). Necesita las variables `TRELLO_KEY`, `TRELLO_TOKEN` y `TRELLO_BOARD`.

| Acción | Comando | ¿El agente lo puede hacer solo? |
|---|---|---|
| Leer tarjeta | `python tools/trello/trello.py show <KEY>` | Sí |
| Asignarse | `python tools/trello/trello.py assign <KEY>` | No, pedí aprobación |
| Mover | `python tools/trello/trello.py move <KEY> "En revisión"` | No, pedí aprobación |
| Tildar criterio | `python tools/trello/trello.py check <KEY> "<texto del ítem>"` | No, pedí aprobación |
| Comentar | `python tools/trello/trello.py comment <KEY> "<texto>"` | No, pedí aprobación |

## Skill del equipo: `tp3-guidelines`

`tp3-guidelines` reúne las guías y convenciones de desarrollo del equipo: arquitectura, navegación, estado, Hilt, Retrofit, Room, UI, manejo de errores, tests y conceptos para la defensa oral. Está en el repo como una carpeta con un `SKILL.md` (estándar abierto [Agent Skills](https://agentskills.io)) en:

| Agente | Ubicación en el repo |
|---|---|
| Claude Code | `.claude/skills/tp3-guidelines/SKILL.md` |
| Codex | `.agents/skills/tp3-guidelines/SKILL.md` |

Como Windows no maneja bien los symlinks en git, la misma carpeta va copiada en las dos rutas. Las dos copias de `SKILL.md` se guardan en UTF-8 **sin BOM**: Codex descarta la skill si el archivo empieza con BOM.
