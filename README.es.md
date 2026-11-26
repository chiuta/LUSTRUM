[English](README.md) · [Română](README.ro.md) · [Français](README.fr.md) · [Italiano](README.it.md) · **Español** · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Recupera tu teléfono. Nada sale de él.**

LUSTRUM encuentra archivos basura, duplicados, fotos parecidas y restos de aplicaciones, y los elimina solo con tu permiso. No tiene el permiso INTERNET, así que no puede enviar nada a ningún sitio.

**Versión: 0.8.0 · Última actualización: 2026-11-26**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Qué hace

- **El almacenamiento de un vistazo** — Espacio usado y libre, desglosado en imágenes, vídeo, audio, documentos y archivos comprimidos.
- **Archivos grandes, antiguos y basura** — Archivos de más de 50 MB, sin tocar desde hace un año, temporales, miniaturas, papelera y descargas interrumpidas.
- **Duplicados exactos** — Comparados por tamaño, luego huella rápida y después SHA-256 completo. Por defecto se conserva la copia más antigua.
- **Fotos parecidas** — El hash perceptual encuentra fotos redimensionadas, guardadas de nuevo o repetidas, agrupadas sin encadenar. Revisas cada grupo; por defecto se conserva la copia más grande.
- **Restos de aplicaciones** — Carpetas que dejaron apps desinstaladas hace tiempo, y carpetas vacías.
- **Compresión de fotos** — Recodifica los JPEG grandes y los sustituye solo si el resultado pesa al menos un 20 % menos. Se conserva el EXIF; las Motion Photos y las fotos HDR se omiten.
- **Apps, cachés y sin uso** — Tamaño de la caché por app, fecha del último uso y acceso directo a sus ajustes o a la desinstalación.
- **Estado del dispositivo** — RAM, nivel y temperatura de la batería — solo lectura, a título informativo.
- **Fotos borrosas y oscuras** — Mide nitidez y brillo en una copia pequeña de cada foto y lista los probables descartes para que los revises.
- **Eliminación segura con deshacer** — Papelera LUSTRUM opcional: los archivos eliminados esperan 30 días y se pueden restaurar; el espacio se libera al vaciarla.
- **Vistas previas y carpetas más grandes** — Miniaturas en cada lista, un ranking de las carpetas que más ocupan y el total liberado hasta ahora.
- **Swiper** — Repasa fotos y vídeos uno a uno, empezando por los más antiguos: desliza a la izquierda para eliminar, a la derecha para conservar. No se quita nada hasta que confirmes al final.
- **Limpieza programada** — Semanal o mensual, en segundo plano, con batería suficiente: una notificación, o limpieza automática solo de la basura evidente de más de 24 horas. Los archivos limpiados van siempre primero a la papelera LUSTRUM de 30 días.
- **Mejor foto y vídeos parecidos** — En cada grupo de fotos parecidas se propone primero la copia más nítida. Los vídeos parecidos se detectan por un fotograma central y una duración casi idéntica.
- **Reducción de caché asistida por el sistema** — Pide a Android que libere la caché de otras apps solicitando espacio. El resultado varía según el dispositivo; sin magia de root. En teléfonos con root también usa root para vaciar todas las cachés.

## Míralo en acción

Capturas y grabaciones reales de LUSTRUM en un emulador Android con archivos de ejemplo generados — no en un teléfono físico.

![Inicio: el almacenamiento de un vistazo y un escaneo con un toque](media/home-after.png)
![Basura encontrada, lista para revisar](media/files-junk.png)
![Archivos idénticos, una copia conservada por grupo](media/dupes-exact.png)
![Fotos parecidas: se conserva la copia más nítida](media/dupes-similar.png)
![Swiper: desliza para conservar o eliminar, nada se quita sin confirmar](media/swiper-marked.png)
![Limpieza programada, eliminación segura y reducción de caché](media/schedule.png)

**Vídeos de demostración cortos:** [▶ Escaneo del teléfono](media/scan.mp4) · [▶ Búsqueda de duplicados](media/dupes.mp4) · [▶ Swiper en acción](media/swiper.mp4) · [▶ Programar la limpieza automática](media/schedule.mp4)

## Privado por diseño

La mayoría de los limpiadores viven de anuncios y datos. LUSTRUM está hecho al revés.

