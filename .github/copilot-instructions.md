# Copilot Instructions — Branch Health Dashboard

## Projekt
JetBrains IDE plugin — monitoruje zdraví Git větví: stáří, aktivitu, predikci merge konfliktů, detekci stale větví, orphan commitů a rebase vs merge doporučení.

## Technický stack
- **Jazyk:** Kotlin (JVM 17+)
- **Build:** Gradle (Kotlin DSL) s `org.jetbrains.intellij.platform` plugin 2.x
- **Platform:** IntelliJ Platform SDK 2024.3+
- **Testování:** JUnit 5 + `BasePlatformTestCase`
- **Git integrace:** Git4Idea plugin API

## Build a testy
```bash
./gradlew build
./gradlew test
./gradlew test --tests "*.BranchAnalyzerTest.testStaleBranchDetection"
./gradlew runIde
./gradlew verifyPlugin
```

## Architektura
- **Model:** `BranchInfo`, `BranchHealth`, `HealthLevel`, `ConflictRisk` — datové třídy
- **Analyzer:** `BranchAnalyzerService` — stáří větví, aktivita, ahead/behind analýza
- **Stale Detector:** `StaleBranchDetector` — identifikace neaktivních větví, cleanup doporučení
- **Conflict Predictor:** `ConflictPredictorService` — analýza společných souborů mezi větvemi, predikce konfliktů
- **Rebase Advisor:** `RebaseAdvisorService` — doporučení rebase vs merge dle divergence
- **Orphan Detector:** `OrphanCommitDetector` — detekce commitů nedostupných z žádné větve/tagu
- **UI:** `BranchHealthToolWindow` (ToolWindowFactory), `BranchListPanel`, `HealthScoreRenderer`
- **Notifications:** `BranchHealthNotifier` — upozornění na stale větve, rizika konfliktů

## Konvence
- Balíčky: `com.jakubjirak.branchhealth.*`
- Health levels: HEALTHY (🟢), WARNING (🟡), CRITICAL (🔴), UNKNOWN (⚪)
- Texty v `messages/BranchHealthBundle.properties`
- Verzování: `2026.2.X`
- Plugin ID: `com.jakubjirak.branchhealth`

## Production-grade požadavky
- Všechny Git/I/O operace na background threadech (`Task.Backgroundable`, coroutines)
- Read/Write actions správně (`ReadAction.run {}`, `WriteAction.run {}`)
- `Disposable` pattern pro listeners a services
- Exception handling: žádné unhandled exceptions, graceful degradation
- Logging: `Logger.getInstance(MyClass::class.java)` v každé třídě
- EDT nesmí být blokován — žádné síťové/git/file operace na EDT
- Smart mode check (`DumbService.isDumb()`) kde potřeba

## Freemium architektura
- `FreemiumGate` centrální guard: `FreemiumGate.isPaidFeatureAvailable("feature.id")`
- JetBrains Marketplace Licensing API přes `<product-descriptor>` v `plugin.xml`
- Free tier: základní health metriky, max 5 větví
- Paid tier: neomezené větve, conflict prediction, cleanup automatizace
- Paid UI elementy: skryté nebo disabled ve free tieru (ne crash/error)
- Trial: 30 dní plný přístup, pak downgrade na free tier
- Upgrade prompt: max 1× denně, non-intrusive balloon

## CI/CD
- GitHub Actions: `./gradlew check`, `verifyPlugin`, `runPluginVerifier`
- Detekt pro statickou analýzu Kotlin kódu
- Tag-based release → `publishPlugin` na Marketplace
- Kotlin coding conventions (JetBrains official style)

## Ikona
- `src/main/resources/META-INF/pluginIcon.svg` (40×40, light)
- `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark)
- Action ikony: 16×16 + 13×13 SVG (light + dark varianty)

## Freemium hranice
- Free: základní health skóre, stáří větví, aktivita, max 5 větví
- Paid: neomezené větve, conflict prediction, rebase advisor, orphan detection, cleanup automatizace, notifikace
