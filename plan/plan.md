# Branch Health Dashboard — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 0: Plugin Ikona & Branding

- [ ] Navrhnout plugin ikonu (SVG + PNG)
  - [ ] Koncept: Git branch s pulzním/health monitorem (heartbeat linka)
  - [ ] Hlavní ikona: 40×40 SVG pro Marketplace listing
  - [ ] Toolbar ikona: 16×16 + 13×13 SVG (light + dark varianta)
  - [ ] Tool window ikona: 13×13 SVG
  - [ ] `src/main/resources/META-INF/pluginIcon.svg` (40×40, light theme)
  - [ ] `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark theme)
  - [ ] Action ikony pro toolbar/menu (16×16 SVG, light + dark)
- [ ] Barevná paleta: 🟢 HEALTHY, 🟡 WARNING, 🔴 CRITICAL, ⚪ UNKNOWN
- [ ] Marketplace banner obrázek (1280×800)
- [ ] Screenshoty pro Marketplace listing (min 3, 1280×800 PNG)

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — IntelliJ Platform 2024.3+, JVM 17, Kotlin 2.1.0
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
  - [ ] Dependency: Git4Idea
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jakubjirak.branchhealth`
  - [ ] Závislosti: `com.intellij.modules.platform`, `Git4Idea`
- [ ] Balíčková struktura: `com.jakubjirak.branchhealth.*`
  - [ ] `.model` — datové třídy
  - [ ] `.analyzer` — branch analyzer service
  - [ ] `.stale` — stale branch detector
  - [ ] `.conflict` — conflict predictor
  - [ ] `.rebase` — rebase advisor
  - [ ] `.orphan` — orphan commit detector
  - [ ] `.ui` — tool window, panely, renderers
  - [ ] `.notification` — notifikace
  - [ ] `.settings` — konfigurace
  - [ ] `.licensing` — freemium gate
- [ ] `src/main/resources/messages/BranchHealthBundle.properties`
- [ ] `.gitignore` (build/, .idea/, .gradle/, .kotlin/, out/)
- [ ] `gradle/libs.versions.toml` — version catalog
- [ ] `LICENSE` (Apache-2.0)
- [ ] `.editorconfig` (Kotlin style conventions)
- [ ] Detekt konfigurace (`detekt.yml`)
- [ ] GitHub Actions CI workflow (`.github/workflows/build.yml`)
- [ ] Ověřit `./gradlew build`

## Fáze 2: Datový model

- [ ] `BranchInfo.kt`
  - [ ] `name: String`, `isLocal: Boolean`, `isRemote: Boolean`
  - [ ] `lastCommitDate: Instant`, `lastCommitAuthor: String`, `lastCommitMessage: String`
  - [ ] `createdDate: Instant?`, `commitCount: Int`
  - [ ] `aheadCount: Int`, `behindCount: Int`
  - [ ] `isMerged: Boolean`, `isProtected: Boolean`
- [ ] `BranchHealth.kt`
  - [ ] `branch: BranchInfo`
  - [ ] `healthLevel: HealthLevel`, `healthScore: Int` (0–100)
  - [ ] `ageInDays: Int`, `daysSinceLastCommit: Int`
  - [ ] `isStale: Boolean`
  - [ ] `conflictRisk: ConflictRisk`, `rebaseRecommended: Boolean`
- [ ] `HealthLevel.kt` — enum: HEALTHY (🟢), WARNING (🟡), CRITICAL (🔴), UNKNOWN (⚪)
- [ ] `ConflictRisk.kt`
  - [ ] `level: RiskLevel` (LOW, MEDIUM, HIGH, CRITICAL)
  - [ ] `conflictingFiles: List<String>`, `conflictingBranches: List<String>`
  - [ ] `estimatedConflictLines: Int`
- [ ] Unit testy

## Fáze 3: Branch Analyzer

- [ ] `BranchAnalyzerService.kt` — project-level service
  - [ ] `fun analyzeBranches(project: Project): List<BranchHealth>`
  - [ ] Stáří větve (čas od prvního commitu na větvi)
  - [ ] Čas od posledního commitu
  - [ ] Ahead/behind count vůči base větvi (main/master/develop)
  - [ ] Health skóre kalkulace (0–100):
    - [ ] Stáří: -1 bod za každý den nad 7 dní
    - [ ] Neaktivita: -2 body za každý den bez commitu nad 3 dny
    - [ ] Divergence: -1 bod za každý commit behind
    - [ ] Merge status: +10 bodů pokud merged
