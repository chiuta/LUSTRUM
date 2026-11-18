// Generează res/values*/strings.xml (en implicit + ro, fr, it, es, pt, de, hu) din tools/app-i18n.json.
// Rulare: node tools/build-android.mjs
import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const res = join(here, "..", "app", "src", "main", "res");
const S = JSON.parse(readFileSync(join(here, "app-i18n.json"), "utf8"));
const LANGS = ["en", "ro", "fr", "it", "es", "pt", "de", "hu"];

function esc(s) {
  const hasArgs = /%\d\$[sd]/.test(s);
  let o = s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
  o = o.replace(/'/g, "\\'").replace(/"/g, '\\"');
  if (/^[@?]/.test(o)) o = "\\" + o;
  if (hasArgs) o = o.replace(/%(?!\d\$[sd])/g, "%%");
  return o;
}

for (const l of LANGS) {
  const dir = join(res, l === "en" ? "values" : `values-${l}`);
  mkdirSync(dir, { recursive: true });
  const lines = ['<?xml version="1.0" encoding="utf-8"?>', "<resources>"];
  if (l === "en") lines.push('    <string name="app_name" translatable="false">LUSTRUM</string>');
  for (const [k, v] of Object.entries(S)) {
    if (!v[l]) throw new Error(`missing ${l} for ${k}`);
    lines.push(`    <string name="${k}">${esc(v[l])}</string>`);
  }
  lines.push("</resources>", "");
  writeFileSync(join(dir, "strings.xml"), lines.join("\n"));
}
console.log("OK:", Object.keys(S).length, "strings x", LANGS.length, "languages");
