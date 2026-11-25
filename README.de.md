[English](README.md) · [Română](README.ro.md) · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · [Português](README.pt.md) · **Deutsch** · [Magyar](README.hu.md)

# LUSTRUM

> **Holen Sie sich Ihr Handy zurück. Nichts verlässt es.**

LUSTRUM findet Datenmüll, Duplikate, ähnliche Fotos und App-Reste und löscht sie nur mit Ihrer Zustimmung. Es hat keine INTERNET-Berechtigung und kann daher nichts irgendwohin senden.

**Version: 0.5.0 · Zuletzt aktualisiert: 2026-11-25**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Was es kann

- **Speicher auf einen Blick** — Belegter und freier Platz, aufgeteilt in Bilder, Videos, Audio, Dokumente und Archive.
- **Große, alte und überflüssige Dateien** — Dateien über 50 MB, seit einem Jahr unberührt, temporäre Dateien, Vorschaubilder, Papierkorb und abgebrochene Downloads.
- **Exakte Duplikate** — Verglichen nach Größe, dann schnellem Fingerabdruck, dann vollständigem SHA-256. Standardmäßig bleibt die älteste Kopie.
- **Ähnliche Fotos** — Wahrnehmungs-Hashing findet verkleinerte, neu gespeicherte und wiederholte Aufnahmen, ohne Kettenbildung gruppiert. Sie prüfen jede Gruppe; standardmäßig bleibt die größte Kopie erhalten.
- **App-Reste** — Ordner, die längst deinstallierte Apps hinterlassen haben, sowie leere Ordner.
- **Foto-Komprimierung** — Kodiert große JPEGs neu und ersetzt sie nur, wenn das Ergebnis mindestens 20 % kleiner ist. EXIF bleibt erhalten; Motion Photos und HDR-Aufnahmen werden übersprungen.
- **Apps, Caches und Ungenutztes** — Cache-Größe pro App, Datum der letzten Nutzung, direkt zu den Einstellungen oder zur Deinstallation.
- **Gerätestatus** — RAM, Akkustand und Temperatur — nur lesend, zur Information.
- **Unscharfe & dunkle Fotos** — Misst Schärfe und Helligkeit an einer kleinen Kopie jedes Fotos und listet wahrscheinlichen Ausschuss zur Prüfung auf.
- **Sicheres Löschen mit Rückgängig** — Optionaler LUSTRUM-Papierkorb: Gelöschtes wartet 30 Tage und lässt sich wiederherstellen; Speicher wird beim Leeren frei.
- **Vorschauen & größte Ordner** — Vorschaubilder in jeder Liste, eine Rangliste der speicherhungrigen Ordner und die bisher freigegebene Gesamtmenge.

## Privat von Grund auf

Die meisten Reiniger leben von Werbung und Daten. LUSTRUM ist umgekehrt gebaut.

- Keine INTERNET-Berechtigung im Manifest — prüfbar in den App-Infos des Systems.
- Keine Konten, keine Analyse, keine Werbung, keine Kaufaufforderungen.
- Jedes Löschen wird bestätigt; bei Mediendateien übernimmt das den Systemdialog von Android.
- Eingebaute Sicherheitsprüfungen: Motion Photos und HDR-Fotos werden nie neu komprimiert, in den letzten 24 Stunden geänderte Dateien gelten nie als Datenmüll, und außerhalb des gemeinsamen Speichers lässt sich nichts löschen.

## Der Vergleich

Mit den bekanntesten Reinigern, nach deren öffentlichen Beschreibungen (November 2026).

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Werbefrei | ✓ | ✗ | ✓ | ✓ | ✗ |
| Kein Internetzugriff, keine Telemetrie | ✓ | ✗ | ~ | ✗ | ✗ |
| Open Source | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplikate und ähnliche Fotos | ✓ | ~ | ✓ | ~ | ✓ |
| Reste deinstallierter Apps | ~ | ✓ | ✓ | ✗ | ~ |
| Foto-Komprimierung | ✓ | ✓ | ✓ | ✗ | ~ |
| Geplante Reinigung | ✗ | ~ | ✓ | ✗ | ✓ |
| „RAM-Boost“ | — | ✓ | ✗ | ✗ | ✓ |
| Root-/Shizuku-Unterstützung | ✗ | ✗ | ✓ | ✗ | ✗ |

✓ ja · ~ teilweise oder kostenpflichtig · ✗ nein · — bewusst nicht vorhanden

## Was es bewusst nicht tut

- „RAM-Boost“ und das Beenden von Hintergrund-Apps. Android verwaltet den Speicher selbst; diese Schaltflächen helfen nicht und kosten Akku.
- Den Cache anderer Apps mit einem Tipp leeren. Android verbietet das ohne Sonderrechte, daher führt LUSTRUM direkt zum Speicherbildschirm jeder App.
- iOS. Das System erlaubt keine Bereinigung außerhalb des eigenen Bereichs einer App.
- Geplantes Aufräumen. LUSTRUM handelt nur, wenn Sie es öffnen und bestätigen; nichts läuft im Hintergrund.
- In den Ordner Android/data anderer Apps schauen. Android 11+ sperrt das für jede normale App; nur Root oder Shizuku können es, und LUSTRUM unterstützt sie noch nicht.

## Berechtigungen in einfachen Worten

| Berechtigung | Wofür |
|---|---|
| Medien | Durchsucht Fotos, Videos und Audio. Zum Löschen fragt Android nach Bestätigung. |
| Zugriff auf alle Dateien (optional) | Dokumente, Datenmüll, Reste, leere Ordner und Komprimierung. |
| Nutzungszugriff (optional) | Cache-Größe pro App und letzte Nutzung. |
| Paketentfernung | Erlaubt das Deinstallieren einer App aus der Liste. |

## LUSTRUM holen

Beta — Version 0.4.0. Direkte Installation, ohne Store-Konto.

1. Laden Sie die APK aus der neuesten veröffentlichten Version herunter.
2. Erlauben Sie die Installation aus dieser Quelle, wenn Android fragt.
3. Öffnen Sie LUSTRUM, gewähren Sie Medienzugriff und tippen Sie auf Scannen. Optionaler Zusatzzugriff schaltet Dokumente, Reste und Komprimierung frei.

## Bauen

JDK 21, Android SDK, Gradle 8.14+: `gradle assembleRelease` ausführen (`gradle testDebugUnitTest` startet die Unit-Tests). Die APK liegt in `app/build/outputs/apk/release/`. Der Workflow Build APK testet, baut und veröffentlicht für jede neue Version auf main ein Release. APKs werden mit einem stabilen öffentlichen Schlüssel aus dem Repository signiert (`tools/lustrum-public.jks`), Updates lassen sich also drüber installieren; für eigene Verteilung den eigenen Schlüssel als Repository-Secrets hinterlegen (`LUSTRUM_KEYSTORE_B64`, `LUSTRUM_STORE_PASS`, `LUSTRUM_KEY_PASS`, `LUSTRUM_KEY_ALIAS`).

## Stand

Beta. 14 Unit-Tests und der Build laufen in CI; noch nicht auf einem echten Handy getestet. Ab 0.4.0 teilen alle Versionen einen Signaturschlüssel, Updates lassen sich also drüber installieren (ab 0.3.0 oder älter einmal deinstallieren).

---

Veröffentlicht unter der MIT-Lizenz. © 2026 Alexandru-Ionuț Chiuță (Alexio)
