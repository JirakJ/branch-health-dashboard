# Branch Health Dashboard - JetBrains Marketplace Listing

## 📝 Plugin Information

**Plugin Name:** Branch Health Dashboard
**Plugin ID:** `com.jakubjirak.branchhealth`
**Version:** 2026.2.11
**Category:** Version Control
**Tags:** git, branches, merge-conflicts, cleanup, developer-tools, branch-management, stale-branches, health-score

---

## 🎯 Short Description (Max 255 chars)

Monitor Git branch health with age tracking, activity heatmaps, conflict prediction, and automated stale branch cleanup — all inside a real-time dashboard in your JetBrains IDE.

---

## 📖 Full Description

### Your Branches Are Telling You Something. Start Listening.

**Branch Health Dashboard** is a JetBrains plugin that gives you real-time visibility into the health of every Git branch in your repository. Track branch age, monitor commit activity, predict merge conflicts, and clean up stale branches — all from a dedicated dashboard inside your IDE.

#### 🔥 Why Branch Health Dashboard?

**Problem:** Long-lived branches rot. They diverge from main, accumulate merge conflicts, and become increasingly painful to integrate. Most teams don't realize a branch is stale until merge day — when it's too late. Meanwhile, dozens of already-merged branches clutter the repository.

**Solution:** Branch Health Dashboard assigns every branch a health score (0–100) based on age, commit activity, and divergence from base. Color-coded indicators (🟢 Healthy, 🟡 Aging, 🔴 Stale, ⚫ Dead) make it obvious which branches need attention — and automated cleanup handles the rest.

#### ⚡ Key Features

**📊 Health Score (0–100)**
- Composite score based on branch age, commit activity, and behind-count
- Color-coded status: 🟢 Healthy (≥75), 🟡 Aging (≥50), 🔴 Stale (≥25), ⚫ Dead (<25)
- Age impact: −15 for aging branches, −25 for stale branches (up to −40)
- Activity bonus: +10 for high activity, +5 for moderate, −10 for inactive

**📅 Branch Age Tracking**
- Real-time age calculation for every branch
- Configurable stale threshold (default: 30 days)
- Aging tiers: Healthy (≤7 days), Aging (7–30 days), Stale (>30 days)

**🔥 Activity Heatmap**
- Commits-per-day visualization
- Trend indicators: HIGH, MODERATE, LOW, INACTIVE
- Spot activity patterns and development velocity at a glance

**📈 Ahead/Behind Comparison**
- Visual divergence tracking vs. base branch
- See exactly how far each branch has drifted
- Integrated into the dashboard table

**🔔 Smart Notifications**
- Balloon alerts when branches go stale
- Scan summary notifications after each health check
- Configurable notification preferences

**🖥️ Dashboard Tool Window**
- Sortable, filterable table with health, age, and activity columns
- Accessible from the bottom panel ("Branch Health")
- Actions: Scan Branch Health, Cleanup Stale Branches (VCS menu)

#### 💎 Pricing

Free & open source (Apache-2.0) — every feature (including conflict prediction, rebase advisor, cleanup automation and orphan commit detection) is available to everyone, with no branch limit and no license needed.

#### 🎓 Perfect For

- **Development Teams** managing multiple feature branches simultaneously
- **Tech Leads** who need visibility into branch health across the team
- **Release Managers** tracking branch readiness before merge
- **DevOps Engineers** automating Git repository hygiene
- **Any developer** tired of merge conflict surprises on integration day

#### 📦 Requirements

- JetBrains IDE 2024.3+ with Git support (IntelliJ IDEA, WebStorm, PyCharm, GoLand, Rider, PhpStorm, CLion, RubyMine, Android Studio)
- Git4Idea plugin (bundled with all JetBrains IDEs)
- Java 17+

#### 🚀 Quick Start

1. Install **Branch Health Dashboard** from the JetBrains Marketplace
2. Open a Git-enabled project — the plugin auto-detects your repository
3. Open the **Branch Health** tool window (bottom panel) to see your dashboard
4. Run a scan via **VCS → Scan Branch Health** or wait for the automatic scan (default: every 60 minutes)
5. Configure thresholds and ignored branches under **Settings → Tools → Branch Health Dashboard**

---

## 📸 Screenshots & Media

### Required Screenshots
1. **Dashboard overview** — Branch Health tool window showing color-coded health scores for all branches
2. **Health score detail** — Expanded view of a single branch showing age, activity, and divergence metrics
3. **Activity heatmap** — Commits-per-day visualization with trend indicators
4. **Conflict prediction** — Risk assessment showing file overlaps and conflict probability
5. **Cleanup automation** — Batch delete dialog with dry-run mode and protected branch list
6. **Stale branch notification** — Balloon alert warning about an aging branch
7. **Settings panel** — Configuration for stale threshold, scan interval, and ignored branches
8. **Rebase advisor** — Recommendation dialog showing REBASE vs. MERGE analysis

---

## 🏷️ Marketplace Categories

**Primary:** Version Control
**Secondary:** Code Quality, Developer Tools

---

## 🔍 Keywords (SEO)

git branches, branch health, stale branches, branch cleanup, merge conflicts, conflict prediction, branch management, git hygiene, branch age, activity heatmap, rebase advisor, orphan commits, branch dashboard, git tools, branch monitoring, development velocity, version control, branch divergence

---

## 📋 Compatibility

**Compatible Products:**
- IntelliJ IDEA (Community & Ultimate)
- WebStorm
- PyCharm
- GoLand
- Rider
- PhpStorm
- CLion
- RubyMine
- Android Studio
- All IntelliJ-based IDEs with Git support

**Minimum Version:** 232 (IntelliJ IDEA 2024.3+)

---

## 📞 Vendor Information

**Vendor Name:** Jakub Jirak
**Email:** jirak@jakubjirak.com
**Website:** https://jakubjirak.com

**Support Channels:**
- Email: support@jakubjirak.com
- GitHub Issues: https://github.com/jakubjirak/branch-health-dashboard/issues

---

## ✅ Pre-Launch Checklist

- [ ] Plugin icon (SVG, 40x40) uploaded
- [ ] Screenshots (minimum 5) captured and uploaded
- [ ] Short description within 255-character limit
- [ ] Full description formatted with HTML for marketplace rendering
- [ ] Vendor profile completed (name, email, website)
- [ ] Support channels documented and active
- [ ] Plugin tested on minimum supported IDE version (2024.3)
- [ ] Plugin tested on latest IDE version
- [ ] Health score calculation verified against expected values
- [ ] Conflict prediction tested with diverging branches
- [ ] Cleanup automation tested with dry-run and protected branches
- [ ] Git4Idea dependency confirmed bundled in all target IDEs
- [ ] Notification behavior tested (stale alerts, scan summaries)
- [ ] change-notes populated for initial release
- [ ] Privacy policy URL added (if collecting any telemetry)
