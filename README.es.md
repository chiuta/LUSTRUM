[English](README.md) · [Română](README.ro.md) · [Français](README.fr.md) · [Italiano](README.it.md) · **Español** · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Recupera tu teléfono. Nada sale de él.**

LUSTRUM encuentra archivos basura, duplicados, fotos parecidas y restos de aplicaciones, y los elimina solo con tu permiso. No tiene el permiso INTERNET, así que no puede enviar nada a ningún sitio.

**Versión: 0.2.0 · Última actualización: 2026-11-18**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Qué hace

- **El almacenamiento de un vistazo** — Espacio usado y libre, desglosado en imágenes, vídeo, audio, documentos y archivos comprimidos.
- **Archivos grandes, antiguos y basura** — Archivos de más de 50 MB, sin tocar desde hace un año, temporales, miniaturas, papelera y descargas interrumpidas.
- **Duplicados exactos** — Comparados por tamaño, luego huella rápida y después SHA-256 completo. Por defecto se conserva la copia más antigua.
- **Fotos parecidas** — El hash perceptual encuentra imágenes redimensionadas, guardadas de nuevo o repetidas. Tú revisas cada grupo.
- **Restos de aplicaciones** — Carpetas que dejaron apps desinstaladas hace tiempo, y carpetas vacías.
- **Compresión de fotos** — Recodifica JPEG grandes y los sustituye solo si el resultado pesa al menos un 20 % menos. Se conserva el EXIF.
- **Apps, cachés y sin uso** — Tamaño de la caché por app, fecha del último uso y acceso directo a sus ajustes o a la desinstalación.
- **Estado del dispositivo** — RAM, nivel y temperatura de la batería — solo lectura, a título informativo.

## Privado por diseño

La mayoría de los limpiadores viven de anuncios y datos. LUSTRUM está hecho al revés.

- Sin permiso INTERNET en el manifiesto — puedes comprobarlo en la información de la app del sistema.
- Sin cuentas, analíticas, anuncios ni avisos de pago.
- Cada borrado se confirma; en los archivos multimedia lo hace el cuadro de diálogo del propio Android.

## Cómo se compara

Frente a los limpiadores más conocidos, según sus descripciones públicas (noviembre de 2026).

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Sin anuncios | ✓ | ✗ | ✓ | ✓ | ✗ |
| Sin acceso a internet ni telemetría | ✓ | ✗ | ~ | ✗ | ✗ |
| Código abierto | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplicados y fotos parecidas | ✓ | ~ | ✓ | ~ | ✓ |
| Restos de apps desinstaladas | ~ | ✓ | ✓ | ✗ | ~ |
| Compresión de fotos | ✓ | ✓ | ✓ | ✗ | ~ |
| Limpieza programada | ✗ | ~ | ✓ | ✗ | ✓ |
| «Boost de RAM» | — | ✓ | ✗ | ✗ | ✓ |

✓ sí · ~ parcial o de pago · ✗ no · — no lo ofrece, a propósito

## Lo que no hace — a propósito

- El «boost de RAM» y los cierra-apps en segundo plano. Android gestiona la memoria por sí solo; esos botones no ayudan y gastan batería.
- Vaciar la caché de otras apps con un toque. Android lo prohíbe sin privilegios especiales, así que LUSTRUM te lleva directo a la pantalla de almacenamiento de cada app.
- iOS. El sistema no permite limpiar fuera del espacio propio de la app.

## Los permisos, en cristiano

| Permiso | Para qué |
|---|---|
| Multimedia | Escanea fotos, vídeo y audio. Para borrar, Android pide confirmación. |
| Acceso a todos los archivos (opcional) | Documentos, basura, restos, carpetas vacías y compresión. |
| Acceso al uso de aplicaciones (opcional) | Tamaño de la caché por app y cuándo se usó por última vez. |
| Eliminación de paquetes | Permite desinstalar una app desde la lista. |

## Consigue LUSTRUM

Beta — versión 0.2.0. Se instala directamente, sin cuenta de tienda.

1. Descarga el APK de la última versión publicada.
2. Permite la instalación desde esta fuente cuando Android te lo pida.
3. Abre LUSTRUM, concede el acceso a multimedia y pulsa Escanear. Un acceso adicional opcional desbloquea documentos, restos y compresión.

## Compilación

JDK 21, Android SDK, Gradle 8.14+: ejecuta `gradle assembleRelease`. El APK aparece en `app/build/outputs/apk/release/`. El workflow Build APK compila en cada push y publica una versión con las etiquetas `v*`. El APK de release está firmado con la clave de depuración (válido para instalación directa); añade tu propia clave para distribución pública.

## Estado

Beta. Compila en CI; aún sin probar en un teléfono real. Informa de los problemas como issues.

---

Publicado bajo licencia MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
