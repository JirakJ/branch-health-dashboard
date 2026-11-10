# Changelog

## [2026.8.0] - 2026-11-10

### Changed
- The plugin is now completely free — all features unlocked, no license needed.
- Source code is now open source under Apache-2.0: https://github.com/JirakJ/branch-health-dashboard

## [2026.3.6] - 2026-04-18

### Changed
- Enabled freemium licensing via `<product-descriptor optional="true">`. Plugin remains fully functional without a license; premium features may be introduced in future minor releases.


## [2026.2.4] - 2026-02-13

### Added
- Added ProGuard obfuscation for code protection


## [2026.2.3] - 2026-02-13

### Changed
- Version bump for JetBrains Marketplace publication


## [2026.2.2] - 2026-02-13

### Changed
- Expanded compatibility range: sinceBuild 232.0+ (IntelliJ 2023.2+)
- Removed upper version bound for future IDE compatibility


## [2026.2.1] - 2026-02-13

### Added
- Initial release
- Branch health scoring (0–100) with color-coded levels (Healthy / Aging / Stale / Dead)
- Branch age tracking with configurable stale threshold (default 30 days)
- Activity rate analysis (HIGH / MODERATE / LOW / INACTIVE trends)
- Ahead/behind branch comparison
- Branch Health tool window with sortable table and status filter
- Balloon notifications for stale branches and scan completion summaries
- Settings panel (Tools → Branch Health Dashboard) for threshold, interval, ignored branches, and notifications
- Scan Branches action (VCS menu)
- Conflict prediction with risk scoring (LOW / MEDIUM / HIGH / CRITICAL)
- Rebase advisor with REBASE / MERGE / EITHER recommendations
- Cleanup automation for batch-deleting merged branches (protects main/master/develop)
- Orphan commit detection
- Unlimited branch monitoring
