# Branch Health Dashboard — Business Requirements Specification (BRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Executive Summary

Branch Health Dashboard je plugin pro JetBrains IDE monitorující zdraví Git větví v reálném čase. Řeší problém zahlcení repozitáře stale větvemi, neočekávaných merge konfliktů a chybějícího přehledu o stavu větví. Jediný svého druhu na JetBrains Marketplace — přímý konkurent neexistuje.

---

## 2. Obchodní cíle

### 2.1 Primární role
**Standalone monetizovaný plugin** — řeší konkrétní bolest vývojářských týmů pracujících s Git větvemi. Freemium model s upsell na Pro tier.

### 2.2 KPI
| Metrika | Cíl (6 měsíců) | Cíl (12 měsíců) |
|---------|:---:|:---:|
| Celkové instalace | 8 000 | 30 000 |
| MAU | 4 000 | 15 000 |
| Konverze Free → Pro | 5 % | 8 % |
| Branches vyčištěno (celkem) | 50 000 | 200 000 |
| Konflikty preventovány | 10 000 | 50 000 |
| DAU (daily active usage) | 1 500 | 8 000 |
| Marketplace rating | ≥ 4.3 | ≥ 4.5 |

---

## 3. Cílový trh

- **Vývojářské týmy používající Git s více větvemi** — feature branches, release branches, hotfix branches
- Primárně: týmy 5–50 vývojářů s aktivním branching modelem (Git Flow, GitHub Flow, trunk-based)
- Sekundárně: open-source projekty s mnoha kontributory
- **Odhadovaná velikost trhu:** ~2M+ JetBrains uživatelů pracujících s Git

---

## 4. Konkurenční prostředí

### 4.1 Přímí konkurenti (JetBrains Marketplace)
**Žádný přímý konkurent.** Na JetBrains Marketplace neexistuje plugin zaměřený na health monitoring Git větví.

### 4.2 Nepřímí konkurenti
| Nástroj | Platforma | Srovnání |
|---------|-----------|----------|
| **GitLens** | VS Code | Bohatá Git vizualizace, ale ne health dashboard |
| **Git Graph** | VS Code | Vizualizace větví, ale bez health metrik |
| **Built-in Git** | JetBrains | Základní branch management, žádné health metriky |
| **GitHub Branch Protection** | GitHub.com | Serverová ochrana, ne IDE monitoring |

### 4.3 Konkurenční výhoda
- **Nativní IDE integrace** — real-time monitoring přímo v editoru
- **Proaktivní notifikace** — upozornění před problémy, ne po nich
- **Conflict prediction** — unikátní funkce, žádný IDE plugin toto nenabízí
- **Rebase advisor** — inteligentní doporučení na základě analýzy divergence
- **Cleanup automatizace** — batch operace přímo z IDE

---

## 5. Revenue model

| Tier | Cena | Funkce |
|------|------|--------|
| **Free** | $0 | Health skóre, stáří větví, stale detection, max 5 větví |
| **Pro** | $6/měsíc nebo $60/rok | Neomezené větve, conflict prediction, cleanup, rebase advisor, orphan detection |

### 5.1 Revenue projekce
| Období | Instalace | Pro konverze | MRR |
|--------|:---------:|:------------:|:---:|
| 6 měsíců | 8 000 | 400 | $2 400 |
| 12 měsíců | 30 000 | 2 400 | $14 400 |
| 24 měsíců | 80 000 | 8 000 | $48 000 |

---

## 6. Go-to-Market

1. **Developer community** — Reddit r/IntelliJIDEA, r/git, r/programming
2. **Content marketing** — blog posty: „Stop drowning in stale branches", „Predict merge conflicts before they happen"
3. **JetBrains Marketplace** — SEO optimalizace, featured plugin kandidatura
4. **Product Hunt launch** — virální spread v dev komunitě
5. **GitHub integrace** — demo repozitář s příklady
6. **Conference talks** — JetBrains Plugin Day, Git Merge

---

## 7. Milníky

| Milestone | Popis |
|-----------|-------|
| **M1: MVP** | Branch list, stáří, health skóre, tool window |
| **M2: Stale Detection** | Stale branch identifikace, cleanup suggestions |
| **M3: Conflict Prediction** | Analýza společných souborů, risk assessment |
| **M4: Rebase Advisor** | Divergence analýza, rebase vs merge doporučení |
| **M5: Orphan Detection** | Nedostupné commity, recovery |
| **M6: Pro Launch** | Freemium gate, Marketplace licensing, release |

---

## 8. Success Criteria

1. 30 000+ instalací do 12 měsíců
2. ≥ 5 % konverze Free → Pro do 6 měsíců
3. Marketplace rating ≥ 4.3
4. $14 000+ MRR do 12 měsíců
5. Měřitelný dopad: 200 000+ vyčištěných větví, 50 000+ preventovaných konfliktů

---

## 9. Rizika

| Riziko | Pravděpodobnost | Dopad | Mitigace |
|--------|:---:|:---:|---------|
| Nízká konverze Free→Pro | Střední | Vysoký | A/B testování paywall umístění, rozšíření free tier |
| JetBrains built-in funkce | Nízká | Vysoký | Rychlá iterace, pokročilé features (conflict prediction) |
| Git4Idea API změny | Střední | Střední | Abstrakční vrstva, sledování EAP releases |
| Výkon na velkých repozitářích | Střední | Střední | Caching, incremental update, lazy loading |
