# Prompt inicial

Pegá este texto como **primer mensaje** de cada sesión nueva con tu agente (Claude Code o Codex), abierto en la raíz del repo. Sirve para los dos.

Antes, verificá que tengas las variables de Trello en esa terminal (ver `TRELLO.md`). No hace falta que pegues la tarjeta: el agente te pide la key y la lee con el script. Si todavía no tenés credenciales de Trello, pegale el título, la descripción y la checklist de la tarjeta.

---

```text
Vamos a trabajar en el parcial VORA Coach (app Android en Kotlin + Jetpack Compose). Seguí estos pasos en orden y no te saltees ninguno:

1. CONTEXTO. Leé AGENTS.md (si sos Claude Code, CLAUDE.md ya lo importa) y docs/onboarding/CONVENCIONES.md, y usá la skill tp3-guidelines (en Claude Code: /tp3-guidelines; en Codex: $tp3-guidelines). Si la skill no está disponible, avisame y seguí con AGENTS.md y CONVENCIONES.md. Respetá sus reglas durante toda la sesión.

2. AMBIENTE. Verificá el ambiente SOLO con comandos de lectura, sin instalar ni cambiar nada:
   - git status y git branch --show-current (en qué rama estoy y si hay cambios sin commitear)
   - git config user.name y git config user.email
   - python --version (o py --version en Windows)
   - qué variables están definidas, sin mostrar sus valores, con exactamente este comando:
     python -c "import os; print({k: bool(os.environ.get(k)) for k in ['TRELLO_KEY','TRELLO_TOKEN','TRELLO_BOARD','JAVA_HOME']})"
     (si las de Trello dan False, fijate si existe el archivo .trello.json en mi carpeta de usuario, sin abrirlo)
   NUNCA muestres, leas ni copies el valor de TRELLO_KEY, TRELLO_TOKEN ni el contenido de .trello.json.
   Resumime el resultado en una tabla y avisame si falta algo. No sigas hasta que lo resolvamos.

3. TARJETA. Preguntame la key de la tarjeta de Trello (por ejemplo PAN-4.3). Leela con:
   python tools/trello/trello.py show <KEY>
   Si la tarjeta remite a otra (por ejemplo REF-03 para el Figma o REF-04 para el mock), leé esa también. Si no podés abrir el link de Figma, pedime una captura del frame. Resumime en pocas líneas qué pide y cuáles son los criterios de aceptación.

4. RAMA. Si estoy en main, proponeme el nombre de la rama según CONVENCIONES.md (tipo/<KEY>-descripcion-corta) y el comando para crearla. No la crees vos: esperá mi OK.

5. PLAN. Antes de editar cualquier archivo, proponé un plan: qué archivos vas a crear o modificar, qué vas a hacer en cada uno y cómo lo vamos a verificar. Marcá cualquier duda o dato que falte en la tarjeta o en el Figma: no lo inventes, y si no lo resolvemos, proponé anotarlo en REF-05. Esperá mi aprobación.

6. IMPLEMENTACIÓN. Implementá solo lo aprobado y solo lo que pide la tarjeta. Sin refactors, cambios de versiones ni dependencias nuevas fuera del plan. Explicame lo importante de lo que escribís, porque lo tengo que defender en un oral.

7. VERIFICACIÓN. Al terminar:
   - Compilá con el comando de la tabla de AGENTS.md para tu shell (PowerShell: .\gradlew.bat :app:assembleDebug; Git Bash: ./gradlew.bat :app:assembleDebug) y mostrame el resultado. Si falla, arreglalo y volvé a compilar.
   - Listá qué verificaste vos y qué tengo que verificar yo en el emulador (por ejemplo, comparar la pantalla con el frame de Figma o probar un escenario del mock).
   - Repasá cada criterio de aceptación de la tarjeta y la definición de terminado (REF-06), y decime si están cumplidos, con la evidencia.

8. CIERRE. Proponeme, sin ejecutar nada:
   - el mensaje de commit (Conventional Commits en español, con la key como scope);
   - el título y la descripción del PR, completando .github/pull_request_template.md;
   - qué actualizar en Trello: ítems del checklist a tildar, comentario (link al PR + uso de IA) y lista destino.
   No commitees, no pushees y no modifiques Trello hasta que yo lo apruebe explícitamente. Cuando lo apruebe, usá tools/trello/trello.py.
```

---

## Consejos

- **Una sesión por tarjeta.** Al cambiar de tarjeta, empezá una conversación nueva (`/clear` en Claude Code, `/new` en Codex) y volvé a pegar el prompt: así el contexto no arrastra la tarea anterior y gastás menos tokens.
- **Si el agente se apura**, recordale el paso en el que está: "volvé al paso 5, todavía no aprobé el plan".
- **Si el agente pide permiso** para un comando que no entendés, preguntale qué hace antes de aceptar.
