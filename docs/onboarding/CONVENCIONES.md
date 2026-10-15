# Convenciones: ramas, commits, PRs e idioma

Estas reglas salen de lo que pide la consigna del parcial (sección de organización y entrega):

- "Todos los integrantes deben realizar **commits propios con cambios significativos y verificables**."
- "Los mensajes de commit deben **describir el cambio realizado**."
- "Suban avances durante las dos semanas y **revisen las contribuciones antes de integrarlas**."
- "**Conserven el historial de commits** y creen una etiqueta de entrega, por ejemplo `entrega-tp3`."
- "Acordar primero rutas, modelos y componentes evita versiones incompatibles."

Cada regla de abajo existe para cumplir alguna de esas frases. Si algo no está acá, se acuerda en el grupo antes de hacerlo.

## Ramas

- **`main` está protegida por acuerdo:** nadie pushea directo a `main`. Todo entra por PR.
- **Una rama por tarjeta de Trello**, creada desde `main` actualizada.
- **Nombre:** `tipo/<KEY>-descripcion-corta`, en minúsculas salvo la key, con guiones y sin tildes ni espacios.

| Tipo | Para qué | Ejemplo |
|---|---|---|
| `feat` | Funcionalidad o pantalla nueva | `feat/PAN-4.3-detalle-entrenamiento` |
| `fix` | Corrección de un error | `fix/NUC-02-detalle-sin-conexion` |
| `refactor` | Reorganizar código sin cambiar comportamiento | `refactor/NUC-01-repositorio-workouts` |
| `docs` | README, documentación | `docs/ENT-04-declaracion-ia` |
| `test` | Tests | `test/NUC-02-dao-detalle` |
| `chore` | Build, dependencias, configuración | `chore/GIT-01-gitattributes` |
| `style` | Formato sin cambio de lógica | `style/PAN-2.1-formato` |

```bash
git switch main
git pull
git switch -c feat/PAN-4.3-detalle-entrenamiento
```

- Borrá la rama después del merge (GitHub ofrece el botón).

> El repo también puede proteger `main` de verdad: quien sea dueño del repo puede crear una regla en **Settings → Rules → Rulesets** que exija PR con 1 aprobación, y desactivar "Allow squash merging" en **Settings → General → Pull Requests**. Es opcional: el acuerdo del grupo alcanza.

## Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/) **en español**, con la key de la tarjeta como *scope*:

```text
tipo(KEY): descripción corta en presente

Cuerpo opcional: qué cambia y por qué, en una o dos líneas.
```

Ejemplos:

```text
feat(PAN-4.3): agrega pantalla de detalle de entrenamiento
fix(NUC-02): muestra el detalle guardado cuando no hay conexión
chore(GIT-01): agrega .gitattributes para los finales de línea
docs(ENT-04): agrega la declaración de uso de IA al README
```

Reglas:

- **Tipos válidos:** `feat`, `fix`, `refactor`, `docs`, `test`, `chore`, `style`.
- **Descripción:** en presente ("agrega", "corrige"), sin punto final, hasta ~72 caracteres. Tiene que decir **qué** cambió: "fix" o "cambios" solos no sirven.
- **Commits chicos y que compilen.** Cada commit deja el proyecto compilando. Mejor varios commits con sentido que uno gigante al final.
- **Hito del 21/10:** para esa fecha, cada integrante necesita al menos un commit significativo propio (consigna, hito 1). No lo dejes para el final.
- **Formato solo no cuenta:** "Un commit vacío, cambios de formato sin aporte o reemplazar el trabajo completo al final no acreditan participación técnica" (consigna). Un `style` acompaña trabajo real; nunca es tu único aporte.
- **Commits propios.** Cada integrante commitea con **su** identidad de git y su cuenta de GitHub. Verificá antes de commitear:
  ```bash
  git config user.name
  git config user.email   # tiene que ser un email asociado a tu cuenta de GitHub
  ```
- **El autor sos vos, aunque te ayude un agente.** No agregues trailers como `Co-Authored-By` de herramientas de IA. El uso de IA se declara en el PR, en un comentario de la tarjeta y, al final, en el README de la entrega.
- **Nada de secretos ni archivos generados:** `local.properties`, `build/`, `.idea/`, claves de Firebase admin, tokens de Trello. Revisá `git status` y `git diff --staged` antes de cada commit; nunca `git add .` sin mirar.

## Pull requests

