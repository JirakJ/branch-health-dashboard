# Branch Health Dashboard — Software Requirements Specification (SRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Úvod

### 1.1 Účel
Branch Health Dashboard monitoruje zdraví Git větví v reálném čase — sleduje stáří, aktivitu, predikuje merge konflikty, detekuje stale a orphan větve a doporučuje rebase vs merge strategii.

### 1.2 Rozsah
Plugin analyzuje lokální i remote větve přes Git4Idea API, zobrazuje health dashboard v tool window a proaktivně upozorňuje na problémové větve.

### 1.3 Reference
- Git4Idea plugin API (bundled v IntelliJ Platform)
- IntelliJ Platform SDK 2024.3+

---

## 2. Celkový popis

### 2.1 Perspektiva produktu
Standalone plugin pro týmy aktivně pracující s Git větvemi. Řeší problém zahlcení repozitáře stale větvemi, neočekávaných merge konfliktů a chybějícího přehledu o stavu větví.

### 2.2 Uživatelské skupiny
- **Vývojáři v týmech** — pracují s více feature/bugfix větvemi denně
- **Tech leads / revieweři** — potřebují přehled o stavu větví v projektu
- **DevOps / release manažeři** — spravují release větve, cleanup

---

## 3. Funkční požadavky

### FR-1: Branch Age Tracking
- **FR-1.1:** Sledování stáří každé větve (čas od vytvoření)
- **FR-1.2:** Čas od posledního commitu na větvi
- **FR-1.3:** Zobrazení ve dnech/hodinách s barevným kódováním (zelená <7d, žlutá 7–30d, červená >30d)

### FR-2: Activity Heatmap
- **FR-2.1:** Počet commitů per den/týden per větev
- **FR-2.2:** Vizualizace aktivity jako heatmapa v tool window
- **FR-2.3:** Identifikace nejaktivnějších a nejméně aktivních větví
- **FR-2.4:** Filtrace dle časového období (posledních 7/30/90 dní)

### FR-3: Merge Conflict Prediction (Paid)
- **FR-3.1:** Analýza společně modifikovaných souborů mezi větvemi
- **FR-3.2:** Výpočet rizika konfliktu: LOW, MEDIUM, HIGH, CRITICAL
- **FR-3.3:** Zobrazení konfliktních souborů a řádků
- **FR-3.4:** Notifikace při HIGH/CRITICAL riziku

### FR-4: Stale Branch Detection
- **FR-4.1:** Identifikace větví bez aktivity po konfigurovatelnou dobu (default: 30 dní)
- **FR-4.2:** Rozlišení merged vs unmerged stale větví
- **FR-4.3:** Konfigurovatelné prahy (7/14/30/60/90 dní)

### FR-5: Stale Branch Cleanup Suggestions (Paid)
- **FR-5.1:** Doporučení větví bezpečných ke smazání (merged, žádná unikátní práce)
- **FR-5.2:** Batch delete s potvrzovacím dialogem
- **FR-5.3:** Dry-run mód (zobrazení bez mazání)
- **FR-5.4:** Ochrana chráněných větví (main, master, develop — konfigurovatelné)

### FR-6: Orphan Commit Detection (Paid)
- **FR-6.1:** Detekce commitů nedostupných z žádné větve nebo tagu
- **FR-6.2:** Zobrazení orphan commitů s autorem, datem a zprávou
- **FR-6.3:** Možnost vytvoření větve z orphan commitu

### FR-7: Rebase Advisor (Paid)
- **FR-7.1:** Analýza divergence větve od base větve
- **FR-7.2:** Doporučení: rebase (malá divergence, lineární historie) vs merge (velká divergence, sdílená větev)
- **FR-7.3:** Zobrazení počtu commitů ahead/behind
- **FR-7.4:** Vizualizace divergence (graf)

### FR-8: Branch Comparison
- **FR-8.1:** Ahead/behind vizualizace mezi libovolnými dvěma větvemi
- **FR-8.2:** Diff summary (počet změněných souborů, řádků)
- **FR-8.3:** Quick action: merge/rebase z comparison view

