[English](README.md) · **Română** · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Recapătă-ți telefonul. Nimic nu pleacă din el.**

LUSTRUM găsește resturi, duplicate, fotografii asemănătoare și rămășițe de aplicații și le șterge doar cu acordul tău. Nu are permisiunea INTERNET, deci nu poate trimite nimic nicăieri.

**Versiune: 0.8.0 · Ultima actualizare: 2026-11-26**

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
- **Swiper** — Treci prin poze și videoclipuri pe rând, cele mai vechi întâi: glisează la stânga ca să ștergi, la dreapta ca să păstrezi. Nimic nu dispare până nu confirmi la final.
- **Curățare programată** — Săptămânal sau lunar, în fundal, cu baterie suficientă: o notificare sau curățarea automată doar a resturilor evidente mai vechi de 24 de ore. Fișierele curățate merg întotdeauna întâi în coșul LUSTRUM de 30 de zile.
- **Poza cea mai bună și videoclipuri asemănătoare** — În fiecare grup de poze asemănătoare, cea mai clară copie e propusă prima. Videoclipurile asemănătoare sunt potrivite după un cadru din mijloc și o durată aproape identică.
- **Reducerea cache-ului, asistată de sistem** — Cere Android să elibereze datele din cache ale altor aplicații, solicitând spațiu. Rezultatul diferă între dispozitive; nu există magie la nivel de root. Pe telefoanele cu root folosește și root pentru a goli toate cache-urile.

## Vezi-l în acțiune

Capturi de ecran și înregistrări reale cu LUSTRUM rulând pe un emulator Android, cu fișiere de probă generate — nu pe un telefon fizic.

![Acasă: spațiul dintr-o privire și scanare dintr-o atingere](media/home-after.png)
![Resturi găsite, gata de verificat](media/files-junk.png)
![Fișiere identice, o copie păstrată în fiecare grup](media/dupes-exact.png)
![Poze asemănătoare: se păstrează cea mai clară copie](media/dupes-similar.png)
![Swiper: glisezi ca să păstrezi sau să ștergi, nimic nu dispare până nu confirmi](media/swiper-marked.png)
![Curățare programată, ștergere sigură și reducere cache](media/schedule.png)

**Videoclipuri demonstrative scurte:** [▶ Scanarea telefonului](media/scan.mp4) · [▶ Căutarea duplicatelor](media/dupes.mp4) · [▶ Swiper în acțiune](media/swiper.mp4) · [▶ Programarea curățării automate](media/schedule.mp4)

## Privat prin construcție

Majoritatea curățătoarelor trăiesc din reclame și date. LUSTRUM e făcut invers.

- Fără permisiunea INTERNET în manifest — o poți verifica în informațiile aplicației din sistem.
- Fără conturi, analitice, reclame sau cereri de plată.
- Orice ștergere e confirmată; ștergerea media trece prin dialogul propriu Android.
- Verificări de siguranță încorporate: fotografiile cu video încorporat (Motion Photo) și cele HDR nu sunt niciodată recomprimate, fișierele modificate în ultimele 24 de ore nu sunt considerate resturi, iar în afara stocării partajate nu se poate șterge nimic.
- Curățarea programată acționează doar asupra resturilor evidente mai vechi de 24 de ore și le mută întotdeauna întâi în coșul LUSTRUM, niciodată direct la ștergere.

## Cum se compară

Față de cele mai cunoscute curățătoare, pe baza descrierilor lor publice (noiembrie 2026). Celulele concurenților provin din descrieri publice și nu au fost testate de noi. Comprimarea LUSTRUM (doar JPEG) și programarea (doar resturi evidente) sunt parțiale; unelte mature precum SD Maid SE fac mai mult.

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Fără reclame | ✓ | ✗ | ✓ | ✓ | ✗ |
| Fără acces la internet sau telemetrie | ✓ | ✗ | ~ | ✗ | ✗ |
| Open source | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplicate și fotografii asemănătoare | ✓ | ~ | ✓ | ~ | ✓ |
| Rămășițe de la aplicații dezinstalate | ~ | ✓ | ✓ | ✗ | ~ |
| Comprimare foto | ~ | ✓ | ✓ | ✗ | ~ |
| Curățare programată | ~ | ~ | ✓ | ✗ | ✓ |
| „Boost RAM” | ~ | ✓ | ✗ | ✗ | ✓ |
| Suport root / Shizuku | ~ | ✗ | ✓ | ✗ | ✗ |

✓ da · ~ parțial sau contra cost · ✗ nu · — nu există, intenționat

## Ce nu face — intenționat

- „Boost RAM” și oprirea aplicațiilor din fundal. Android gestionează singur memoria; butoanele astea nu ajută și consumă baterie.
- Golirea garantată, dintr-o atingere, a cache-ului altor aplicații. Android o interzice fără privilegii speciale; LUSTRUM oferă o reducere asistată de sistem (rezultate variabile) și trimite la ecranul de stocare al fiecărei aplicații.
- iOS. Sistemul nu permite curățare în afara propriului spațiu al aplicației.
- Automatizare agresivă în fundal. Curățarea programată atinge doar resturile evidente mai vechi de 24 de ore, niciodată pozele sau documentele tale.
- Să se uite în folderele Android/data ale altor aplicații. Android 11+ blochează asta pentru orice aplicație obișnuită; doar root sau Shizuku pot. LUSTRUM folosește root doar pentru reducerea cache-ului și nu suportă Shizuku.

## Permisiunile, pe înțelesul tuturor

| Permisiune | De ce |
|---|---|
| Media | Scanează fotografii, video și audio. La ștergere, Android cere confirmare. |
| Acces la toate fișierele (opțional) | Documente, resturi, rămășițe, foldere goale și comprimare. |
| Acces la utilizare (opțional) | Mărimea cache-ului per aplicație și când a fost folosită ultima oară. |
| Ștergere pachete | Îți permite să dezinstalezi o aplicație din listă. |

## Obține LUSTRUM

Beta — versiunea 0.8.0. Se instalează direct; nu ai nevoie de cont de magazin.

1. Descarcă APK-ul din ultima versiune publicată.
2. Permite instalarea din această sursă când Android te întreabă.
3. Deschide LUSTRUM, acordă accesul la media și apasă Scanează. Accesul suplimentar opțional deblochează documentele, rămășițele și comprimarea.

## Construire

JDK 21, Android SDK, Gradle 8.14+: rulează `gradle assembleRelease` (`gradle testDebugUnitTest` rulează testele unitare). APK-ul apare în `app/build/outputs/apk/release/`. Fluxul Build APK testează, compilează și publică o versiune pentru fiecare versiune nouă ajunsă pe main. APK-urile sunt semnate cu o cheie publică stabilă din repo (`tools/lustrum-public.jks`), deci actualizările se instalează peste versiunea veche; pentru distribuția ta adaugă propria cheie ca secrete de repo (`LUSTRUM_KEYSTORE_B64`, `LUSTRUM_STORE_PASS`, `LUSTRUM_KEY_PASS`, `LUSTRUM_KEY_ALIAS`).

## Stare

Beta. 23 de teste unitare, 10 teste instrumentate pe un emulator Android și un test de fum al versiunii release minificate rulează în CI; încă netestat pe un telefon fizic. Traducerile sunt automate și nerevizuite de vorbitori nativi. Versiunile de la 0.4.0 încoace au aceeași cheie de semnare, deci actualizările se instalează peste cea veche (de la 0.3.0 sau mai vechi, dezinstalează o dată).

---

Publicat sub licența MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
