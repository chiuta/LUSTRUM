[English](README.md) · [Română](README.ro.md) · **Français** · [Italiano](README.it.md) · [Español](README.es.md) · [Português](README.pt.md) · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Reprenez votre téléphone. Rien n'en sort.**

LUSTRUM repère fichiers inutiles, doublons, photos similaires et restes d'applications, et ne les supprime qu'avec votre accord. Il n'a pas la permission INTERNET : il ne peut physiquement rien envoyer.

**Version: 0.6.0 · Dernière mise à jour: 2026-11-25**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Ce qu'il fait

- **Le stockage d'un coup d'œil** — Espace utilisé et libre, réparti entre images, vidéos, audio, documents et archives.
- **Gros fichiers, anciens et inutiles** — Fichiers de plus de 50 Mo, intacts depuis un an, temporaires, miniatures, corbeille et téléchargements interrompus.
- **Doublons exacts** — Comparés par taille, puis empreinte rapide, puis SHA-256 complet. L'exemplaire le plus ancien est conservé par défaut.
- **Photos similaires** — Le hachage perceptuel repère les clichés redimensionnés, réenregistrés ou répétés, groupés sans chaînage. Vous vérifiez chaque groupe ; la plus grande copie est conservée par défaut.
- **Restes d'applications** — Dossiers laissés par des applications désinstallées depuis longtemps, et dossiers vides.
- **Compression photo** — Réencode les gros JPEG et ne les remplace que si le résultat est au moins 20 % plus léger. L'EXIF est conservé ; les Motion Photos et les photos HDR sont ignorées.
- **Applications, caches et inutilisées** — Taille du cache par application, date de dernière utilisation, accès direct aux réglages ou à la désinstallation.
- **État de l'appareil** — RAM, niveau et température de la batterie — en lecture seule, à titre informatif.
- **Photos floues et sombres** — Mesure la netteté et la luminosité sur une petite copie de chaque photo et liste les ratées probables, à vérifier par vous.
- **Suppression sécurisée, annulable** — Corbeille LUSTRUM facultative : les fichiers supprimés attendent 30 jours et peuvent être restaurés ; l'espace est libéré au vidage.
- **Aperçus et plus gros dossiers** — Miniatures dans chaque liste, un classement des dossiers qui mangent votre stockage et le total libéré jusqu'ici.
- **Swiper** — Passez photos et vidéos en revue une à une, les plus anciennes d'abord : glissez à gauche pour supprimer, à droite pour garder. Rien n'est supprimé avant votre confirmation finale.
- **Nettoyage programmé** — Chaque semaine ou chaque mois, en arrière-plan, batterie suffisante : une notification, ou le nettoyage automatique des seuls fichiers inutiles évidents de plus de 24 h.
- **Meilleure photo et vidéos similaires** — Dans chaque groupe de photos similaires, la copie la plus nette est proposée en premier. Les vidéos similaires sont repérées par une image du milieu et une durée presque identique.
- **Réduction du cache assistée par le système** — Demande à Android de libérer le cache des autres applications en réclamant de l'espace. Le résultat varie selon l'appareil ; pas de magie root.

## Privé par construction

La plupart des nettoyeurs vivent de publicité et de données. LUSTRUM fait l'inverse.

- Aucune permission INTERNET dans le manifeste — vérifiable dans les informations de l'application.
- Pas de compte, pas d'analyse, pas de pub, pas d'incitation à payer.
- Chaque suppression est confirmée ; pour les médias, c'est la boîte de dialogue d'Android qui s'en charge.
- Garde-fous intégrés : les Motion Photos et les photos HDR ne sont jamais recompressées, les fichiers modifiés depuis moins de 24 h ne sont jamais traités comme inutiles, et rien hors du stockage partagé ne peut être supprimé.
- Le nettoyage programmé n'agit que sur les fichiers inutiles évidents de plus de 24 h ; avec la suppression sécurisée, ils vont d'abord dans la corbeille LUSTRUM.

## Comparaison

