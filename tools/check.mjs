// Verificări de coerență: traduceri complete, aceleași substituții (%1$d …) în toate limbile,
// aceeași formă a textelor paginii, versiunea din build.mjs = versionName din Gradle.
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const read = f => readFileSync(join(here, f), "utf8");
const LANGS = ["en", "ro", "fr", "it", "es", "pt", "de", "hu"];
const errors = [];
const err = m => errors.push(m);

const A = JSON.parse(read("app-i18n.json"));
const ph = s => (s.match(/%\d\$[sd]/g) || []).sort().join(",");
for (const [k, v] of Object.entries(A)) {
  for (const l of LANGS) {
    if (!v[l] || !String(v[l]).trim()) err(`app-i18n: ${k} lipsește în ${l}`);
    else if (ph(v[l]) !== ph(v.en)) err(`app-i18n: ${k} (${l}) are alte substituții decât en: [${ph(v[l])}] ≠ [${ph(v.en)}]`);
  }
}

const D = JSON.parse(read("i18n.json"));
const shape = x => Array.isArray(x) ? `[${x.length}${x.length && Array.isArray(x[0]) ? ":" + x[0].length : ""}]` : typeof x;
for (const k of Object.keys(D.en)) {
  for (const l of LANGS) {
    if (!(k in D[l])) { err(`i18n: ${k} lipsește în ${l}`); continue; }
    if (shape(D[l][k]) !== shape(D.en[k])) err(`i18n: ${k} (${l}) are altă formă: ${shape(D[l][k])} ≠ ${shape(D.en[k])}`);
  }
}

const gradle = read("../app/build.gradle.kts");
const build = read("build.mjs");
const v1 = /versionName = "([^"]+)"/.exec(gradle)?.[1];
const v2 = /const VERSION = "([^"]+)"/.exec(build)?.[1];
if (v1 !== v2) err(`versiune: Gradle ${v1} ≠ build.mjs ${v2}`);
for (const l of LANGS) if (!String(D[l].d_sub).includes(v1)) err(`i18n: d_sub (${l}) nu menționează versiunea ${v1}`);

if (errors.length) { console.error(errors.join("\n")); process.exit(1); }
console.log(`OK: ${Object.keys(A).length} șiruri aplicație, ${Object.keys(D.en).length} chei pagină, versiune ${v1}`);
