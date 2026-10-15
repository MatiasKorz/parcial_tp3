@AGENTS.md

## Claude Code

Las reglas del proyecto están en `AGENTS.md`, importado arriba. Acá va solo lo propio de Claude Code.

- **Permisos:** `.claude/settings.json` arranca las sesiones de terminal en modo manual, pide confirmación para `git commit`, `git push` y las escrituras de `trello.py`, y quita la atribución de commits y PRs. Para leer la tarjeta y armar el plan, usá el modo plan (`Shift+Tab` hasta "plan mode"). No uses `bypassPermissions` ni `--dangerously-skip-permissions`.
- **Contexto:** usá `/clear` al cambiar de tarjeta, `/context` para ver qué ocupa el contexto y `/usage` para ver el consumo.
- **Skill del equipo:** `tp3-guidelines`. Claude Code la lee de `.claude/skills/tp3-guidelines/`; la copia en `.agents/skills/tp3-guidelines/` es la de Codex. Se carga sola o se invoca con `/tp3-guidelines`.
