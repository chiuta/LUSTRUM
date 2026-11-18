# LUSTRUM

Curățător de stocare pentru Android, **trade-free**: fără reclame, fără cont, fără telemetrie și **fără permisiunea INTERNET** (nu poate trimite nimic nicăieri). Scris de la zero (clean-room), pe baza documentației publice Android și a unei analize a funcțiilor oferite de aplicațiile concurente.

## Funcții

- **Stocare**: spațiu folosit/liber, defalcare pe categorii.
- **Fișiere**: mari (>50 MB), vechi (>1 an), resturi (temporare, miniaturi, coș, descărcări întrerupte), capturi de ecran, instalatoare, foldere goale.
- **Rămășițe aplicații**: foldere rămase în spațiul partajat de la aplicații dezinstalate.
- **Comprimare foto**: JPEG la calitate 80, înlocuit doar dacă rezultatul e cu ≥20% mai mic; păstrează data și etichetele EXIF principale.
- **Duplicate**: fișiere identice (mărime → hash 64 KB → SHA-256) și imagini asemănătoare (hash perceptual). Păstrează implicit cel mai vechi exemplar și blochează ștergerea tuturor copiilor dintr-un grup.
- **Aplicații**: cache și mărime per aplicație, aplicații nefolosite, scurtătură spre ecranul aplicației, dezinstalare.
- **Starea dispozitivului**: RAM, nivel și temperatură baterie (informativ).

## Comparație cu aplicațiile concurente

| Funcție | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|---|---|---|---|---|
| Fără reclame | da | nu în versiunea gratuită | da | da | nu / abonament |
| Fără permisiunea INTERNET, fără telemetrie | da | nu | parțial | nu | nu |
| Cod sursă deschis | da | nu | da (GPLv3) | nu | nu |
| Analiză stocare | da | da (în abonament) | da | da | da |
| Fișiere mari / vechi | da | da | da (filtre) | da | da |
| Duplicate identice | da | parțial | da | da | da |
| Imagini asemănătoare | da | da | da | parțial | da |
| Capturi, instalatoare, foldere goale | da | parțial | da | da | parțial |
| Rămășițe de la aplicații dezinstalate | da (limitat de Android) | da | da (cel mai bun) | nu | parțial |
| Comprimare foto | da | da | da | nu | parțial |
| Manager aplicații / nefolosite | da | da | da | da | da |
| Cache ale altor aplicații într-un clic | nu (blocat de Android) | prin accesibilitate | prin accesibilitate / root | nu | prin accesibilitate |
| Programare automată | nu încă | în abonament | da | nu | da |
| „Boost RAM” | nu, intenționat | da | nu | nu | da |
| Curățare cu root / Shizuku | nu | nu | da | nu | nu |

Sursele comparației: descrierile publice ale aplicațiilor și ale proiectului SD Maid SE. Tabelul reflectă starea din noiembrie 2026 și poate deveni depășit.

## Ce NU face (intenționat)

- **„Boost RAM” / „oprește aplicațiile din fundal”**: Android gestionează memoria singur; astfel de butoane nu ajută și consumă baterie.
- **Cache-ul altor aplicații dintr-un clic**: Android a interzis asta fără privilegii speciale. Aplicația arată cât ocupă fiecare și te duce în setările aplicației.
- **iOS**: sistemul nu permite nicio curățare în afara propriei aplicații.

## Permisiuni

| Permisiune | La ce folosește |
|---|---|
| Media (imagini/video/audio) | scanare de bază, ștergere cu confirmare de sistem |
| Acces la toate fișierele | documente, resturi, rămășițe, foldere goale, comprimare; opțional |
| Acces la utilizare | cache per aplicație și „ultima folosire”; opțional |
| Ștergere pachete | dezinstalare din listă |

## Construire

JDK 21, Android SDK, Gradle 8.14+:

```
gradle assembleRelease
```

APK-ul apare în `app/build/outputs/apk/release/`. Workflow-ul `Build APK` îl construiește la fiecare push și îl publică la etichete `v*`. APK-ul este semnat cu cheia de debug (instalare directă); pentru distribuție publică adaugă o cheie proprie.

## Stare

v0.2.0. Se compilează în CI; nu a fost încă testat pe un telefon real.
