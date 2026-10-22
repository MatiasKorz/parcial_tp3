# PAN-1.1 — SignIn

Implementación de demostración de Login - 01 — SignIn, nodo 2007:214 (390 × 894).
Referencia: https://www.figma.com/design/fdPiQd1t3W57WaR1GdKux4/Parcial-Domiciliario-7-Oct-26--Copy-?node-id=2007-214
Requisitos: descripción y criterios de Trello proporcionados manualmente por el integrante. No se consultó Trello durante la implementación.

## Recursos y procedencia

Originales locales: C:\dev\vora-referencias\PAN-1.1-originales.
El registro local procedencia.json contiene los nodos y enlaces temporales de exportación; no se incorporan esos enlaces al repositorio.
Logo PNG: nodo 2007:248. Se conserva el archivo original sin recrear efectos.
Google: exportación manual C:\dev\vora-referencias\SVG.svg, confirmada por el integrante como original; copiada como google.svg. Su clip-path identifica el nodo 2007:308.
Correo 2007:271, candado 2007:284, ojo 2007:287, Apple 2007:305, Face ID 2007:314, volver 2007:236, idioma 2007:243, flecha 2007:295 y escudo 2007:318.

SVG convertidos a VectorDrawable: mismos paths, colores, tamaños y viewports; opacidad de grupo 0.7 conservada con alpha del drawable de correo/candado/ojo.
Google conserva cuatro colores y clip rectangular. No hay filtros, máscaras ni imágenes en los SVG.
La conversión estructural no acredita equivalencia visual: pendiente comparar renders Android con PNG originales; si se detecta pérdida, usar el PNG original.

## Adaptación y decisiones provisionales

- Fondo exclusivo de SignIn #F3F9F5; sin cambiar BASE-01 globalmente.
- Los estilos exclusivos del subtítulo y la descripción viven en ui/theme/SignInType.kt: 12 sp con tracking 1 sp, y 14 sp con interlineado 20 sp, respectivamente. Se derivan de BASE-01 sin modificar el tema global.
- Se reutilizan Manrope, paleta, VoraCard, VoraLimeButton y VoraTextAction de BASE-01/02. Campos específicos preservan las APIs compartidas.
- Padding horizontal 24 dp; ancho máximo 448 dp; campos con mínimo 55 dp y CTA con mínimo 56 dp.
- Insets Android reales en lugar de dibujar status bar o home indicator de iOS. Scroll vertical y ajuste por teclado.
- Los controles sociales tienen mínimo 48 dp de altura (frame: 46) por accesibilidad. Tamaños crecen con el texto.
- Colores y tipografía exactos no recuperados por MCP: se reutilizan los tokens del equipo. El halo y radios se aproximan visualmente; no se presenta esto como medición exacta de Figma.
- Volver muestra «Ya estás en la pantalla inicial». EN muestra «El cambio de idioma no está disponible en esta demostración». Ambas decisiones son provisionales, pendientes de validación del equipo.
- Recuperación, Apple, Google y Face ID muestran avisos de demostración descartables.
- La contraseña inicia vacía con puntos de ejemplo como placeholder. No se almacena ninguna credencial; el ojo alterna la visibilidad del texto ingresado.
- Alex Morgan se representa mediante el email de ejemplo. No se crea sesión, se verifica la contraseña ni se modifica Home provisional. Sign In usa la ruta Home existente, elimina SignIn del back stack y evita duplicados, como NAV-01.
- SignUp y botón Atrás del sistema conservan NAV-01. No se agregan rutas.

## Verificación

Ejecutar `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest`.

Resultados confirmados:
- Build exitoso.
- 5 tests unitarios existentes aprobados, sin fallos.
- Pixel 4: navegación a Home, ida y regreso de SignUp, avisos de demostración, ojo de contraseña y scroll probados.
- Pixel 6: arranque y avisos probados. Ojo, navegación y demás verificaciones pendientes; no se considera validación completa del segundo emulador.
- La revisión manual del integrante confirmó los avisos de demostración y la navegación al placeholder SignUp existente.

Pendiente: completar las verificaciones de Pixel 6, teclado, edición de campos, fuentes grandes, horizontal y aceptación visual final contra Figma con capturas comparativas.
No se agregan tests unitarios que dupliquen composición; no hay lógica de credenciales ni servicios.

## Uso de IA para futuro PR

Herramienta: Codex.
Uso: inspección del diseño y recursos, conversión de SVG originales, implementación Compose y navegación del acceso de demostración, documentación y ejecución de verificaciones.
El integrante confirmó manualmente los avisos y la navegación a SignUp. Pendiente aceptación visual final y verificación completa en dos emuladores. Sin commits, pushes ni PR.
