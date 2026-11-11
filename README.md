# Branch Health Dashboard

[![Plugin Verification](https://github.com/JirakJ/branch-health-dashboard/actions/workflows/verify.yml/badge.svg)](https://github.com/JirakJ/branch-health-dashboard/actions/workflows/verify.yml)
[![Version](https://img.shields.io/badge/version-2026.8.0-blue.svg)](https://github.com/JirakJ/branch-health-dashboard/releases)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)

> Monitor, score, and clean up your Git branches — right inside your JetBrains IDE.

## ✨ Features

- **Health score** — 0–100 per branch from age, 30-day activity, commits behind the base branch, and overlap with base-branch changes
- **Status levels** — 🟢 Healthy (≥75), 🟡 Aging (≥50), 🔴 Stale (≥25), ⚫ Dead (<25)
- **Ahead/behind** — against `main`, `master` or `develop` (local first, then `origin/`)
- **Conflict risk** — files changed on both the branch and the base branch since their merge base
- **Rebase advisor** — REBASE / MERGE / EITHER recommendation from the ahead/behind counts
- **Tool window** — sortable table (health, age, activity, ahead/behind, conflict risk, advice) with a status filter
- **Notifications** — scan summary and a stale-branch warning after each scan
- **Cleanup** — delete local branches already merged into HEAD, after a confirmation listing them

## 📦 Installation

### From JetBrains Marketplace
1. Open **Settings** → **Plugins** → **Marketplace**
2. Search for **Branch Health Dashboard**
3. Click **Install** and restart the IDE

### From ZIP
1. Download the latest release from [Releases](https://github.com/JirakJ/branch-health-dashboard/releases)
2. Open **Settings** → **Plugins** → ⚙️ → **Install Plugin from Disk…**
3. Select the downloaded ZIP file and restart the IDE

## 🚀 Quick Start

1. Open a project with a Git repository
2. Open the **Branch Health** tool window (bottom panel)
3. Click **Scan Branches** to analyze all branches
4. Review the health scores and status indicators in the table
5. Use the filter dropdown to focus on **Stale** or **Dead** branches
6. Run **Cleanup Stale Branches** from the VCS menu to delete branches already merged into HEAD

## ⚙️ Configuration

Open **Settings** → **Tools** → **Branch Health Dashboard** to configure:

| Setting | Default | Description |
|---------|---------|-------------|
| Stale Threshold (days) | `30` | Number of days without commits before a branch is considered stale |
| Ignored Branches | `main, develop, master` | Comma-separated branch names excluded from scans and cleanup (matches local and `origin/` names) |
| Enable Notifications | `true` | Show the scan summary and stale-branch notifications |

## 🎯 Usage

### Health Score Calculation

The health score (0–100) is computed from multiple factors:

| Factor | Impact |
|--------|--------|
| Branch age | Up to −40 points (−15 aging, −25 stale) |
| Activity (commits/day over the last 30 days) | +10 (≥1), +5 (≥0.3), 0 (≥0.05), −10 (below) |
| Behind count | Up to −15 points based on how far behind the base branch |
| Conflict risk | Up to −20 points: files changed on both sides ÷ all changed files |

**Thresholds:** ≥75 = 🟢 Healthy, ≥50 = 🟡 Aging, ≥25 = 🔴 Stale, <25 = ⚫ Dead

### Scanning Branches
- Click **Scan Branches** in the tool window or use **VCS** → **Scan Branch Health**
- Local and remote-tracking branches of the project's first Git repository are scanned; the base branch itself is skipped

### Cleaning Up
- **VCS** → **Cleanup Stale Branches**
- Shows a confirmation dialog listing branches to delete
- Never offers `main`, `master`, `develop`, your ignored branches, the current branch, or branches checked out in other worktrees
- Only lists branches already merged into HEAD and deletes them with `git branch -d`

## 📋 Requirements

- IntelliJ IDEA 2023.2+ (or any JetBrains IDE build 232+)
- Git plugin (bundled with all JetBrains IDEs)

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/branch-health-dashboard.git
cd branch-health-dashboard
./gradlew build
```

The built plugin ZIP will be in `build/distributions/`.

## 📝 License

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE).

## 👤 Author

**Jakub Jirák** — [@JirakJ](https://github.com/JirakJ)

## Free & open source

Since version 2026.8.0 the plugin is completely free — every feature is unlocked and no license is needed. The source code is open source under the [Apache License 2.0](LICENSE).

## Contributing

Issues and pull requests are welcome at https://github.com/JirakJ/branch-health-dashboard.