- [ ] `BranchInfoCollector.kt`
  - [ ] Čtení větví přes Git4Idea API (`GitRepositoryManager`, `GitBranchUtil`)
  - [ ] Extrakce commit history per větev
  - [ ] Detekce base větve (main/master/develop — konfigurovatelné)
- [ ] `HealthScoreCalculator.kt`
  - [ ] Konfigurovatelné váhy pro jednotlivé metriky
  - [ ] Mapování skóre na HealthLevel
- [ ] Caching výsledků s TTL (default: 5 minut)
- [ ] Unit testy

## Fáze 4: Stale Branch Detector

- [ ] `StaleBranchDetector.kt`
  - [ ] `fun detectStaleBranches(branches: List<BranchHealth>): List<BranchHealth>`
  - [ ] Konfigurovatelný práh neaktivity (default: 30 dní)
  - [ ] Rozlišení: merged stale vs unmerged stale
  - [ ] Ochrana chráněných větví (main, master, develop — konfigurovatelné)
- [ ] `CleanupSuggestion.kt`
  - [ ] `branch: BranchInfo`, `reason: String`, `isSafeToDelete: Boolean`
  - [ ] Safe to delete: merged + stale + ne-protected
  - [ ] Unsafe: unmerged stale (varování, ale doporučení ke kontrole)
- [ ] `CleanupExecutor.kt` (Paid)
  - [ ] Batch delete s potvrzovacím dialogem
  - [ ] Dry-run mód
  - [ ] Undo podpora (vytvoření větve zpět z posledního commitu)
- [ ] Unit testy

## Fáze 5: Conflict Predictor (Paid)

- [ ] `ConflictPredictorService.kt`
  - [ ] `fun predictConflicts(branches: List<BranchInfo>): Map<Pair<String, String>, ConflictRisk>`
  - [ ] Analýza diff mezi větvemi — společně modifikované soubory
  - [ ] Risk level kalkulace:
    - [ ] LOW: 0–2 společné soubory, žádné stejné řádky
    - [ ] MEDIUM: 3–5 společných souborů
    - [ ] HIGH: 6+ společných souborů nebo stejné řádky
    - [ ] CRITICAL: 10+ společných souborů s překrývajícími se změnami
  - [ ] Optimalizace: analýza jen aktivních větví (ne stale)
- [ ] `FileChangeAnalyzer.kt`
  - [ ] Extrakce změněných souborů per větev (diff vůči base)
  - [ ] Detekce překrývajících se řádkových rozsahů
- [ ] Unit testy

## Fáze 6: Rebase Advisor (Paid)

- [ ] `RebaseAdvisorService.kt`
  - [ ] `fun advise(branch: BranchHealth): RebaseAdvice`
  - [ ] Doporučení na základě:
    - [ ] Divergence (ahead/behind count)
    - [ ] Počet konfliktních souborů
    - [ ] Sdílení větve (další autoři → merge, solo → rebase)
    - [ ] Historie větve (lineární → rebase, komplexní → merge)
  - [ ] `RebaseAdvice`:
    - [ ] `recommendation: Enum (REBASE, MERGE, EITHER)`
    - [ ] `reason: String`
    - [ ] `confidence: Float (0.0–1.0)`
- [ ] Unit testy

## Fáze 7: Orphan Commit Detector (Paid)

- [ ] `OrphanCommitDetector.kt`
  - [ ] `fun detectOrphanCommits(project: Project): List<OrphanCommit>`
  - [ ] Využití `git fsck --unreachable` nebo `git log --all` porovnání
  - [ ] Filtrace: jen commity (ne tree/blob objekty)
- [ ] `OrphanCommit.kt`
  - [ ] `sha: String`, `author: String`, `date: Instant`, `message: String`
  - [ ] `action: RecoverAction` — vytvoření větve z commitu
- [ ] Unit testy

## Fáze 8: Tool Window UI

- [ ] `BranchHealthToolWindowFactory.kt` implements `ToolWindowFactory`
  - [ ] Registrace v `plugin.xml`: `<toolWindow id="Branch Health" ...>`
  - [ ] Ikona: 13×13 SVG
- [ ] `BranchHealthToolWindow.kt`
  - [ ] Hlavní panel s tabulkou větví
  - [ ] Toolbar: Refresh, Filter, Cleanup, Settings
