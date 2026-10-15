# Onboarding: cómo arrancar en Windows

Esta guía te deja listo para trabajar en el parcial VORA Coach con un agente de IA (Claude Code o Codex). Seguila en orden.

| Archivo | Para qué |
|---|---|
| [`guia-agentes-ia.html`](guia-agentes-ia.html) | Qué es un agente, cómo gasta tokens y cómo lo usamos. **Leela primero**: abrila con doble clic, se lee en 10 minutos |
| Este README | La instalación y el flujo diario |
| [`CONVENCIONES.md`](CONVENCIONES.md) | Ramas, commits y PRs |
| [`TRELLO.md`](TRELLO.md) | Credenciales y script del board |
| [`PROMPT_INICIAL.md`](PROMPT_INICIAL.md) | El texto que pegás en el agente para arrancar |
| [`../../AGENTS.md`](../../AGENTS.md) | Las reglas que sigue el agente |

En el board también hay tarjetas para arrancar: **`AMB-01`** es el checklist de ambiente de cada integrante (tildá el tuyo a medida que avanzás con la sección 1), **`GIT-01`** cubre el acceso al repo y la identidad de git, **`REF-06`** es la definición de terminado de cualquier tarjeta y **`REF-05`** es donde se anotan las dudas abiertas.

En los comandos, reemplazá lo que está entre `< >`, incluidos los signos.

## 1. Requisitos

### 1.1 Git for Windows, con tu identidad (`GIT-01`)

1. Instalá Git desde <https://git-scm.com/download/win>. Trae Git Bash y Git Credential Manager, que te pide el login de GitHub la primera vez que pusheás.
2. Configuralo en PowerShell:

   ```powershell
   git config --global user.name "Tu Nombre"
   git config --global user.email "el-email-de-tu-cuenta-de-github"
   git config --global core.autocrlf true
   git config --global core.longpaths true
   ```

   El email tiene que estar asociado a tu cuenta de GitHub: así tus commits cuentan como tuyos.

### 1.2 Acceso al repo

1. Pedile a quien administra el repo que te invite como colaborador (**Settings → Collaborators → Add people**). Aceptá la invitación desde el email o desde GitHub.
2. Cloná el repo en una ruta **corta y fuera de OneDrive**. La URL está en el botón **Code** del repo en GitHub:

   ```powershell
   mkdir C:\dev
   cd C:\dev
   git clone <URL_DEL_REPO>
   ```

> Si usás WSL: Android Studio y el emulador corren en Windows, así que conviene tener el repo en Windows (`C:\dev\parcial_tp3`).

### 1.3 Android Studio, SDK y emulador (`AMB-01`)

El proyecto usa **AGP 9.3.3**, que necesita **Android Studio Quail 2 (2026.1.2) o posterior**. La versión estable hoy es **Rabbit 1 (2026.2.1)**: instalá esa.

1. **Requisitos de la máquina:**
   - Windows 10 u 11 de 64 bits, con procesador x86_64. Windows en ARM no está soportado.
   - 16 GB de RAM para Studio y el emulador (32 GB recomendados).
   - Al menos 16 GB libres de disco, mejor en SSD.
   - Virtualización activada en el BIOS. Comprobalo en **Administrador de tareas → Rendimiento → CPU → Virtualización: Habilitado**.
2. **Activá la aceleración del emulador (WHPX):**
   - Abrí **Activar o desactivar las características de Windows**.
   - Tildá **Plataforma del hipervisor de Windows** (*Windows Hypervisor Platform*).
   - Reiniciá.
3. **Instalá Android Studio** desde <https://developer.android.com/studio>, o en PowerShell:

   ```powershell
   winget install --id Google.AndroidStudio -e
   ```

4. **Primer arranque:** elegí la instalación **Standard**. El asistente baja el SDK, Platform-Tools y el emulador.
5. **SDK Manager** (**Settings → Languages & Frameworks → Android SDK**). Instalá:
   - en **SDK Platforms**: Android API 37;
   - en **SDK Tools**: Android SDK Build-Tools 36, Platform-Tools y Android Emulator.
6. **Abrí el proyecto** con **File → Open → `C:\dev\parcial_tp3`** y esperá el *Gradle sync*.
   - La primera vez tarda: baja Gradle 9.5.0, las dependencias y un JDK 25 (lo pide `gradle/gradle-daemon-jvm.properties`).
   - Necesita internet sin VPN que lo bloquee.
7. **Creá un emulador** en **Device Manager → Create Virtual Device**:
   - Elegí un teléfono.
   - Como imagen de sistema, **API 37, x86_64, Google APIs**.
   - Creá dos AVD: uno de referencia lo más cerca posible de **390 dp** de ancho, el de los frames de Figma (por ejemplo Pixel 5, ≈393 dp), y otro de otro tamaño (por ejemplo Small Phone o Medium Phone). La consigna pide que la app se vea bien en los dos (UI03, `AMB-01`). Corré uno a la vez.
8. **Verificá la aceleración:**

   ```powershell
   & "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -accel-check
   ```

   Tiene que decir `WHPX(...) is installed and usable.`