Face aux nettoyeurs les plus connus, d'après leurs descriptions publiques (novembre 2026). Les cellules des concurrents viennent de descriptions publiques et n'ont pas été testées par nous. La compression de LUSTRUM (JPEG seulement) et la planification (fichiers inutiles évidents seulement) sont partielles ; des outils matures comme SD Maid SE font plus.

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Sans publicité | ✓ | ✗ | ✓ | ✓ | ✗ |
| Sans accès internet ni télémétrie | ✓ | ✗ | ~ | ✗ | ✗ |
| Open source | ✓ | ✗ | ✓ | ✗ | ✗ |
| Doublons et photos similaires | ✓ | ~ | ✓ | ~ | ✓ |
| Restes d'applications désinstallées | ~ | ✓ | ✓ | ✗ | ~ |
| Compression photo | ~ | ✓ | ✓ | ✗ | ~ |
| Nettoyage planifié | ~ | ~ | ✓ | ✗ | ✓ |
| « Boost RAM » | — | ✓ | ✗ | ✗ | ✓ |
| Prise en charge root / Shizuku | ✗ | ✗ | ✓ | ✗ | ✗ |

✓ oui · ~ partiel ou payant · ✗ non · — absent, volontairement

## Ce qu'il ne fait pas — volontairement

- Le « boost RAM » et les tueurs d'applications en arrière-plan. Android gère la mémoire seul ; ces boutons n'aident pas et vident la batterie.
- Vider à coup sûr, en un geste, le cache des autres applications. Android l'interdit sans privilèges spéciaux ; LUSTRUM propose une réduction assistée par le système (résultats variables) et renvoie à l'écran de stockage de chaque application.
- iOS. Le système interdit tout nettoyage hors du bac à sable de l'application.
- Une automatisation agressive en arrière-plan. Le nettoyage programmé ne touche que les fichiers inutiles évidents de plus de 24 h, jamais vos photos ni documents.
- Regarder dans le dossier Android/data des autres applications. Android 11+ l'interdit à toute application ordinaire ; seuls root ou Shizuku le permettent, et LUSTRUM ne les gère pas encore.

## Les permissions, en clair

| Permission | Pourquoi |
|---|---|
| Médias | Analyse photos, vidéos et audio. Pour supprimer, Android demande confirmation. |
| Accès à tous les fichiers (facultatif) | Documents, fichiers inutiles, restes, dossiers vides et compression. |
| Accès aux données d'utilisation (facultatif) | Taille du cache par application et date de dernière utilisation. |
| Suppression de paquets | Permet de désinstaller une application depuis la liste. |

## Obtenir LUSTRUM

Bêta — version 0.6.0. Installation directe, sans compte de boutique.

1. Téléchargez l'APK depuis la dernière version publiée.
2. Autorisez l'installation depuis cette source lorsque Android le demande.
3. Ouvrez LUSTRUM, accordez l'accès aux médias et touchez Analyser. Un accès supplémentaire facultatif débloque documents, restes et compression.

## Compilation

JDK 21, SDK Android, Gradle 8.14+ : lancez `gradle assembleRelease` (`gradle testDebugUnitTest` exécute les tests unitaires). L'APK apparaît dans `app/build/outputs/apk/release/`. Le workflow Build APK teste, compile et publie une version pour chaque nouvelle version poussée sur main. Les APK sont signés avec une clé publique stable du dépôt (`tools/lustrum-public.jks`), donc les mises à jour s'installent par-dessus ; pour votre propre diffusion, ajoutez votre clé en secrets du dépôt (`LUSTRUM_KEYSTORE_B64`, `LUSTRUM_STORE_PASS`, `LUSTRUM_KEY_PASS`, `LUSTRUM_KEY_ALIAS`).

## État

Bêta. 17 tests unitaires et 8 tests instrumentés sur émulateur Android tournent en CI ; pas encore testé sur un téléphone physique. Les traductions sont automatiques, non relues par des natifs. Les versions depuis 0.4.0 partagent une clé de signature : les mises à jour s'installent par-dessus (depuis 0.3.0 ou avant, désinstallez une fois).

---

Publié sous licence MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