- [ ] `BranchListPanel.kt`
  - [ ] JBTable s kolonkami: Název, Stáří, Poslední commit, Health, Conflict Risk
  - [ ] Barevné kódování dle HealthLevel
  - [ ] Řazení dle libovolné kolonky
  - [ ] Filtrace: All/Local/Remote, Healthy/Warning/Critical, Active/Stale
- [ ] `BranchDetailPanel.kt`
  - [ ] Detail vybrané větve: metriky, heatmapa aktivity, conflict souhrn
  - [ ] Akce: Checkout, Merge, Rebase, Delete
- [ ] `HealthScoreRenderer.kt`
  - [ ] Vizuální zobrazení health skóre (progress bar + barva)
- [ ] `ConflictRiskRenderer.kt`
  - [ ] Badge s rizikem konfliktu (LOW/MEDIUM/HIGH/CRITICAL)
- [ ] `ActivityHeatmapPanel.kt`
  - [ ] Heatmapa commitů per den (posledních 90 dní)
  - [ ] Barevná škála: žádná aktivita → vysoká aktivita

## Fáze 9: Notifications

- [ ] `BranchHealthNotifier.kt`
  - [ ] Registrace notification group v `plugin.xml`:
    ```xml
    <notificationGroup id="Branch Health Dashboard"
                       displayType="BALLOON" />
    ```
  - [ ] Notifikace při detekci nové stale větve
  - [ ] Notifikace při HIGH/CRITICAL conflict risku (Paid)
  - [ ] Periodický health souhrn (denní/týdenní — konfigurovatelné, Paid)
- [ ] `NotificationThrottler.kt`
  - [ ] Omezení frekvence notifikací (max 1 per typ per hodinu)
  - [ ] Agregace: „5 nových stale větví" místo 5 samostatných notifikací

## Fáze 10: Settings

- [ ] `BranchHealthSettings.kt` implements `PersistentStateComponent`
  - [ ] Stale branch práh (dny): default 30
  - [ ] Chráněné větve: default `main, master, develop`
  - [ ] Base větev pro analýzu: auto-detect nebo manuální
  - [ ] Health skóre váhy (stáří, aktivita, divergence)
  - [ ] Notification preferences (enable/disable per typ)
  - [ ] Auto-refresh interval (default: 5 minut)
- [ ] `BranchHealthConfigurable.kt` — Settings → Tools → Branch Health Dashboard
  - [ ] Stale detection nastavení
  - [ ] Chráněné větve editor
  - [ ] Notification toggles
  - [ ] Health skóre customizace
- [ ] Unit testy

## Fáze 11: Freemium & Licensing Infrastructure

- [ ] Integrace JetBrains Marketplace Licensing API
  - [ ] `LicenseChecker.kt` — ověření licence
  - [ ] `LicenseStatus.kt` — enum: FREE, TRIAL, PAID, EXPIRED
  - [ ] `FreemiumGate.kt` — centrální guard pro paid features
    - [ ] `fun isPaidFeatureAvailable(featureId: String): Boolean`
    - [ ] Graceful degradation — paid UI elementy skryté/disabled ve free tieru
  - [ ] Trial period: 30 dní plný přístup
- [ ] `plugin.xml` — `<product-descriptor>` element
  - [ ] `code` atribut (unique product code)
  - [ ] `release-date`, `release-version`
- [ ] Feature flags
  - [ ] `FeatureRegistry.kt` — definice free vs. paid features:
    - [ ] Free: branch age, basic activity, stale detection (max 5 větví)
    - [ ] Paid: unlimited větve, conflict prediction, cleanup, rebase advisor, orphan detection
- [ ] Upgrade prompt
  - [ ] Non-intrusive balloon při pokusu o paid feature
  - [ ] „Upgrade to Pro" action → Marketplace stránka
  - [ ] Max 1× denně
- [ ] Unit testy

## Fáze 12: Production-Grade Quality

- [ ] Error Handling & Resilience
  - [ ] Globální exception handler (`ErrorReportSubmitter` implementace)
  - [ ] Graceful degradation — plugin nesmí crashnout IDE
  - [ ] Try-catch na všech EP entry pointech
  - [ ] Timeout na Git operacích (default: 30s)
  - [ ] Rate limiting na analýzu (ne častěji než 1× za minutu)