### FR-9: Tool Window
- **FR-9.1:** Registrace tool window: „Branch Health" (pravý panel)
- **FR-9.2:** Tabulka/list větví s kolonkami: název, stáří, poslední commit, health skóre, conflict risk
- **FR-9.3:** Filtrace: all/local/remote, healthy/warning/critical, stale/active
- **FR-9.4:** Řazení dle libovolné kolonky
- **FR-9.5:** Detail panel: klik na větev → podrobné metriky, heatmapa, conflict souhrn
- **FR-9.6:** Toolbar akce: refresh, cleanup, settings

### FR-10: Notifications
- **FR-10.1:** Notifikace při detekci nové stale větve
- **FR-10.2:** Notifikace při HIGH/CRITICAL conflict risku (Paid)
- **FR-10.3:** Periodický souhrn zdraví větví (denní/týdenní, konfigurovatelné)
- **FR-10.4:** Notification group: „Branch Health Dashboard"

---

## 4. Nefunkční požadavky

### NFR-1: Výkon
- Git operace na background threadech — nesmí blokovat EDT
- Analýza < 5 sekund pro repozitář s < 100 větvemi
- Incremental update (jen změněné větve)
- Caching výsledků s konfigurovatelnou TTL

### NFR-2: Kompatibilita
- IntelliJ IDEA 2024.3+ (sinceBuild=243, untilBuild=252.*)
- Všechny JetBrains IDE s Git4Idea
- macOS, Linux, Windows
- JVM 17, Kotlin 2.1.0

### NFR-3: Závislosti
- `com.intellij.modules.platform`
- `Git4Idea`

---

## 5. Datový model

### 5.1 BranchInfo
```
BranchInfo {
  name: String
  isLocal: Boolean
  isRemote: Boolean
  lastCommitDate: Instant
  lastCommitAuthor: String
  lastCommitMessage: String
  createdDate: Instant?
  commitCount: Int
  aheadCount: Int
  behindCount: Int
  isMerged: Boolean
  isProtected: Boolean
}
```

### 5.2 BranchHealth
```
BranchHealth {
  branch: BranchInfo
  healthLevel: HealthLevel
  healthScore: Int (0-100)
  ageInDays: Int
  daysSinceLastCommit: Int
  isStale: Boolean
  conflictRisk: ConflictRisk
  rebaseRecommended: Boolean
}
```

### 5.3 HealthLevel
```
HealthLevel {
  HEALTHY   // 🟢 skóre 70-100
  WARNING   // 🟡 skóre 40-69
  CRITICAL  // 🔴 skóre 0-39
  UNKNOWN   // ⚪ nedostatek dat
}
```

### 5.4 ConflictRisk
```
ConflictRisk {
  level: RiskLevel (LOW, MEDIUM, HIGH, CRITICAL)
  conflictingFiles: List<String>
  conflictingBranches: List<String>
  estimatedConflictLines: Int
}
```

---

## 6. Technické specifikace

### 6.1 Plugin Metadata
- **Plugin ID:** `com.jakubjirak.branchhealth`
- **Package:** `com.jakubjirak.branchhealth`
- **Version:** `2026.2.1`
- **sinceBuild:** `243`
- **untilBuild:** `252.*`

### 6.2 Balíčková struktura
```
com.jakubjirak.branchhealth
  ├── .model        — BranchInfo, BranchHealth, HealthLevel, ConflictRisk
  ├── .analyzer     — BranchAnalyzerService, stáří, aktivita, ahead/behind
  ├── .stale        — StaleBranchDetector, cleanup suggestions
  ├── .conflict     — ConflictPredictorService
  ├── .rebase       — RebaseAdvisorService
  ├── .orphan       — OrphanCommitDetector
  ├── .ui           — BranchHealthToolWindow, BranchListPanel, renderers
  ├── .notification — BranchHealthNotifier
  ├── .settings     — BranchHealthSettings, BranchHealthConfigurable
  └── .licensing    — FreemiumGate, LicenseChecker
```

---

## 7. Freemium model

### Free tier
- Branch age tracking (stáří, poslední commit)
- Activity heatmap (základní)
- Stale branch detection (identifikace)
- Branch comparison (ahead/behind)
- Tool window s health skóre
- **Limit: max 5 větví**

### Paid tier ($6/měsíc)
- Neomezený počet větví
- Merge conflict prediction (FR-3)
- Stale branch cleanup automatizace (FR-5)
- Orphan commit detection (FR-6)
- Rebase advisor (FR-7)
- Pokročilé notifikace (conflict risk, periodický souhrn)
- Pokročilé filtry a exporty
