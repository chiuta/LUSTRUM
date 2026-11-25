[English](README.md) · [Română](README.ro.md) · [Français](README.fr.md) · [Italiano](README.it.md) · [Español](README.es.md) · **Português** · [Deutsch](README.de.md) · [Magyar](README.hu.md)

# LUSTRUM

> **Recupere seu celular. Nada sai dele.**

O LUSTRUM encontra arquivos inúteis, duplicatas, fotos parecidas e sobras de aplicativos, e só os apaga com a sua autorização. Ele não tem a permissão INTERNET, então não consegue enviar nada para lugar nenhum.

**Versão: 0.5.0 · Última atualização: 2026-11-25**

[lustrum.chiuta.ro/](https://lustrum.chiuta.ro/)

## O que faz

- **Armazenamento num relance** — Espaço usado e livre, dividido em imagens, vídeo, áudio, documentos e arquivos compactados.
- **Arquivos grandes, antigos e inúteis** — Arquivos acima de 50 MB, parados há um ano, temporários, miniaturas, lixeira e downloads interrompidos.
- **Duplicatas exatas** — Comparadas por tamanho, depois impressão rápida e depois SHA-256 completo. Por padrão, a cópia mais antiga é mantida.
- **Fotos parecidas** — O hash perceptual encontra fotos redimensionadas, salvas de novo ou repetidas, agrupadas sem encadeamento. Você revisa cada grupo; por padrão a maior cópia é mantida.
- **Sobras de aplicativos** — Pastas deixadas por aplicativos desinstalados há muito tempo, além de pastas vazias.
- **Compressão de fotos** — Recodifica JPEGs grandes e só os substitui se o resultado for pelo menos 20% menor. O EXIF é mantido; Motion Photos e fotos HDR são ignoradas.
- **Apps, caches e sem uso** — Tamanho do cache por app, data do último uso e acesso direto às configurações ou à desinstalação.
- **Estado do dispositivo** — RAM, nível e temperatura da bateria — somente leitura, apenas informativo.
- **Fotos tremidas e escuras** — Mede nitidez e brilho em uma cópia pequena de cada foto e lista os prováveis descartes para você revisar.
- **Exclusão segura com desfazer** — Lixeira LUSTRUM opcional: os arquivos excluídos esperam 30 dias e podem ser restaurados; o espaço é liberado ao esvaziá-la.
- **Prévias e maiores pastas** — Miniaturas em cada lista, um ranking das pastas que mais ocupam espaço e o total liberado até agora.

## Privado por construção

A maioria dos limpadores vive de anúncios e dados. O LUSTRUM foi feito ao contrário.

- Sem a permissão INTERNET no manifesto — você confere nas informações do app no sistema.
- Sem contas, análises, anúncios ou pedidos de pagamento.
- Toda exclusão é confirmada; nas mídias, quem confirma é a caixa de diálogo do próprio Android.
- Verificações de segurança integradas: Motion Photos e fotos HDR nunca são recomprimidas, arquivos alterados nas últimas 24 horas nunca são tratados como lixo e nada fora do armazenamento compartilhado pode ser excluído.

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
| Suporte a root / Shizuku | ✗ | ✗ | ✓ | ✗ | ✗ |

✓ sim · ~ parcial ou pago · ✗ não · — não oferece, de propósito

## O que ele não faz — de propósito

- “Turbo de RAM” e fechadores de apps em segundo plano. O Android gerencia a memória sozinho; esses botões não ajudam e gastam bateria.
- Limpar o cache de outros apps com um toque. O Android proíbe isso sem privilégios especiais, então o LUSTRUM leva você direto à tela de armazenamento de cada app.
- iOS. O sistema não permite limpeza fora do espaço do próprio app.
- A limpeza agendada. O LUSTRUM só age quando você o abre e confirma; nada roda em segundo plano.
- Olhar dentro da pasta Android/data de outros apps. O Android 11+ bloqueia isso para qualquer app comum; só root ou Shizuku conseguem, e o LUSTRUM ainda não os suporta.

## As permissões, em palavras simples

| Permissão | Para quê |
|---|---|
| Mídia | Verifica fotos, vídeo e áudio. Para apagar, o Android pede confirmação. |
| Acesso a todos os arquivos (opcional) | Documentos, lixo, sobras, pastas vazias e compressão. |
| Acesso ao uso de apps (opcional) | Tamanho do cache por app e quando foi usado pela última vez. |
| Remoção de pacotes | Permite desinstalar um app pela lista. |

## Baixe o LUSTRUM

Beta — versão 0.4.0. Instalação direta, sem conta de loja.

1. Baixe o APK na última versão publicada.
2. Permita a instalação desta fonte quando o Android perguntar.
3. Abra o LUSTRUM, conceda acesso às mídias e toque em Verificar. Um acesso extra opcional libera documentos, sobras e compressão.

## Compilação

JDK 21, Android SDK, Gradle 8.14+: execute `gradle assembleRelease` (`gradle testDebugUnitTest` roda os testes unitários). O APK aparece em `app/build/outputs/apk/release/`. O workflow Build APK testa, compila e publica uma versão a cada nova versão enviada para main. Os APKs são assinados com uma chave pública estável do repositório (`tools/lustrum-public.jks`), então as atualizações se instalam por cima; para sua própria distribuição, adicione sua chave como segredos do repositório (`LUSTRUM_KEYSTORE_B64`, `LUSTRUM_STORE_PASS`, `LUSTRUM_KEY_PASS`, `LUSTRUM_KEY_ALIAS`).

## Situação

Beta. 14 testes unitários e a compilação rodam no CI; ainda não testado em um celular físico. Da 0.4.0 em diante as versões compartilham a mesma chave de assinatura, então as atualizações se instalam por cima (da 0.3.0 ou anterior, desinstale uma vez).

---

Publicado sob a licença MIT. © 2026 Alexandru-Ionuț Chiuță (Alexio)