- **Uno por tarjeta y chico.** Si un PR toca más de una pantalla o más de ~400 líneas, probablemente son dos tarjetas.
- **Título:** igual que un commit, `feat(PAN-4.3): agrega pantalla de detalle de entrenamiento`.
- **Descripción:** completá la plantilla (`.github/pull_request_template.md`), que GitHub carga sola.
- **Si es UI:** captura de la app al lado del frame de Figma. Esas capturas sirven después para las "cinco comparaciones visuales" del README.
- **Revisión:** al menos **1 aprobación de otro integrante**. Quien revisa baja la rama, compila y mira la pantalla en el emulador; no alcanza con leer el diff.
- **Al abrir el PR**, la tarjeta pasa a **En revisión**, con un comentario que tenga el link al PR y el texto de su sección "Uso de IA" (lo pide la definición de terminado, `REF-06`). Si la revisión pide cambios, vuelve a **En curso** con un comentario. Al mergear, pasa a **Terminado**.
- **Merge sin squash:** usá **Create a merge commit** o **Rebase and merge**. **No uses "Squash and merge"**: junta todos los commits en uno y se pierde el historial que la consigna pide conservar.

### Cómo revisar un PR

```bash
git fetch origin
git switch feat/PAN-4.3-detalle-entrenamiento
.\gradlew.bat :app:assembleDebug      # Windows PowerShell (en CMD, sin .\)
./gradlew :app:assembleDebug          # Linux, macOS o WSL con SDK
```

Mirá: que cumpla los criterios de aceptación de la tarjeta y la definición de terminado (`REF-06`), que se parezca al Figma en los dos tamaños de emulador, que no invente pantallas ni endpoints, que no rompa la navegación y que no suba secretos. Comentá en GitHub con sugerencias concretas.

## Ramas viejas: sincronizar con `main`

Si `main` avanzó mientras trabajabas, traé los cambios **antes** de pedir revisión.

**Opción A: rebase** (recomendada si la rama es solo tuya):

```bash
git fetch origin
git switch feat/PAN-4.3-detalle-entrenamiento
git rebase origin/main
# si hay conflictos: resolvelos, luego
git add <archivo>
git rebase --continue
# para volver atrás: git rebase --abort
git push --force-with-lease
```

`--force-with-lease` reescribe solo **tu** rama y falla si alguien más pusheó en ella. Nunca hagas force push a `main`.

**Opción B: merge** (si otra persona también trabaja en la rama):

```bash
git fetch origin
git switch feat/PAN-4.3-detalle-entrenamiento
git merge origin/main
# si hay conflictos: resolvelos, luego
git add <archivo>
git commit
git push
```

### Resolver conflictos

1. `git status` lista los archivos en conflicto.
2. Abrí cada uno (Android Studio tiene un *merge tool* en **Git → Resolve Conflicts**) y elegí qué queda entre `<<<<<<<`, `=======` y `>>>>>>>`.
3. Compilá antes de continuar.
4. Si no entendés el código de la otra parte, preguntale a quien lo escribió.

Los archivos que más chocan son los compartidos: la navegación, el tema y `gradle/libs.versions.toml`. Por eso:

- **Rutas, modelos y componentes compartidos se acuerdan primero** (una tarjeta de base antes de las pantallas).
- **No cambies versiones de dependencias** dentro de una tarjeta de pantalla: eso va en su propia tarjeta `chore`.

## Etiqueta de entrega

El día de la entrega, con todo mergeado en `main`, **un integrante acordado** crea la etiqueta:

```bash
git switch main
git pull
git tag -a entrega-tp3 -m "Entrega final TP3 - VORA Coach"
git push origin entrega-tp3
git ls-remote --tags origin   # verificá que aparece entrega-tp3
```

La etiqueta va sobre el commit entregado (ver `ENT-05`). Si antes del 28/10 hay que corregir algo, el mismo integrante la vuelve a crear sobre el commit final, con acuerdo del grupo:

```bash
git tag -fa entrega-tp3 -m "Entrega final TP3 - VORA Coach"
git push --force origin entrega-tp3
```

Después del 28/10 no se toca.

## Idioma

> **Acuerdo del equipo (propuesta inicial).** Se puede cambiar, pero entre todos y antes de que haya mucho código.

| Qué | Idioma |
|---|---|
| Identificadores (clases, funciones, variables, rutas) | Inglés |
| Comentarios de código | Inglés, y solo para explicar el porqué, no el qué |
| Textos visibles de la app | Tal cual el Figma, en `strings.xml` (nunca escritos directo en el código) |
| Commits, PRs, tarjetas y documentación | Español |
