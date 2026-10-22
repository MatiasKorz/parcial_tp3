# PAN-4.2 — Weekly Plan

## Referencia y alcance
- Copia oficial: https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=1-328
- Frame `1:328`, Workout / 02 — Weekly Plan, 390 × 844.
- Se consultaron estructura y captura del frame. No se encontró un PNG local de Weekly Plan.
- Base: `main` en `d95c61c`, con PAN-4.1 fusionado (PR #19). NUC-01, PR #18, no incorporado ni modificado.

## Interfaz y recursos
- Manrope, fondo #F7F8F5 y colores BASE-01; tarjetas VoraCard y barra inferior compartida de NAV-01.
- Margen horizontal 20; cabecera con separación 4; calendario a 20 de la cabecera, días de 42 × 56 en el ancho de referencia.
- Tarjetas separadas por 12; padding 16; bloque VORA separado por 20, padding 16.
- Textos originales en inglés, con datos fijos del Figma.
- Reutilización de `ic_home_orb.xml` a 24 y 20 dp; no se agregan exportaciones ni gráficos inventados.
- Estilos exclusivos en WeeklyPlanStyles; sin cambios al tema ni a componentes compartidos.
- La lista raíz permite scroll. Los chips fluyen a otra línea si falta ancho; las tarjetas pueden crecer con el texto.
- Previews para 390 × 844 y 360 × 640 con fuente ampliada. No equivalen a pruebas en dispositivos.

## Navegación y decisiones
- Entrada desde el calendario del Dashboard por la ruta existente `weekly_plan`.
- Las tres tarjetas navegan a Workout Details con el mismo `DEMO_WORKOUT_ID` existente. No son tres identidades del catálogo real.
- Atrás del sistema conserva el back stack existente para regresar al Dashboard; no se crean rutas.
- Barra compartida con Workout activa; sin duplicación ni cambios globales.
- TUE 8 seleccionado inicialmente. Cambiar el día solo cambia el resaltado local; no filtra tarjetas ni dispara consultas.
- Ambos chips muestran un aviso de demostración en inglés y no cambian el plan.
- El calendario y el aviso son estado local de presentación, conservado con rememberSaveable.

## Integración pendiente de NUC-01
- La responsabilidad prevista del catálogo continúa en Weekly Plan, pendiente de integración del núcleo y confirmaciones del equipo.
- La pantalla está aislada en `ui/screens/workout/weeklyplan` para evitar ocupar el archivo raíz previsto por NUC-01.
- No se agregan ViewModels, repositorios, Retrofit, Room, endpoints, datos de red simulados ni adaptadores provisionales.
- Al integrar: conectar modelos e IDs reales a las tarjetas y su callback; componer loading, empty, error/retry y contenido con última caché válida desde el núcleo.
- La obtención de datos, reintentos y conservación de caché corresponden al núcleo. Esta entrega no afirma cumplir esos criterios.
- Coordinar el destino Weekly Plan en AppNavGraph y su fila README con el responsable de NUC-01: son puntos de posible conflicto.

## Diferencias y verificación
- Se reutilizan tokens BASE-01 cuando no hay propiedades tipográficas/colores exactos recuperados. La captura y las medidas guían las derivaciones locales; fidelidad visual pendiente de revisión manual.
- El orbe existente omite el desenfoque suave de su SVG original, limitación ya documentada en el asset.
- READY utiliza el radio extraSmall existente, sin modificar formas globales.
- Barras de estado y navegación del sistema son nativas de Android; no se reproduce la barra iOS del frame.
- Build: assembleDebug exitoso (BUILD SUCCESSFUL, 32 s).
- Tests: testDebugUnitTest exitoso; 8 tests, 0 fallos y 0 errores.
- git diff --check: sin errores; archivos nuevos revisados con --no-index --check.
- No se ejecutan emuladores, ADB ni automatización visual por instrucción del usuario.
- Pendiente: revisión manual en Android Studio de layout, selección del calendario, tres tarjetas, avisos, scroll, barra inferior y regreso.

## Uso de IA
Codex se utilizó para consultar el diseño, implementar la interfaz Compose y navegación de demostración, documentar decisiones y ejecutar verificaciones locales. La revisión visual y funcional manual corresponde al integrante.
