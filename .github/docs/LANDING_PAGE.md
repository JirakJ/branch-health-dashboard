# Branch Health Dashboard

## Monitor, score, and clean up your Git branches — right inside your JetBrains IDE

Long-lived branches silently accumulate risk. Merge conflicts grow. Code drifts apart. By the time you notice, integration is painful. Branch Health Dashboard gives every branch a health score from 0–100 so you can act before problems start.

[Get Started — Free](https://plugins.jetbrains.com/plugin/branch-health-dashboard) | [Go Pro — $6/month](https://plugins.jetbrains.com/plugin/branch-health-dashboard/pricing)

---

## The Problem

Long-lived branches are a top source of integration pain on any team. Developers create feature branches, get pulled into other work, and forget about them. Days turn into weeks. The branch falls behind `main`. Files diverge. When it's finally time to merge, conflicts are everywhere.

Most teams have no visibility into branch health until something breaks. There is no warning, no dashboard, and no guidance — just a messy merge on a Friday afternoon.

**Branch Health Dashboard fixes this.** It brings branch health monitoring directly into your IDE with clear scores, actionable alerts, and automated cleanup.

---

## Key Features

### 🩺 Health Score (0–100)
Every branch gets a single number that tells you how healthy it is. The score factors in age, recent activity, how far behind the base branch it is, and conflict risk. No guesswork — just a clear signal.

- **≥ 75** — Healthy (green)
- **≥ 50** — Aging (yellow)
- **≥ 25** — Stale (orange)
- **< 25** — Dead (red)

### 📅 Branch Age Tracking
Branches are color-coded by age so you can spot problems at a glance.

| Status  | Age        | Color  |
|---------|------------|--------|
| Healthy | ≤ 7 days   | Green  |
| Aging   | 7–30 days  | Yellow |
| Stale   | > 30 days  | Orange |
| Dead    | Inactive   | Red    |

### 📊 Activity Heatmap
See commits-per-day for each branch with trend indicators. Quickly identify branches that have gone quiet and branches that are actively being worked on.

### 🔔 Stale Branch Notifications
Get balloon alerts inside your IDE when branches cross your configured staleness threshold. Choose from 7, 14, 30, 60, or 90 days — whatever fits your workflow.

### ⚡ Branch Comparison
See exactly how far ahead or behind each branch is relative to its base. Understand at a glance which branches need attention.

### 🪟 Tool Window Dashboard
A sortable, filterable table right in your IDE. Sort by health score, age, or activity. Filter by status (Healthy, Aging, Stale, Dead). Everything you need in one view.

### 🔮 Conflict Prediction (Pro)
Detect common files across branches and get a risk score — LOW, MEDIUM, HIGH, or CRITICAL — before you attempt a merge. Stop conflicts before they happen.

### 🧹 Cleanup Automation (Pro)
Batch-delete merged branches with a single click. Dry-run mode lets you preview what will be removed. Protected branches (`main`, `master`, `develop`) are never touched.

---

## How It Works

### Step 1 — Install
Install Branch Health Dashboard from the JetBrains Marketplace. It works with any project that uses Git.

### Step 2 — Open the Dashboard
Open the Branch Health tool window from the sidebar. Every local and remote branch appears with its health score, age, and activity.

### Step 3 — Review and Act
Sort and filter branches to find problems. Click any branch to see detailed metrics — behind count, conflict risk, commit history, and recommended actions.

### Step 4 — Clean Up
Use the cleanup automation to remove merged and dead branches. Protect important branches. Keep your repository lean and your team focused.

---

## Pricing

### Free
**$0/month**

- Health score (0–100)
- Branch age tracking with color coding
- Activity analysis with heatmap
- Stale branch notifications
- Branch comparison (ahead/behind)
- Tool window dashboard
- **Up to 5 branches**

[Install Free](https://plugins.jetbrains.com/plugin/branch-health-dashboard)

### Pro
**$6/month**

Everything in Free, plus:

- **Unlimited branches**
- Conflict prediction (LOW/MEDIUM/HIGH/CRITICAL)
- Rebase advisor (REBASE/MERGE/EITHER recommendations)
- Cleanup automation (batch delete, dry-run, branch protection)
- Orphan commit detection
- Enhanced notifications
- Team notifications
- Branch policy enforcement

[Start Pro](https://plugins.jetbrains.com/plugin/branch-health-dashboard/pricing)

---

## What Users Are Saying

> "Branch Health Dashboard saved us from a week of merge conflict hell. We now clean up stale branches before they become a problem."
> — *[Name], Senior Developer at [Company]*

> "The health score is simple and effective. Our team went from 40+ stale branches to single digits within a month."
> — *[Name], Engineering Manager at [Company]*

> "Conflict prediction alone is worth the Pro subscription. We catch risky merges before they happen."
> — *[Name], Tech Lead at [Company]*

*Have feedback? We'd love to hear from you — [submit a testimonial](https://github.com/branch-health-dashboard/feedback).*

---

## Frequently Asked Questions

### What IDEs are supported?
Branch Health Dashboard works with IntelliJ IDEA 2024.3 and later (builds 243–252.*). It requires the bundled Git4Idea plugin and Java 17+.

### Does it work with remote branches?
Yes. The dashboard monitors both local and remote branches. Remote branch data is refreshed whenever your IDE fetches from the remote.

### What does the health score measure?
The score starts at 100 and adjusts based on four factors: branch age (up to −40 points), recent activity (+10 to −10 points), behind count relative to the base branch (up to −15 points), and conflict risk (up to −20 points, Pro only).

### Can I customize staleness thresholds?
Yes. You can set the staleness alert threshold to 7, 14, 30, 60, or 90 days in the plugin settings.

### Is the Pro subscription per-user or per-team?
The Pro subscription is per-user. Each developer who wants Pro features needs their own subscription.

---

## Get Started Today

Stop letting stale branches silently accumulate risk. Install Branch Health Dashboard and give your team the visibility they need to keep Git workflows clean.

[Install from JetBrains Marketplace](https://plugins.jetbrains.com/plugin/branch-health-dashboard)
