# Trello: credenciales y script

El board del equipo es <https://trello.com/b/fDQQV0fL/tp3-parcial-vora-coach>. Todas las tarjetas empiezan con su key (`REF-01`, `NUC-02`, `PAN-4.3`, …): el script las busca por ese prefijo.

El script `tools/trello/trello.py` permite que vos o tu agente lean una tarjeta y la actualicen desde la terminal. Usa solo la biblioteca estándar de Python (≥ 3.9) y funciona en Windows y en WSL.

## 1. Crear tus credenciales (una vez por integrante)

Cada integrante crea **su propia** app, key y token. No se comparten.

1. Con tu cuenta de Trello abierta, entrá a <https://trello.com/apps/admin> y hacé clic en **New**.
2. Completá el formulario:
   - **Name:** por ejemplo `TP3 CLI`.
   - **Workspace:** el del board.
   - Los emails y el autor que pide.
   - No hace falta completar la URL de *Iframe connector*.
3. Hacé clic en **Create**.
4. Abrí la app, entrá a la pestaña de la **API key** y hacé clic en **Generate a new API Key**.
5. A la derecha de la key aparece el link **Token**. Abrilo, revisá los permisos y hacé clic en **Allow**. Copiá el token.

Si querés que el token venza solo al terminar el parcial, generalo con este link en lugar del paso 5 (reemplazá `TU_KEY`):

```text
https://trello.com/1/authorize?expiration=30days&scope=read,write&response_type=token&key=TU_KEY
```

### Cuidalas

- **La key + el token leen y escriben en toda tu cuenta de Trello**, no solo en este board.
- **Nunca** van en el repo (es público), en un commit, en una captura ni en el chat con el agente. Tampoco le pidas al agente que abra `.trello.json`: lo que lee el agente se envía al proveedor del modelo.
- Si se filtran, revocá el token: **tu perfil → Settings → Applications** (`https://trello.com/u/<tu-usuario>/account`) y generá uno nuevo.

## 2. Configurarlas

El script busca las credenciales en este orden:

1. Variables de entorno `TRELLO_KEY`, `TRELLO_TOKEN` y `TRELLO_BOARD`.
2. El archivo `.trello.json` en tu carpeta de usuario, **fuera del repo**.

El board también se puede pasar con `--board <link>`. Acepta el link completo (`https://trello.com/b/AbCd1234/...`), el *shortLink* (`AbCd1234`) o el id.

### Opción A: variables en PowerShell (persistentes)

`setx` guarda la variable para tu usuario. Con `Read-Host`, el valor no queda en el historial de PowerShell:

```powershell
setx TRELLO_KEY (Read-Host "Trello API key")
setx TRELLO_TOKEN (Read-Host "Trello token")
setx TRELLO_BOARD "https://trello.com/b/fDQQV0fL/tp3-parcial-vora-coach"
```

`setx` no cambia la terminal abierta: **cerrá y abrí** la terminal, VS Code o Android Studio. Para comprobarlo sin mostrar el valor:

```powershell
[bool]$env:TRELLO_KEY; [bool]$env:TRELLO_TOKEN; $env:TRELLO_BOARD
```

### Opción B: variables en WSL, leídas de archivos protegidos

El token no va escrito en `~/.bashrc`: ese archivo lo suelen leer los agentes cuando revisan el ambiente. Guardá la key y el token en archivos que solo vos podés leer (`read -rs` no muestra lo que pegás ni lo deja en el historial):

```bash
mkdir -p ~/.config/trello && chmod 700 ~/.config/trello
printf 'Trello API key: '; read -rs v; printf '%s' "$v" > ~/.config/trello/key; echo
printf 'Trello token: ';   read -rs v; printf '%s' "$v" > ~/.config/trello/token; echo
unset v
chmod 600 ~/.config/trello/key ~/.config/trello/token
```

Después agregá estas líneas al final de `~/.bashrc` (o `~/.zshrc`, si usás zsh) y abrí una terminal nueva:

```bash
export TRELLO_KEY="$(cat ~/.config/trello/key)"
export TRELLO_TOKEN="$(cat ~/.config/trello/token)"
export TRELLO_BOARD="https://trello.com/b/fDQQV0fL/tp3-parcial-vora-coach"
```

### Opción C: archivo en tu carpeta de usuario