- [ ] Logging
  - [ ] `Logger` instance v každé třídě (IntelliJ `Logger.getInstance()`)
  - [ ] Structured logging: DEBUG pro vývoj, INFO pro operace, WARN/ERROR pro problémy
  - [ ] Žádné citlivé údaje v logech (commit SHA OK, obsah souborů NE)
- [ ] Telemetrie (opt-in, anonymní)
  - [ ] `PluginUsageTracker.kt` — FUS integrace
  - [ ] Metriky: počet analyzovaných větví, cleanup akcí, conflict detekcí
  - [ ] Registrace FUS group v `plugin.xml`
- [ ] Performance
  - [ ] Profiling na velkých repozitářích (500+ větví)
  - [ ] Background thread pro všechny Git/I/O operace
  - [ ] Read/write action správně (ReadAction, WriteAction, smart mode check)
  - [ ] Disposal pattern: `Disposable` pro všechny listeners a services
  - [ ] Memory leak check (WeakReference kde potřeba)
  - [ ] Lazy loading: analýza jen viditelných větví, zbytek on-demand
- [ ] Kódová kvalita
  - [ ] Kotlin coding conventions (official JetBrains style)
  - [ ] Detekt pro statickou analýzu
  - [ ] Žádné deprecated API
  - [ ] KDoc pro všechny public API
- [ ] CI/CD Pipeline
  - [ ] GitHub Actions workflow:
    - [ ] `./gradlew check` — kompilace + testy
    - [ ] `./gradlew verifyPlugin` — plugin strukturální validace
    - [ ] `./gradlew runPluginVerifier` — binární kompatibilita
    - [ ] Qodana scan
  - [ ] Release workflow:
    - [ ] Tag-based release → `publishPlugin`
    - [ ] Changelog generace z git commitů
  - [ ] Dependabot / Renovate pro dependency updates

## Fáze 13: Tests

- [ ] Unit testy: ≥80 % coverage core logiky
  - [ ] `BranchInfoTest.kt` — datový model
  - [ ] `HealthScoreCalculatorTest.kt` — skóre kalkulace
  - [ ] `StaleBranchDetectorTest.kt` — stale detekce, prahové hodnoty
  - [ ] `ConflictPredictorTest.kt` — conflict risk kalkulace
  - [ ] `RebaseAdvisorTest.kt` — rebase vs merge logika
  - [ ] `OrphanCommitDetectorTest.kt` — orphan detekce
  - [ ] `FreemiumGateTest.kt` — licensing logic
- [ ] Integrační testy: `BasePlatformTestCase`
  - [ ] `BranchAnalyzerIntegrationTest.kt` — end-to-end analýza s mock Git repozitářem
  - [ ] `ToolWindowIntegrationTest.kt` — tool window rendering
- [ ] UI testy: `HeavyPlatformTestCase`
  - [ ] `BranchHealthToolWindowUITest.kt` — tool window interakce
  - [ ] `BranchListPanelUITest.kt` — tabulka, filtrace, řazení
- [ ] Performance testy
  - [ ] Benchmark: analýza 100/500/1000 větví
  - [ ] Memory profiling

## Fáze 14: Release

- [ ] README.md s screenshoty, feature listem, instalací
- [ ] CHANGELOG.md (Keep a Changelog format)
- [ ] `./gradlew verifyPlugin`, `./gradlew buildPlugin`
- [ ] Manuální testování s reálným Git repozitářem (mnoho větví)
- [ ] Cross-IDE test (IntelliJ IDEA, WebStorm, PyCharm, GoLand)
- [ ] Cross-OS test (macOS, Linux, Windows)
- [ ] Marketplace listing:
  - [ ] Popis, screenshoty, banner
  - [ ] Tagy: git, branch, health, stale, merge, conflict
  - [ ] Pricing: Free + Pro $6/měsíc
- [ ] Git tag `v2026.2.1`
- [ ] `./gradlew publishPlugin`

---

## Budoucí fáze

- [ ] Branch naming convention checker (enforcing naming patterns)
- [ ] PR readiness indicator (linked issues, tests passing, approvals)
- [ ] Team branch dashboard (sdílený přehled pro celý tým)
- [ ] Git hooks integrace (pre-push stale check)
- [ ] Slack/Teams notifikace pro stale větve
- [ ] Branch lifecycle analytics (průměrná doba života větve, merge rate)
- [ ] AI-powered conflict resolution suggestions
