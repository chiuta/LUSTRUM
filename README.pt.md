[English](README.md) · [Română](README.ro.md) · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · **Português** · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Recupere seu celular. Nada sai dele.**

O LUSTRUM encontra arquivos inúteis, duplicatas, fotos parecidas e sobras de aplicativos, e só os apaga com a sua autorização. Ele não tem a permissão INTERNET, então não consegue enviar nada para lugar nenhum.

**Versão: 0.3.0 · Última atualização: 2026-11-18**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## O que faz

- **Armazenamento num relance** — Espaço usado e livre, dividido em imagens, vídeo, áudio, documentos e arquivos compactados.
- **Arquivos grandes, antigos e inúteis** — Arquivos acima de 50 MB, parados há um ano, temporários, miniaturas, lixeira e downloads interrompidos.
- **Duplicatas exatas** — Comparadas por tamanho, depois impressão rápida e depois SHA-256 completo. Por padrão, a cópia mais antiga é mantida.
- **Fotos parecidas** — O hash perceptual encontra imagens redimensionadas, salvas de novo ou repetidas. Você revisa cada grupo.
- **Sobras de aplicativos** — Pastas deixadas por aplicativos desinstalados há muito tempo, além de pastas vazias.
- **Compressão de fotos** — Recodifica JPEGs grandes e só os substitui se o resultado for pelo menos 20% menor. O EXIF é preservado.
- **Apps, caches e sem uso** — Tamanho do cache por app, data do último uso e acesso direto às configurações ou à desinstalação.
- **Estado do dispositivo** — RAM, nível e temperatura da bateria — somente leitura, apenas informativo.

## Privado por construção

A maioria dos limpadores vive de anúncios e dados. O LUSTRUM foi feito ao contrário.

- Sem a permissão INTERNET no manifesto — você confere nas informações do app no sistema.
- Sem contas, análises, anúncios ou pedidos de pagamento.
- Toda exclusão é confirmada; nas mídias, quem confirma é a caixa de diálogo do próprio Android.

## Como se compara

Com os limpadores mais conhecidos, com base nas descrições públicas deles (novembro de 2026).

| | LUSTRUM | CCleaner | SD Maid SE | Files by Google | Norton Clean / Avast Cleanup |
|---|:---:|:---:|:---:|:---:|:---:|
| Sem anúncios | ✓ | ✗ | ✓ | ✓ | ✗ |
| Sem acesso à internet nem telemetria | ✓ | ✗ | ~ | ✗ | ✗ |
| Código aberto | ✓ | ✗ | ✓ | ✗ | ✗ |
| Duplicatas e fotos parecidas | ✓ | ~ | ✓ | ~ | ✓ |
| Sobras de apps desinstalados | ~ | ✓ | ✓ | ✗ | ~ |
| Compressão de fotos | ✓ | ✓ | ✓ | ✗ | ~ |
| Limpeza agendada | ✗ | ~ | ✓ | ✗ | ✓ |
| “Turbo de RAM” | — | ✓ | ✗ | ✗ | ✓ |

✓ sim · ~ parcial ou pago · ✗ não · — não oferece, de propósito

## O que ele não faz — de propósito

- “Turbo de RAM” e fechadores de apps em segundo plano. O Android gerencia a memória sozinho; esses botões não ajudam e gastam bateria.
- Limpar o cache de outros apps com um toque. O Android proíbe isso sem privilégios especiais, então o LUSTRUM leva você direto à tela de armazenamento de cada app.
- iOS. O sistema não permite limpeza fora do espaço do próprio app.

## As permissões, em palavras simples

| Permissão | Para quê |
|---|---|
| Mídia | Verifica fotos, vídeo e áudio. Para apagar, o Android pede confirmação. |
| Acesso a todos os arquivos (opcional) | Documentos, lixo, sobras, pastas vazias e compressão. |
| Acesso ao uso de apps (opcional) | Tamanho do cache por app e quando foi usado pela última vez. |
| Remoção de pacotes | Permite desinstalar um app pela lista. |

## Baixe o LUSTRUM

Beta — versão 0.3.0. Instalação direta, sem conta de loja.

1. Baixe o APK na última versão publicada.
2. Permita a instalação desta fonte quando o Android perguntar.
3. Abra o LUSTRUM, conceda acesso às mídias e toque em Verificar. Um acesso extra opcional libera documentos, sobras e compressão.

## Compilação

JDK 21, Android SDK, Gradle 8.14+: execute `gradle assembleRelease`. O APK aparece em `app/build/outputs/apk/release/`. O workflow Build APK compila a cada push e publica uma versão nas tags `v*`. O APK de release é assinado com a chave de depuração (serve para instalação direta); para distribuição pública, adicione sua própria chave.

## Situação

Beta. Compila no CI; ainda não testado em um celular real. Relate problemas como issues.

---

Publicado sob a licença MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