9. **Compilá desde la terminal.** `gradlew.bat` necesita `JAVA_HOME`: apuntalo al JDK que trae Android Studio y abrí una terminal nueva.

   ```powershell
   setx JAVA_HOME "C:\Program Files\Android\Android Studio\jbr"
   # en una terminal nueva, desde C:\dev\parcial_tp3:
   .\gradlew.bat :app:assembleDebug
   ```

   Tiene que terminar en `BUILD SUCCESSFUL`. En CMD el comando va sin `.\`; en Git Bash es `./gradlew.bat :app:assembleDebug` (con `.\` no anda). El botón **Run** de Android Studio compila e instala la app en el emulador.

### 1.4 Python, para el mock

1. Instalá Python 3.9 o superior desde <https://www.python.org/downloads/windows/>. Verificalo con `py --version` o `python --version`.
2. **El mock no está en el repo.** Bajá el paquete del docente (`mock.zip`) del material de la materia y descomprimilo **fuera del repo**, por ejemplo en `C:\dev\vora-mock`.
3. Levantalo y dejalo corriendo:

   ```powershell
   py C:\dev\vora-mock\mock\server.py
   ```

4. Probalo:
   - desde Windows, en <http://127.0.0.1:8000/> (probador web) y <http://127.0.0.1:8000/docs> (Swagger);
   - desde el emulador, abrí `http://10.0.2.2:8000/health` en Chrome.

## 2. Instalar el agente: Claude Code o Codex

Usá el que prefieras. Los dos leen las reglas del repo solos: Codex lee `AGENTS.md`, y Claude Code lee `CLAUDE.md`, que importa `AGENTS.md`.

### Claude Code

- **Cuenta:** necesitás un plan Claude Pro, Max, Team o Enterprise, o una cuenta de Console con crédito de API. El plan gratuito de claude.ai no incluye Claude Code.
- **Instalación:** en PowerShell (no hace falta ser administrador):

  ```powershell
  irm https://claude.ai/install.ps1 | iex
  ```

  También se puede con `winget install Anthropic.ClaudeCode`, pero esa vía no se actualiza sola.
- **Verificación:** abrí una terminal nueva y corré `claude --version` y `claude doctor`.
- **Login:** `cd C:\dev\parcial_tp3` y después `claude`. La primera vez abre el navegador para iniciar sesión. `/status` muestra con qué cuenta entraste.
- **En Windows**, Claude Code corre los comandos sin sandbox, con Git Bash y PowerShell (sin Git for Windows, solo PowerShell). Leé cada comando antes de aprobarlo y nunca elijas "Yes, and don't ask again" para `git` ni para `trello.py`: esa respuesta se guarda en `.claude/settings.local.json` y no vuelve a preguntar.
- **Permisos del repo:** `.claude/settings.json` arranca las sesiones de terminal en modo manual y pide confirmación para `git commit`, `git push` y las escrituras de `trello.py`. La extensión de VS Code no lee ese modo inicial: en tu configuración de usuario de VS Code poné `claudeCode.initialPermissionMode` en `default`.

### Codex

- **Cuenta:** iniciás sesión con tu cuenta de ChatGPT. Cada plan tiene límites de uso distintos. También acepta una API key, que se cobra aparte.
- **Instalación:** en una PowerShell nueva:

  ```powershell
  powershell -ExecutionPolicy ByPass -c "irm https://chatgpt.com/codex/install.ps1 | iex"
  ```

  Alternativa con Node.js: `npm install -g @openai/codex`. Se recomienda Windows 11.
- **Login:** `cd C:\dev\parcial_tp3` y después `codex`. Elegí **Sign in with ChatGPT**. `codex login status` muestra si quedaste logueado.
- **Permisos del repo:** `.codex/config.toml` hace que Codex arranque con sandbox `read-only` y aprobaciones `on-request`: lee y planifica solo, y te pide permiso antes de editar archivos o de correr comandos que escriban, usen la red (Gradle, el script de Trello) o toquen `.git` (commit, push). Codex carga ese archivo **solo si confiás en el proyecto** cuando te lo pregunta. Si no confiaste, arrancalo así:

  ```powershell
  codex --sandbox read-only --ask-for-approval on-request
  ```

- **Modos dentro de la sesión:** `/plan` para que proponga un plan antes de implementar, y `/permissions` para cambiar de preset (por ejemplo, a **Auto** si querés que edite el repo sin preguntar en esa sesión).

## 3. Credenciales de Trello

Seguí [`TRELLO.md`](TRELLO.md): creás tu key y tu token, y los guardás en variables de entorno, **nunca** en el repo. Al final, `python tools/trello/trello.py show REF-02` tiene que mostrarte la tarjeta de la consigna.

## 4. El flujo diario

1. **Tomá una tarjeta** de **Por hacer**, asignátela y pasala a **En curso** (desde Trello o con el script).
2. **Creá la rama** desde `main` actualizada (nombres en [`CONVENCIONES.md`](CONVENCIONES.md)):

   ```powershell
   git switch main
   git pull
   git switch -c feat/PAN-4.3-detalle-entrenamiento
   ```

