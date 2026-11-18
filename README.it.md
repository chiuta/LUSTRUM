[English](README.md) · [Română](README.ro.md) · [Français](README.fr.md) · **Italiano** · [Español](README.es.md) · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Riprenditi il telefono. Niente ne esce.**

LUSTRUM trova file inutili, duplicati, foto simili e residui di app, e li elimina solo con il tuo consenso. Non ha il permesso INTERNET, quindi non può inviare nulla da nessuna parte.

**Versione: 0.2.0 · Ultimo aggiornamento: 2026-11-18**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Cosa fa

- **Archiviazione a colpo d'occhio** — Spazio usato e libero, suddiviso in immagini, video, audio, documenti e archivi.
- **File grandi, vecchi e inutili** — File oltre 50 MB, intatti da un anno, temporanei, miniature, cestino e download interrotti.
- **Duplicati esatti** — Confrontati per dimensione, poi impronta rapida, poi SHA-256 completo. Per impostazione predefinita resta la copia più vecchia.
- **Foto simili** — L'hash percettivo trova immagini ridimensionate, risalvate o ripetute. Controlli tu ogni gruppo.
- **Residui di app** — Cartelle lasciate da app disinstallate da tempo, più le cartelle vuote.
- **Compressione foto** — Ricodifica i JPEG grandi e li sostituisce solo se il risultato è almeno il 20% più leggero. EXIF conservato.
- **App, cache e inutilizzate** — Dimensione della cache per app, data dell'ultimo utilizzo, accesso diretto alle impostazioni o alla disinstallazione.
- **Stato del dispositivo** — RAM, livello e temperatura della batteria — sola lettura, a scopo informativo.

## Privato per costruzione

La maggior parte dei pulitori vive di pubblicità e dati. LUSTRUM è fatto al contrario.

- Nessun permesso INTERNET nel manifest — lo verifichi nelle info dell'app del sistema.
- Niente account, analisi, pubblicità o richieste di pagamento.
- Ogni eliminazione è confermata; per i contenuti multimediali interviene la finestra di dialogo di Android.

## Il confronto

Con i pulitori più noti, in base alle loro descrizioni pubbliche (novembre 2026).

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Senza pubblicità | ✓ | ✗ | ✓ | ✓ | ✗ |
| Senza accesso a internet né telemetria | ✓ | ✗ | ~ | ✗ | ✗ |
| Open source | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplicati e foto simili | ✓ | ~ | ✓ | ~ | ✓ |
| Residui di app disinstallate | ~ | ✓ | ✓ | ✗ | ~ |
| Compressione foto | ✓ | ✓ | ✓ | ✗ | ~ |
| Pulizia programmata | ✗ | ~ | ✓ | ✗ | ✓ |
| «Boost RAM» | — | ✓ | ✗ | ✗ | ✓ |

✓ sì · ~ parziale o a pagamento · ✗ no · — assente, di proposito

## Cosa non fa — di proposito

- Il «boost RAM» e i chiusori di app in background. Android gestisce da solo la memoria; questi pulsanti non servono e scaricano la batteria.
- Svuotare la cache delle altre app con un tocco. Android lo vieta senza privilegi speciali, perciò LUSTRUM ti porta direttamente alla schermata di archiviazione di ogni app.
- iOS. Il sistema non consente pulizie fuori dall'area riservata dell'app.

## I permessi, in parole semplici

| Permesso | Perché |
|---|---|
| Contenuti multimediali | Analizza foto, video e audio. Per eliminare, Android chiede conferma. |
| Accesso a tutti i file (facoltativo) | Documenti, file inutili, residui, cartelle vuote e compressione. |
| Accesso ai dati di utilizzo (facoltativo) | Dimensione della cache per app e data dell'ultimo utilizzo. |
| Rimozione pacchetti | Consente di disinstallare un'app dall'elenco. |

## Ottieni LUSTRUM

Beta — versione 0.2.0. Si installa direttamente, senza account di uno store.

1. Scarica l'APK dall'ultima versione pubblicata.
2. Consenti l'installazione da questa fonte quando Android lo chiede.
3. Apri LUSTRUM, concedi l'accesso ai contenuti multimediali e tocca Scansiona. Un accesso extra facoltativo sblocca documenti, residui e compressione.

## Compilazione

JDK 21, Android SDK, Gradle 8.14+: esegui `gradle assembleRelease`. L'APK compare in `app/build/outputs/apk/release/`. Il workflow Build APK compila a ogni push e pubblica una release sui tag `v*`. L'APK di release è firmato con la chiave di debug (adatto all'installazione diretta); per la distribuzione pubblica aggiungi una chiave tua.

## Stato

Beta. Compila in CI; non ancora provato su un telefono reale. Segnala i problemi come issue.

---

Rilasciato con licenza MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
