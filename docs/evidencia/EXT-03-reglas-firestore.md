# Evidencia EXT-03: las reglas de Firestore aíslan los datos de dos cuentas

- **Reglas probadas:** [`firestore.rules`](../../firestore.rules), las mismas que se despliegan al proyecto.
- **Tests:** [`firestore-rules-tests/rules.test.mjs`](../../firestore-rules-tests/rules.test.mjs), con `@firebase/rules-unit-testing` 6.0.0 y `firebase` 13.0.0.
- **Dónde corren:** el emulador de Firestore (`cloud-firestore-emulator` v1.22.0) con el proyecto local `demo-vora-coach`. No usan el proyecto real ni crean usuarios.
- **Herramientas:** Firebase CLI 15.34.1, Node 24.14.0, JDK 25 (el de Android Studio).
- **Fecha de la corrida:** 26/10/2026.
- **Despliegue:** el mismo `firestore.rules` se desplegó el 26/10/2026 al proyecto `vora-coach-tp3-659c9` con `firebase deploy --only firestore:rules` ("released rules firestore.rules to cloud.firestore"). Se puede comparar en la consola, en Firestore Database → Reglas.

## Qué se prueba

Las dos cuentas son `alice-uid` (cuenta A) y `bob-uid` (cuenta B). Antes de cada test, cada una tiene su perfil en `users/{uid}`, cargado sin pasar por las reglas.

| Criterio de EXT-03 | Test | Resultado |
|---|---|---|
| Cada cuenta usa su propio perfil | `account A on its own profile` → reads / creates / updates it | permitido ✔ |
| Con la cuenta A no se puede leer el perfil de B | `account A on the profile of account B` → cannot read it | denegado ✔ |
| Con la cuenta A no se puede escribir el perfil de B | → cannot overwrite / update / delete it; cannot create a profile with the id of another account | denegado ✔ |
| Sin sesión no se accede a ningún perfil | `without a session` → cannot read / write any profile | denegado ✔ |
| Lo que no está en las reglas se rechaza | `outside users/{uid}` → a signed-in account cannot use other collections | denegado ✔ |

`assertSucceeds` falla si la operación es rechazada, y `assertFails` falla si la operación pasa: un test en verde quiere decir que las reglas hicieron lo esperado.

## Salida de la corrida

Comando, desde la raíz del repo (ver README, "Reglas de Firestore"):

```text
firebase emulators:exec --only firestore --project demo-vora-coach "npm test --prefix firestore-rules-tests"
```

```text
▶ account A on its own profile
  ✔ reads it
  ✔ creates it
  ✔ updates it
✔ account A on its own profile
▶ account A on the profile of account B
  ✔ cannot read it
  ✔ cannot overwrite it
  ✔ cannot update it
  ✔ cannot delete it
  ✔ cannot create a profile with the id of another account
✔ account A on the profile of account B
▶ without a session
  ✔ cannot read any profile
  ✔ cannot write any profile
✔ without a session
▶ outside users/{uid}
  ✔ a signed-in account cannot use other collections
✔ outside users/{uid}
ℹ tests 11
ℹ suites 4
ℹ pass 11
ℹ fail 0
+  Script exited successfully (code 0)
```

Entre los tests, el emulador registra cada rechazo con la regla que lo decidió. Por ejemplo, la escritura de A sobre el perfil de B:

```text
PERMISSION_DENIED: false for 'update' @ L15
```

`L15` es la línea `allow read, write: if isOwner(userId);` de `firestore.rules`.