- Sin permiso INTERNET en el manifiesto — puedes comprobarlo en la información de la app del sistema.
- Sin cuentas, analíticas, anuncios ni avisos de pago.
- Cada borrado se confirma; en los archivos multimedia lo hace el cuadro de diálogo del propio Android.
- Comprobaciones de seguridad integradas: las Motion Photos y las fotos HDR nunca se recomprimen, los archivos modificados en las últimas 24 horas nunca se consideran basura y no se puede borrar nada fuera del almacenamiento compartido.
- La limpieza programada actúa solo sobre basura evidente de más de 24 horas y la mueve siempre primero a la papelera LUSTRUM, nunca directa a la eliminación.

## Cómo se compara

Frente a los limpiadores más conocidos, según sus descripciones públicas (noviembre de 2026). Las celdas de la competencia proceden de descripciones públicas y no las hemos probado. La compresión de LUSTRUM (solo JPEG) y la programación (solo basura evidente) son parciales; herramientas maduras como SD Maid SE hacen más.

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Sin anuncios | ✓ | ✗ | ✓ | ✓ | ✗ |
| Sin acceso a internet ni telemetría | ✓ | ✗ | ~ | ✗ | ✗ |
| Código abierto | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplicados y fotos parecidas | ✓ | ~ | ✓ | ~ | ✓ |
| Restos de apps desinstaladas | ~ | ✓ | ✓ | ✗ | ~ |
| Compresión de fotos | ~ | ✓ | ✓ | ✗ | ~ |
| Limpieza programada | ~ | ~ | ✓ | ✗ | ✓ |
| «Boost de RAM» | ~ | ✓ | ✗ | ✗ | ✓ |
| Soporte root / Shizuku | ~ | ✗ | ✓ | ✗ | ✗ |

✓ sí · ~ parcial o de pago · ✗ no · — no lo ofrece, a propósito

## Lo que no hace — a propósito

- El «boost de RAM» y los cierra-apps en segundo plano. Android gestiona la memoria por sí solo; esos botones no ayudan y gastan batería.
- Vaciar con garantía, con un toque, la caché de otras apps. Android lo prohíbe sin privilegios especiales; LUSTRUM ofrece una reducción asistida por el sistema (resultados variables) y enlaza a la pantalla de almacenamiento de cada app.
- iOS. El sistema no permite limpiar fuera del espacio propio de la app.
- Automatización agresiva en segundo plano. La limpieza programada solo toca basura evidente de más de 24 horas, nunca tus fotos ni documentos.
- Mirar dentro de las carpetas Android/data de otras apps. Android 11+ lo bloquea a toda app normal; solo root o Shizuku pueden. LUSTRUM usa root solo para reducir la caché y no admite Shizuku.

## Los permisos, en cristiano

| Permiso | Para qué |
|---|---|
| Multimedia | Escanea fotos, vídeo y audio. Para borrar, Android pide confirmación. |
| Acceso a todos los archivos (opcional) | Documentos, basura, restos, carpetas vacías y compresión. |
| Acceso al uso de aplicaciones (opcional) | Tamaño de la caché por app y cuándo se usó por última vez. |
| Eliminación de paquetes | Permite desinstalar una app desde la lista. |

## Consigue LUSTRUM

Beta — versión 0.8.0. Se instala directamente, sin cuenta de tienda.

1. Descarga el APK de la última versión publicada.
2. Permite la instalación desde esta fuente cuando Android te lo pida.
3. Abre LUSTRUM, concede el acceso a multimedia y pulsa Escanear. Un acceso adicional opcional desbloquea documentos, restos y compresión.

## Compilación

JDK 21, SDK de Android, Gradle 8.14+: ejecuta `gradle assembleRelease` (`gradle testDebugUnitTest` ejecuta las pruebas unitarias). El APK aparece en `app/build/outputs/apk/release/`. El flujo Build APK prueba, compila y publica una versión por cada versión nueva en main. Los APK se firman con una clave pública estable del repositorio (`tools/lustrum-public.jks`), así las actualizaciones se instalan encima; para tu propia distribución añade tu clave como secretos del repositorio (`LUSTRUM_KEYSTORE_B64`, `LUSTRUM_STORE_PASS`, `LUSTRUM_KEY_PASS`, `LUSTRUM_KEY_ALIAS`).

## Estado

Beta. 25 pruebas unitarias, 11 instrumentadas en un emulador Android y una prueba de humo de la versión release minificada se ejecutan en CI; aún no probado en un teléfono físico. Las traducciones son automáticas y no están revisadas por nativos. Las versiones desde 0.4.0 comparten clave de firma: las actualizaciones se instalan encima (desde 0.3.0 o anteriores, desinstala una vez).

---

Publicado bajo licencia MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
