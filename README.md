**English** · [Română](README.ro.md) · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Reclaim your phone. Nothing leaves it.**

LUSTRUM finds junk, duplicates, look-alike photos and app leftovers — and clears them only with your approval. It has no INTERNET permission, so it physically cannot send anything anywhere.

**Version: 0.5.0 · Last updated: 2026-11-25**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## What it does

- **Storage at a glance** — Used and free space, broken down by images, video, audio, documents and archives.
- **Large, old & junk files** — Files over 50 MB, untouched for a year, temp files, thumbnails, trash and interrupted downloads.
- **Exact duplicates** — Matched by size, then a quick fingerprint, then full SHA-256. The oldest copy is kept by default.
- **Look-alike photos** — Perceptual hashing finds resized, re-saved and repeated shots, grouped without chaining. You review each group; the largest copy is kept by default.
- **App leftovers** — Folders left behind by apps you uninstalled long ago, plus empty folders.
- **Photo compression** — Re-encodes big JPEGs and replaces them only if the result is at least 20% smaller. EXIF is kept; Motion Photos and HDR shots are skipped.
- **Apps, caches & unused** — Cache size per app, last-used date, one tap to its settings or to uninstall.
- **Device status** — RAM, battery level and temperature — read-only, for information.
- **Blurry & dark photos** — Measures sharpness and brightness on a small copy of each photo and lists the likely rejects for you to review.
- **Safe delete with undo** — Optional LUSTRUM trash: deleted files wait 30 days and can be restored; space is freed when you empty it.
- **Previews & biggest folders** — Thumbnails in every list, a ranking of the folders that eat your storage, and a running total of what you have freed.

## Private by construction

Most cleaners earn their keep from ads and data. LUSTRUM is built the other way around.

- No INTERNET permission in the manifest — you can verify it in your system's app info.
- No accounts, no analytics, no ads, no purchase prompts.
- Every deletion is confirmed first; media deletions go through Android's own system dialog.
- Built-in safety checks: Motion Photos and HDR photos are never recompressed, files changed in the last 24 hours are never called junk, and nothing outside shared storage can be deleted.

## How it compares

Against the best-known cleaners, based on their public descriptions (November 2026).

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| No ads | ✓ | ✗ | ✓ | ✓ | ✗ |
| No internet access or telemetry | ✓ | ✗ | ~ | ✗ | ✗ |
| Open source | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplicates & look-alike photos | ✓ | ~ | ✓ | ~ | ✓ |
| Leftovers of uninstalled apps | ~ | ✓ | ✓ | ✗ | ~ |
| Photo compression | ✓ | ✓ | ✓ | ✗ | ~ |
| Scheduled cleaning | ✗ | ~ | ✓ | ✗ | ✓ |
| “RAM boost” | — | ✓ | ✗ | ✗ | ✓ |
| Root / Shizuku support | ✗ | ✗ | ✓ | ✗ | ✗ |

✓ yes · ~ partial or paid · ✗ no · — not offered, on purpose

## What it will not do — on purpose

- “RAM boost” and background-app killers. Android manages memory itself; these buttons don't help and drain the battery.
- Clearing other apps' caches in one tap. Android forbids it without special privileges, so LUSTRUM takes you straight to each app's storage screen.
- iOS. The system does not allow cleaning outside an app's own sandbox.
- Scheduled cleaning. LUSTRUM acts only when you open it and confirm; nothing runs in the background.
- Look inside other apps' Android/data folder. Android 11+ blocks this for every normal app; only root or Shizuku can, and LUSTRUM does not support them yet.

## Permissions, in plain words

| Permission | Why |
|---|---|
| Media | Scans photos, video and audio. Deleting asks Android for confirmation. |
| All files access (optional) | Documents, junk, leftovers, empty folders and compression. |
| Usage access (optional) | Cache size per app and when each app was last used. |
| Package removal | Lets you uninstall an app from the list. |

## Get LUSTRUM

Beta — version 0.4.0. Installs directly; no store account needed.

1. Download the APK from the latest release.
2. Allow installation from this source when Android asks.
3. Open LUSTRUM, grant media access and tap Scan. Optional extra access unlocks documents, leftovers and compression.

## Building

JDK 21, Android SDK, Gradle 8.14+: run `gradle assembleRelease` (`gradle testDebugUnitTest` runs the unit tests). The APK appears in `app/build/outputs/apk/release/`. The Build APK workflow tests, builds and publishes a release for every new version pushed to main. APKs are signed with a stable public key kept in the repository (`tools/lustrum-public.jks`), so updates install over each other; for your own distribution add your key as repository secrets (`LUSTRUM_KEYSTORE_B64`, `LUSTRUM_STORE_PASS`, `LUSTRUM_KEY_PASS`, `LUSTRUM_KEY_ALIAS`).

## Status

Beta. 14 unit tests and the build run in CI; not yet tested on a physical phone. Builds from 0.4.0 on share one signing key, so updates install over the old version (from 0.3.0 or earlier, uninstall once).

---

Released under the MIT licence. © 2026 Alexandru-Ionuț Chiuță (Alexio)
