# Changelog

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
- **Pro:** Conflict prediction with risk scoring (LOW / MEDIUM / HIGH / CRITICAL)
- **Pro:** Rebase advisor with REBASE / MERGE / EITHER recommendations
- **Pro:** Cleanup automation for batch-deleting merged branches (protects main/master/develop)
- **Pro:** Orphan commit detection
- **Pro:** Unlimited branch monitoring (Free tier: 5 branches)
- Freemium licensing gate with Free and Pro ($6/month) tiers
