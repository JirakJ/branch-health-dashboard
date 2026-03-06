# Branch Health Dashboard

[![Build](https://github.com/JirakJ/branch-health-dashboard/actions/workflows/build.yml/badge.svg)](https://github.com/JirakJ/branch-health-dashboard/actions/workflows/build.yml)
[![Version](https://img.shields.io/badge/version-2026.2.1-blue.svg)](https://github.com/JirakJ/branch-health-dashboard/releases)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

> Monitor, score, and clean up your Git branches — right inside your JetBrains IDE.

## ✨ Features

- **Branch age tracking** — color-coded health levels: 🟢 Healthy (≤7 days), 🟡 Aging (7–30 days), 🔴 Stale (>30 days), ⚫ Dead
- **Health score** — 0–100 score calculated from age, activity, behind-count, and conflict risk penalties
- **Activity heatmap** — commits-per-day rate with trend indicators (HIGH / MODERATE / LOW / INACTIVE)
- **Stale branch detection** — configurable threshold (7 / 14 / 30 / 60 / 90 days)
- **Branch comparison** — ahead/behind visualization against base branch
- **Tool window** — sortable table with health, age, activity columns and filter by status
- **Notifications** — balloon alerts for stale branches, scan completion summaries, and conflict risk warnings
- **Unlimited branches** — no cap on monitored branches
- **Conflict prediction** — detects common files across branches and calculates risk score (LOW / MEDIUM / HIGH / CRITICAL)
- **Rebase advisor** — ahead/behind analysis with REBASE / MERGE / EITHER recommendations
- **Cleanup automation** — batch-delete merged branches with dry-run mode (protects main, master, develop)
- **Orphan commit detection** — find commits not reachable from any branch

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
6. Run **Cleanup Stale Branches** from the VCS menu to batch-delete merged branches

## ⚙️ Configuration

Open **Settings** → **Tools** → **Branch Health Dashboard** to configure:

| Setting | Default | Description |
|---------|---------|-------------|
| Stale Threshold (days) | `30` | Number of days without commits before a branch is considered stale |
| Scan Interval (minutes) | `60` | How often to automatically re-scan branches |
| Ignored Branches | `main, develop, master` | Comma-separated list of branches excluded from stale detection |
| Enable Notifications | `true` | Show balloon notifications for stale branches and scan results |

## 🎯 Usage

### Health Score Calculation

The health score (0–100) is computed from multiple factors:

| Factor | Impact |
|--------|--------|
| Branch age | Up to −40 points (−15 aging, −25 stale) |
| Activity rate | +10 (HIGH), +5 (MODERATE), −10 (INACTIVE) |
| Behind count | Up to −15 points based on how far behind the base branch |
| Conflict risk | Up to −20 points based on common file overlap |

**Thresholds:** ≥75 = 🟢 Healthy, ≥50 = 🟡 Aging, ≥25 = 🔴 Stale, <25 = ⚫ Dead

### Scanning Branches
- **Manual:** Click **Scan Branches** in the tool window or use **VCS** → **Scan Branch Health**
- **Automatic:** Runs at the configured scan interval in the background

### Cleaning Up
- **VCS** → **Cleanup Stale Branches**
- Shows a confirmation dialog listing branches to delete
- Automatically protects `main`, `master`, and `develop` from deletion
- Only deletes branches already merged into a protected branch

## 📋 Requirements

- IntelliJ IDEA 2024.3+ (or any JetBrains IDE build 243–252.*)
- Git4Idea plugin (bundled with all JetBrains IDEs)
- Java 17+

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/branch-health-dashboard.git
cd branch-health-dashboard
./gradlew build
```

The built plugin ZIP will be in `build/distributions/`.

## 📝 License

MIT — [LICENSE](LICENSE)

## 👤 Author

**Jakub Jirák** — [@JirakJ](https://github.com/JirakJ)
