[English](README.md) · [Română](README.ro.md) · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · [Português](README.pt.md) · [Deutsch](README.de.md) · **Magyar**

# LUSTRUM

> **Vedd vissza a telefonod. Semmi nem hagyja el.**

A LUSTRUM megtalálja a felesleges fájlokat, duplikátumokat, hasonló fotókat és az alkalmazások maradványait, és csak a jóváhagyásoddal törli őket. Nincs INTERNET-engedélye, így fizikailag sem tud semmit sehová küldeni.

**Verzió: 0.2.0 · Utolsó frissítés: 2026-11-18**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## Mit tud

- **Tárhely egy pillantásra** — Használt és szabad hely, képekre, videókra, hangra, dokumentumokra és archívumokra bontva.
- **Nagy, régi és felesleges fájlok** — 50 MB feletti, egy éve érintetlen, ideiglenes fájlok, bélyegképek, kuka és megszakadt letöltések.
- **Pontos duplikátumok** — Méret, majd gyors ujjlenyomat, majd teljes SHA-256 alapján. Alapértelmezés szerint a legrégebbi másolat marad.
- **Hasonló fotók** — Az észlelési hash megtalálja az átméretezett, újramentett és ismételt képeket. Minden csoportot te nézel át.
- **Alkalmazásmaradványok** — Régen eltávolított alkalmazások hátrahagyott mappái, valamint az üres mappák.
- **Fotótömörítés** — Újrakódolja a nagy JPEG-eket, és csak akkor cseréli le őket, ha az eredmény legalább 20%-kal kisebb. Az EXIF megmarad.
- **Alkalmazások, gyorsítótárak, nem használtak** — Alkalmazásonkénti gyorsítótár-méret, utolsó használat dátuma, közvetlen ugrás a beállításokhoz vagy az eltávolításhoz.
- **Eszközállapot** — RAM, akkumulátorszint és hőmérséklet — csak olvasható, tájékoztató jellegű.

## Magánszféra-barát felépítés

A legtöbb tisztító reklámokból és adatokból él. A LUSTRUM fordítva készült.

- Nincs INTERNET-engedély a jegyzékben — ellenőrizheted a rendszer alkalmazásinfóiban.
- Nincs fiók, analitika, reklám vagy fizetési felszólítás.
- Minden törlést megerősítesz; a médiafájloknál az Android saját párbeszédablaka kér jóváhagyást.

## Összehasonlítás

A legismertebb tisztítókkal szemben, nyilvános leírásaik alapján (2026. november).

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Reklámmentes | ✓ | ✗ | ✓ | ✓ | ✗ |
| Nincs internet-hozzáférés, nincs telemetria | ✓ | ✗ | ~ | ✗ | ✗ |
| Nyílt forráskód | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplikátumok és hasonló fotók | ✓ | ~ | ✓ | ~ | ✓ |
| Eltávolított alkalmazások maradványai | ~ | ✓ | ✓ | ✗ | ~ |
| Fotótömörítés | ✓ | ✓ | ✓ | ✗ | ~ |
| Ütemezett tisztítás | ✗ | ~ | ✓ | ✗ | ✓ |
| „RAM-gyorsítás” | — | ✓ | ✗ | ✗ | ✓ |

✓ igen · ~ részben vagy fizetős · ✗ nem · — szándékosan nincs

## Amit szándékosan nem tesz

- „RAM-gyorsítás” és a háttérben futó alkalmazások leállítása. Az Android magától kezeli a memóriát; ezek a gombok nem segítenek, és merítik az akkumulátort.
- Más alkalmazások gyorsítótárának egy koppintásos ürítése. Az Android különleges jogosultság nélkül tiltja, ezért a LUSTRUM közvetlenül az adott alkalmazás tárhely-képernyőjére visz.
- iOS. A rendszer nem engedi a tisztítást az alkalmazás saját területén kívül.

## Az engedélyek, egyszerűen

| Engedély | Miért |
|---|---|
| Média | Fotókat, videókat és hangfájlokat keres. Törléskor az Android megerősítést kér. |
| Hozzáférés minden fájlhoz (választható) | Dokumentumok, felesleges fájlok, maradványok, üres mappák és tömörítés. |
| Használati hozzáférés (választható) | Alkalmazásonkénti gyorsítótár-méret és az utolsó használat ideje. |
| Csomag eltávolítása | Lehetővé teszi egy alkalmazás eltávolítását a listából. |

## Szerezd meg a LUSTRUM-ot

Béta — 0.2.0 verzió. Közvetlenül telepíthető, áruházi fiók nélkül.

1. Töltsd le az APK-t a legújabb kiadásból.
2. Engedélyezd a telepítést erről a forrásról, amikor az Android rákérdez.
3. Nyisd meg a LUSTRUM-ot, add meg a médiahozzáférést, és koppints a Keresésre. A választható extra hozzáférés feloldja a dokumentumokat, maradványokat és a tömörítést.

## Fordítás

JDK 21, Android SDK, Gradle 8.14+: futtasd a `gradle assembleRelease` parancsot. Az APK az `app/build/outputs/apk/release/` mappában jelenik meg. A Build APK munkafolyamat minden push-nál fordít, és a `v*` címkéknél kiadást tesz közzé. A kiadási APK a hibakeresési kulccsal van aláírva (közvetlen telepítéshez elég); nyilvános terjesztéshez adj hozzá saját kulcsot.

## Állapot

Béta. A CI-ben lefordul; valódi telefonon még nem tesztelt. A hibákat issue-ként jelezd.

---

MIT licenc alatt kiadva. © 2026 Alexandru-Ionuț Chiuță (Alexio)
