# LUSTRUM

Curățător de stocare pentru Android, în spiritul CCleaner, dar **trade-free**: fără reclame, fără cont, fără telemetrie și **fără permisiunea INTERNET** (aplicația nu poate trimite nimic nicăieri).

## Ce face

- **Analiză stocare**: spațiu folosit/liber și defalcare pe categorii (imagini, video, audio, documente, arhive, altele).
- **Fișiere mari, vechi și resturi**: listă sortată, selecție, ștergere cu confirmare.
- **Duplicate**: fișiere identice (mărime → hash 64 KB → SHA-256) și imagini asemănătoare (dHash). Păstrează implicit cel mai vechi exemplar și nu te lasă să ștergi toate copiile dintr-un grup.
- **Aplicații**: cache și mărime per aplicație, aplicații nefolosite, scurtături spre ecranul aplicației și dezinstalare.

## Ce NU face (intenționat)

- **„Boost RAM” / „închide aplicații din fundal”**: Android gestionează memoria singur; astfel de butoane nu ajută și consumă baterie.
- **Ștergerea cache-ului altor aplicații dintr-un clic**: Android a interzis asta (de la 6.0 încoace). LUSTRUM arată cât ocupă fiecare și te duce direct în setările aplicației.
- **iOS**: sistemul nu permite nicio curățare în afara propriei aplicații.

## Permisiuni

| Permisiune | La ce folosește |
|---|---|
| Media (imagini/video/audio) | scanare de bază, fără acces larg |
| Acces la toate fișierele | documente, descărcări, resturi; opțional |
| Acces la utilizare | cache per aplicație și „ultima folosire”; opțional |
| Ștergere pachete | dezinstalare din listă |

Fișierele din MediaStore se șterg printr-o cerere de sistem (confirmare Android). Fără „Acces la toate fișierele” aplicația vede doar media.

## Construire

Local (JDK 21, Android SDK, Gradle 8.14+):

```
gradle assembleRelease
```

APK-ul apare în `app/build/outputs/apk/release/`. În GitHub, workflow-ul `Build APK` îl construiește la fiecare push și îl publică la etichete `v*`.

APK-ul de release este semnat cu cheia de debug, potrivit pentru instalare directă; pentru distribuție publică adaugă o cheie proprie.

## Stare

v0.1.0, scris fără acces la un SDK Android: **nu a fost compilat sau testat pe telefon**. Primul build în CI poate cere mici corecturi.