Creá `.trello.json` en `C:\Users\<tu-usuario>\` (Windows) o `~/` (WSL):

```json
{"key": "...", "token": "...", "board": "https://trello.com/b/fDQQV0fL/tp3-parcial-vora-coach"}
```

En WSL, protegelo con `chmod 600 ~/.trello.json`. Si las tres variables de entorno están definidas, el script ni abre el archivo; si falta alguna, la completa desde ahí. Acepta archivos con o sin BOM (el que deja `Out-File -Encoding utf8` en Windows PowerShell).

## 3. Usar el script

Desde la raíz del repo. En Windows, si `python` no anda, usá `py`.

```bash
python tools/trello/trello.py show PAN-4.3                      # descripción, checklist y links
python tools/trello/trello.py assign PAN-4.3                    # te asigna la tarjeta
python tools/trello/trello.py move PAN-4.3 "En curso"           # la mueve de lista
python tools/trello/trello.py check PAN-4.3 "texto del ítem"    # tilda un criterio de aceptación
python tools/trello/trello.py check AMB-01 "accel-check" --checklist "Integrante 2"   # ítem repetido entre checklists
python tools/trello/trello.py comment PAN-4.3 "PR abierto: <link>"
python tools/trello/trello.py --board <link> show REF-02        # con otro board
```

- **`show`** busca la tarjeta abierta cuyo nombre empieza con la key (sin distinguir mayúsculas). `PAN-4.3` no confunde con `PAN-4.30`.
- **`move`** necesita el nombre exacto de la lista: Referencias, Pendientes, Por hacer, En curso, Bloqueado, En revisión o Terminado.
- **`check`** acepta una parte del texto del ítem. Si coincide con más de uno, te pide que seas más específico. Cuando el mismo ítem se repite en varios checklists (como en `AMB-01`, uno por integrante), elegí el tuyo con `--checklist "<nombre o parte del nombre>"`.
- **Errores:** el script nunca imprime la key ni el token. Ante un error explica qué falta o qué falló (credenciales, board, tarjeta, lista, red o una respuesta que no es JSON) y termina con código 1. Si Trello responde 429 (límite de pedidos), espera y reintenta solo.

## 4. Flujo agéntico: qué hace el agente solo y qué no

Cada tarjeta le indica al agente cómo trabajar: abrirse en la raíz del repo, leer `AGENTS.md`/`CLAUDE.md`, usar la skill `tp3-guidelines` y seguir `docs/onboarding/CONVENCIONES.md`. El [prompt inicial](PROMPT_INICIAL.md) ya cubre esos pasos, y el script se corre desde esa misma raíz.

| Acción | ¿El agente lo puede hacer solo? |
|---|---|
| `show`: leer tarjetas | **Sí.** Es de solo lectura. |
| `assign`, `move`, `check`, `comment` | **No.** El agente propone el comando y lo corre solo si vos lo aprobás. |

En Claude Code, `.claude/settings.json` hace que estas escrituras pidan confirmación siempre, en cualquier modo. En Codex, el sandbox del repo pide aprobación antes de cualquier comando que use la red, incluido `show`.

Los cambios en el board los ve todo el equipo, y la tarjeta tiene que reflejar el estado real. Por eso:

- **Al empezar:** `assign` y `move` a **En curso**.
- **Al abrir el PR:** `check` de los criterios cumplidos, `comment` con el link al PR y el texto de su sección "Uso de IA" (lo pide `REF-06`, para que `ENT-04` arme la declaración), y `move` a **En revisión**.
- **Si la revisión pide cambios:** `move` a **En curso**, con un `comment` que resuma qué hay que cambiar.
- **Al mergear:** `move` a **Terminado**.
- **Si te trabás:** `move` a **Bloqueado**, con un `comment` que explique qué falta.

## Problemas comunes

| Mensaje | Qué hacer |
|---|---|
| `Faltan credenciales` | La terminal no ve las variables. Si usaste `setx`, abrí una terminal nueva. |
| `caracteres inválidos (¿un salto de línea o comillas?)` | La key o el token tienen un salto de línea o comillas pegados. Volvé a cargarlos sin espacios ni comillas extra. |
| `Falta el board` | Definí `TRELLO_BOARD` o pasá `--board <link>`. |
| `Trello respondió 401` | La key o el token son incorrectos, o el token fue revocado. Generá uno nuevo. |
| `Trello no encontró el recurso` | El link del board está mal, o tu usuario no es miembro del board. |
| `No pude conectar con Trello` | No hay internet, o una VPN o un proxy lo bloquean. En **Codex**, la red viene apagada por defecto: aprobá el acceso a red cuando te lo pida. |
| En Codex dice `Faltan credenciales` pero en tu terminal anda | Revisá `shell_environment_policy` en `~/.codex/config.toml`: si filtra variables con `KEY` o `TOKEN` en el nombre, usá la opción C (archivo). |
