// Generează index.html (pagina de prezentare) și README.md + README.<xx>.md din tools/i18n.json.
// Rulare: node tools/build.mjs
import { readFileSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const root = join(here, "..");
const T = JSON.parse(readFileSync(join(here, "i18n.json"), "utf8"));
const ORDER = ["en", "ro", "fr", "it", "es", "pt", "de", "hu"];
const VERSION = "0.3.0";
const DATE = process.env.LUSTRUM_DATE || new Date().toISOString().slice(0, 10);
const SITE = "https://lustrum.chiuta.ro/";

// ── pagina ──
const hreflang = ORDER.map(c => `<link rel="alternate" hreflang="${c}" href="${SITE}?lang=${c}">`).join("\n") +
  `\n<link rel="alternate" hreflang="x-default" href="${SITE}">`;
const json = JSON.stringify(T).replace(/</g, "\\u003c");
let html = readFileSync(join(here, "template.html"), "utf8")
  .replace("<!--HREFLANG-->", hreflang)
  .replace('"__I18N__"', () => json);
writeFileSync(join(root, "index.html"), html);

// ── documentație ──
const COLS = ["LUSTRUM", "CCleaner", "SD Maid SE", "Files by Google", "Norton Clean / Avast Cleanup"];
const M = [["y","n","y","y","n"],["y","n","p","n","n"],["y","n","y","n","n"],["y","p","y","p","y"],["p","y","y","n","p"],["y","y","y","n","p"],["n","p","y","n","y"],["x","y","n","n","y"]];
const SYM = { y: "✓", p: "~", n: "✗", x: "—" };

for (const c of ORDER) {
  const t = T[c];
  const file = c === "en" ? "README.md" : `README.${c}.md`;
  const bar = ORDER.map(k => {
    const f = k === "en" ? "README.md" : `README.${k}.md`;
    return k === c ? `**${T[k].name}**` : `[${T[k].name}](${f})`;
  }).join(" · ");
  const out = [];
  out.push(bar, "", "# LUSTRUM", "", `> **${t.h1a} ${t.h1b}**`, "", t.sub, "");
  out.push(`**${t.ver}: ${VERSION} · ${t.upd}: ${DATE}**`, "");
  out.push(`[${SITE.replace("https://", "")}](${SITE})`, "");
  out.push(`## ${t.f_title}`, "");
  t.features.forEach(f => out.push(`- **${f[0]}** — ${f[1]}`));
  out.push("", `## ${t.p_title}`, "", t.p_text, "");
  t.points.forEach(p => out.push(`- ${p}`));
  out.push("", `## ${t.c_title}`, "", t.c_sub, "");
  out.push("| |" + COLS.map(x => ` ${x} |`).join(""));
  out.push("|---|" + COLS.map(() => ":---:|").join(""));
  M.forEach((r, i) => out.push(`| ${t.rows[i]} |` + r.map(v => ` ${SYM[v]} |`).join("")));
  out.push("", t.legend, "", `## ${t.n_title}`, "");
  t.n_items.forEach(p => out.push(`- ${p}`));
  out.push("", `## ${t.perm_title}`, "", `| ${t.perm_cols[0]} | ${t.perm_cols[1]} |`, "|---|---|");
  t.perms.forEach(p => out.push(`| ${p[0]} | ${p[1]} |`));
  out.push("", `## ${t.d_title}`, "", t.d_sub, "");
  t.steps.forEach((s, i) => out.push(`${i + 1}. ${s}`));
  out.push("", `## ${t.build_title}`, "", t.build_text, "");
  out.push(`## ${t.status_title}`, "", t.status_text, "");
  out.push("---", "", `${t.lic} © 2026 Alexandru-Ionuț Chiuță (Alexio)`, "");
  writeFileSync(join(root, file), out.join("\n"));
}
console.log("OK: index.html +", ORDER.length, "README files, date", DATE);
