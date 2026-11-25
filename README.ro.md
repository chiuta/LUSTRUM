[English](README.md) · **Română** · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Recapătă-ți telefonul. Nimic nu pleacă din el.**

LUSTRUM găsește resturi, duplicate, fotografii asemănătoare și rămășițe de aplicații și le șterge doar cu acordul tău. Nu are permisiunea INTERNET, deci nu poate trimite nimic nicăieri.

**Versiune: 0.5.0 · Ultima actualizare: 2026-11-25**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Ce face

- **Stocarea dintr-o privire** — Spațiu folosit și liber, defalcat pe imagini, video, audio, documente și arhive.
- **Fișiere mari, vechi și resturi** — Fișiere peste 50 MB, neatinse de un an, temporare, miniaturi, coș și descărcări întrerupte.
- **Duplicate exacte** — Comparate după mărime, apoi amprentă rapidă, apoi SHA-256 complet. Implicit se păstrează cel mai vechi exemplar.
- **Fotografii asemănătoare** — Hash perceptual: găsește aceeași poză redimensionată, resalvată sau repetată, grupată fără înlănțuire. Tu verifici fiecare grup; implicit se păstrează cea mai mare copie.
- **Rămășițe de aplicații** — Foldere lăsate de aplicații dezinstalate de mult, plus folderele goale.
- **Comprimare foto** — Recomprimă JPEG-urile mari și le înlocuiește doar dacă rezultatul e cu cel puțin 20% mai mic. EXIF se păstrează; fotografiile cu video încorporat și cele HDR sunt ocolite.
- **Aplicații, cache și nefolosite** — Mărimea cache-ului per aplicație, data ultimei folosiri, un clic spre setări sau dezinstalare.
- **Starea dispozitivului** — RAM, nivelul și temperatura bateriei — doar citire, informativ.
- **Poze neclare și întunecate** — Măsoară claritatea și luminozitatea pe o copie mică a fiecărei poze și listează probabilele rebuturi, ca să le verifici tu.
- **Ștergere sigură, cu anulare** — Coș LUSTRUM opțional: fișierele șterse așteaptă 30 de zile și pot fi restaurate; spațiul se eliberează când golești coșul.
- **Miniaturi și cele mai mari foldere** — Miniaturi în fiecare listă, un clasament al folderelor care îți mănâncă spațiul și totalul eliberat până acum.

## Privat prin construcție

Majoritatea curățătoarelor trăiesc din reclame și date. LUSTRUM e făcut invers.

- Fără permisiunea INTERNET în manifest — o poți verifica în informațiile aplicației din sistem.
- Fără conturi, analitice, reclame sau cereri de plată.
- Orice ștergere e confirmată; ștergerea media trece prin dialogul propriu Android.
- Verificări de siguranță încorporate: fotografiile cu video încorporat (Motion Photo) și cele HDR nu sunt niciodată recomprimate, fișierele modificate în ultimele 24 de ore nu sunt considerate resturi, iar în afara stocării partajate nu se poate șterge nimic.

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
| Suport root / Shizuku | ✗ | ✗ | ✓ | ✗ | ✗ |

✓ da · ~ parțial sau contra cost · ✗ nu · — nu există, intenționat

## Ce nu face — intenționat

- „Boost RAM” și oprirea aplicațiilor din fundal. Android gestionează singur memoria; butoanele astea nu ajută și consumă baterie.
- Golirea cache-ului altor aplicații dintr-un clic. Android o interzice fără privilegii speciale, așa că LUSTRUM te duce direct la ecranul de stocare al fiecărei aplicații.
- iOS. Sistemul nu permite curățare în afara propriului spațiu al aplicației.
- Curățare programată. LUSTRUM acționează doar când îl deschizi și confirmi; nimic nu rulează în fundal.
- Să se uite în folderul Android/data al altor aplicații. Android 11+ blochează asta pentru orice aplicație obișnuită; doar root sau Shizuku pot, iar LUSTRUM nu le suportă încă.

## Permisiunile, pe înțelesul tuturor

| Permisiune | De ce |
|---|---|
| Media | Scanează fotografii, video și audio. La ștergere, Android cere confirmare. |
| Acces la toate fișierele (opțional) | Documente, resturi, rămășițe, foldere goale și comprimare. |
| Acces la utilizare (opțional) | Mărimea cache-ului per aplicație și când a fost folosită ultima oară. |
| Ștergere pachete | Îți permite să dezinstalezi o aplicație din listă. |

## Obține LUSTRUM

Beta — versiunea 0.4.0. Se instalează direct; nu ai nevoie de cont de magazin.

1. Descarcă APK-ul din ultima versiune publicată.
2. Permite instalarea din această sursă când Android te întreabă.
3. Deschide LUSTRUM, acordă accesul la media și apasă Scanează. Accesul suplimentar opțional deblochează documentele, rămășițele și comprimarea.

## Construire

JDK 21, Android SDK, Gradle 8.14+: rulează `gradle assembleRelease` (`gradle testDebugUnitTest` rulează testele unitare). APK-ul apare în `app/build/outputs/apk/release/`. Fluxul Build APK testează, compilează și publică o versiune pentru fiecare versiune nouă ajunsă pe main. APK-urile sunt semnate cu o cheie publică stabilă din repo (`tools/lustrum-public.jks`), deci actualizările se instalează peste versiunea veche; pentru distribuția ta adaugă propria cheie ca secrete de repo (`LUSTRUM_KEYSTORE_B64`, `LUSTRUM_STORE_PASS`, `LUSTRUM_KEY_PASS`, `LUSTRUM_KEY_ALIAS`).

## Stare

Beta. 14 teste unitare și compilarea rulează în CI; încă netestată pe un telefon fizic. Versiunile de la 0.4.0 încolo împart aceeași cheie de semnare, deci actualizările se instalează peste cea veche (de la 0.3.0 sau mai vechi, dezinstalează o dată).

---

Publicat sub licența MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
