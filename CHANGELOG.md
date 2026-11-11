# Changelog

## [Unreleased]

## [2026.9.0] - 2026-11-11

### Fixed
- Commit dates were parsed from `%(committerdate:iso)` with a broken pattern, so every branch was
  treated as just committed; dates now come from `iso-strict` and branches with unparseable dates are skipped.
- Ahead/behind, recent commit count and conflict risk were never collected (always 0 / none), so every
  branch got the inactivity penalty and "EITHER" advice. The scan now runs `git rev-list` and
  `git diff --name-only` against the base branch (`main`/`master`/`develop`, local first, then `origin/`).
- Activity is now commits per day over the last 30 days instead of commits divided by days since the last commit.
- Remote-tracking branches were never recognised as remote (`origin/HEAD` showed up as a branch called `origin`).
- Health, age and activity columns sorted as text; they are now numeric. Added Status and Ahead/Behind columns.
- Notifications and table refresh now happen on the EDT after the background scan.
- Cleanup no longer runs git on the EDT, honours the ignored-branches setting and skips branches checked
  out in other worktrees.

### Removed
- Non-functional "Scan interval" setting (there is no background scanning).
- Description/README claims for features that never shipped (activity heatmap, orphan commit detection,
  team notifications/policy enforcement, dry-run mode), the unused message bundle, the unused
  conflict-risk notification, the leftover ProGuard obfuscation task and MARKETPLACE*.md drafts.

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
