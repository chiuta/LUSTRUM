[English](README.md) · **Română** · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Recapătă-ți telefonul. Nimic nu pleacă din el.**

LUSTRUM găsește resturi, duplicate, fotografii asemănătoare și rămășițe de aplicații și le șterge doar cu acordul tău. Nu are permisiunea INTERNET, deci nu poate trimite nimic nicăieri.

**Versiune: 0.3.0 · Ultima actualizare: 2026-11-18**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Ce face

- **Stocarea dintr-o privire** — Spațiu folosit și liber, defalcat pe imagini, video, audio, documente și arhive.
- **Fișiere mari, vechi și resturi** — Fișiere peste 50 MB, neatinse de un an, temporare, miniaturi, coș și descărcări întrerupte.
- **Duplicate exacte** — Comparate după mărime, apoi amprentă rapidă, apoi SHA-256 complet. Implicit se păstrează cel mai vechi exemplar.
- **Fotografii asemănătoare** — Hash perceptual pentru poze redimensionate, resalvate sau repetate. Tu verifici fiecare grup.
- **Rămășițe de aplicații** — Foldere lăsate de aplicații dezinstalate de mult, plus folderele goale.
- **Comprimare foto** — Recodează JPEG-uri mari și le înlocuiește doar dacă rezultatul e cu cel puțin 20% mai mic. EXIF păstrat.
- **Aplicații, cache și nefolosite** — Mărimea cache-ului per aplicație, data ultimei folosiri, un clic spre setări sau dezinstalare.
- **Starea dispozitivului** — RAM, nivelul și temperatura bateriei — doar citire, informativ.

## Privat prin construcție

Majoritatea curățătoarelor trăiesc din reclame și date. LUSTRUM e făcut invers.

- Fără permisiunea INTERNET în manifest — o poți verifica în informațiile aplicației din sistem.
- Fără conturi, analitice, reclame sau cereri de plată.
- Orice ștergere e confirmată; ștergerea media trece prin dialogul propriu Android.

## Cum se compară

Față de cele mai cunoscute curățătoare, pe baza descrierilor lor publice (noiembrie 2026).

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Fără reclame | ✓ | ✗ | ✓ | ✓ | ✗ |
| Fără acces la internet sau telemetrie | ✓ | ✗ | ~ | ✗ | ✗ |
| Open source | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplicate și fotografii asemănătoare | ✓ | ~ | ✓ | ~ | ✓ |
| Rămășițe de la aplicații dezinstalate | ~ | ✓ | ✓ | ✗ | ~ |
| Comprimare foto | ✓ | ✓ | ✓ | ✗ | ~ |
| Curățare programată | ✗ | ~ | ✓ | ✗ | ✓ |
| „Boost RAM” | — | ✓ | ✗ | ✗ | ✓ |

✓ da · ~ parțial sau contra cost · ✗ nu · — nu există, intenționat

## Ce nu face — intenționat

- „Boost RAM” și oprirea aplicațiilor din fundal. Android gestionează singur memoria; butoanele astea nu ajută și consumă baterie.
- Golirea cache-ului altor aplicații dintr-un clic. Android o interzice fără privilegii speciale, așa că LUSTRUM te duce direct la ecranul de stocare al fiecărei aplicații.
- iOS. Sistemul nu permite curățare în afara propriului spațiu al aplicației.

## Permisiunile, pe înțelesul tuturor

| Permisiune | De ce |
|---|---|
| Media | Scanează fotografii, video și audio. La ștergere, Android cere confirmare. |
| Acces la toate fișierele (opțional) | Documente, resturi, rămășițe, foldere goale și comprimare. |
| Acces la utilizare (opțional) | Mărimea cache-ului per aplicație și când a fost folosită ultima oară. |
| Ștergere pachete | Îți permite să dezinstalezi o aplicație din listă. |

## Obține LUSTRUM

Beta — versiunea 0.3.0. Se instalează direct; nu ai nevoie de cont de magazin.

1. Descarcă APK-ul din ultima versiune publicată.
2. Permite instalarea din această sursă când Android te întreabă.
3. Deschide LUSTRUM, acordă accesul la media și apasă Scanează. Accesul suplimentar opțional deblochează documentele, rămășițele și comprimarea.

## Construire

JDK 21, Android SDK, Gradle 8.14+: rulează `gradle assembleRelease`. APK-ul apare în `app/build/outputs/apk/release/`. Workflow-ul Build APK construiește la fiecare push și publică o versiune la etichetele `v*`. APK-ul de release e semnat cu cheia de debug (potrivit pentru instalare directă); pentru distribuție publică adaugă o cheie proprie.

## Stare

Beta. Se compilează în CI; încă netestat pe un telefon real. Raportează problemele ca issues.

---

Publicat sub licența MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