3. **Abrí el agente** en la raíz del repo, pegá [`PROMPT_INICIAL.md`](PROMPT_INICIAL.md) y dale la key de la tarjeta.
4. **Revisá el plan** que te propone. Corregilo o aprobalo.
5. **Dejá que implemente.** Después compilá (`.\gradlew.bat :app:assembleDebug`) y **probá en el emulador**:
   - comparalo con el frame de Figma de la tarjeta;
   - si usa red, probalo con el mock levantado.
6. **Revisá el diff** (`git status`, `git diff`) y commiteá **vos** con el mensaje que propone el agente:

   ```powershell
   git add <archivos>
   git commit -m "feat(PAN-4.3): agrega pantalla de detalle de entrenamiento"
   git push
   ```

7. **Abrí el PR** en GitHub. La plantilla se carga sola: completala con capturas y la sección "Uso de IA". Pasá la tarjeta a **En revisión** y dejale un comentario con el link del PR y el texto de "Uso de IA" (lo pide `REF-06`).
8. **Pedí revisión** a un compañero. Si pide cambios, la tarjeta vuelve a **En curso** con un comentario. Cuando apruebe, mergeá con **Create a merge commit** o **Rebase and merge** (nunca squash), borrá la rama y pasá la tarjeta a **Terminado**.

## 5. Costos y tokens

- Claude Code necesita un plan pago o crédito de API. Codex usa el cupo de tu plan de ChatGPT, que se renueva por ventanas de horas y semanas.
- Todo lo que el agente lee (archivos, salidas de comandos, mensajes) se acumula en el contexto. Una conversación larga gasta mucho más que varias cortas.
- Usá una conversación por tarjeta: `/clear` en Claude Code, `/new` en Codex. Si se alarga, `/compact`.
- Para ver el consumo: `/usage` y `/context` en Claude Code, `/status` en Codex.
- Pedí cosas concretas, aprobá el plan antes de que escriba código y no pegues logs enteros. Más detalle en la [guía](guia-agentes-ia.html#tokens).

## 6. Problemas comunes en Windows

| Problema | Solución |
|---|---|
| `Filename too long` al clonar o compilar | Rutas largas. Corré `git config --global core.longpaths true`, cloná en una ruta corta (`C:\dev\`) y fuera de OneDrive. Si sigue fallando, habilitá las rutas largas de Windows en PowerShell **como administrador** y reiniciá: `New-ItemProperty -Path "HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem" -Name "LongPathsEnabled" -Value 1 -PropertyType DWORD -Force` |
| `warning: LF will be replaced by CRLF` | Es normal con `core.autocrlf true`: Git guarda LF en el repo y te da CRLF en Windows. Pero si `git diff` muestra un archivo **entero** cambiado sin que lo tocaras, no lo commitees: revisá que tengas `core.autocrlf true` y descartá el cambio con `git restore <archivo>`. Los finales de línea de `gradlew` y `gradlew.bat` (`.gitattributes`) se resuelven en `GIT-01`. |
| `ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH` | Definí `JAVA_HOME` (paso 1.3.9) y abrí una terminal nueva. Comprobalo con `& "$env:JAVA_HOME\bin\java" -version`. |
| Gradle falla bajando el JDK o las dependencias | El primer build necesita internet. Desactivá la VPN y reintentá. |
| `gradlew.bat` "no se reconoce como nombre de un cmdlet" | En PowerShell hace falta el prefijo: `.\gradlew.bat`. |
| El emulador no arranca, va muy lento o pide aceleración | Activá la virtualización en el BIOS y la **Plataforma del hipervisor de Windows** (paso 1.3.2), reiniciá y corré `emulator.exe -accel-check`. HAXM ya no se usa y AEHD se discontinúa el 31/12/2026: usá WHPX. |
| El emulador no llega al mock | El mock tiene que estar corriendo en Windows. Desde el emulador la URL es `http://10.0.2.2:8000/`, no `localhost`. |
| `python` abre la Microsoft Store | Usá `py`, o desactivá los alias en **Configuración → Aplicaciones → Configuración avanzada de aplicaciones → Alias de ejecución de aplicaciones**. |

## 7. Skill del equipo: `tp3-guidelines`

El equipo comparte una sola skill, `tp3-guidelines`, con las guías y convenciones de desarrollo: arquitectura, navegación, estado, Hilt, Retrofit, Room, UI, manejo de errores, tests y conceptos para la defensa oral. Es una carpeta con un `SKILL.md` (estándar [Agent Skills](https://agentskills.io)) que va copiada en:

| Agente | Ubicación |
|---|---|
| Claude Code | `.claude/skills/tp3-guidelines/SKILL.md` |
| Codex | `.agents/skills/tp3-guidelines/SKILL.md` |

El agente la carga solo cuando la tarea lo amerita. También se puede invocar: `/tp3-guidelines` en Claude Code, `$tp3-guidelines` o `/skills` en Codex.
