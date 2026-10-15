## Tarjeta

<!-- Key y link de la tarjeta de Trello. Ej: PAN-4.3 - https://trello.com/c/... -->
Trello:

## Qué cambia

<!-- Una o dos líneas. -->

## Por qué

<!-- Qué criterio de aceptación o requisito cubre. -->

## Cómo probarlo

1. `.\gradlew.bat :app:assembleDebug` (PowerShell) o `./gradlew :app:assembleDebug`
2. <!-- Pasos en la app: desde dónde se llega, qué tocar, qué escenario del mock usar. -->

## Checklist

- [ ] Compila sin errores.
- [ ] Lo probé en dos emuladores: el de referencia (~390 dp) y otro tamaño (UI03). Indicar API y tamaños.
- [ ] Actualicé el inventario de pantallas del README (NAV01), si aplica.
- [ ] Cumple la definición de terminado (`REF-06`).
- [ ] Cumple los criterios de aceptación de la tarjeta.
- [ ] Si es UI: agregué la captura comparada con el frame de Figma.
- [ ] No inventé pantallas, endpoints ni datos fuera del Figma o del contrato del mock.
- [ ] La navegación sigue funcionando (atrás y barra inferior, si aplica).
- [ ] No subo secretos ni archivos generados (`local.properties`, `build/`, `.idea/`, tokens).
- [ ] Los commits siguen `docs/onboarding/CONVENCIONES.md`.

## Capturas (si es UI)

| Figma | App (referencia, ~390 dp) | App (otro tamaño) |
|---|---|---|
| <!-- captura del frame --> | <!-- captura del emulador --> | <!-- captura del emulador --> |

## Uso de IA

<!-- Copiá esta sección como comentario en la tarjeta de Trello (REF-06): con eso se arma la declaración del README (ENT-04). -->
- Herramienta: <!-- Claude Code / Codex / ninguna -->
- Para qué la usé: <!-- ej: generar el layout inicial, explicar un error de Gradle -->
- [ ] Revisé y entiendo todo el código de este PR (lo puedo explicar en la defensa oral).
