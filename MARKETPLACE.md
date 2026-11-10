# Branch Health Dashboard — JetBrains Marketplace Listing

## Plugin Name
Branch Health Dashboard

## Tagline
Spot stale branches, predict merge conflicts, and keep your repo clean.

## Short Description
Real-time branch health monitoring with age tracking, conflict prediction, and automated cleanup. See which branches need attention before they become problems.

## Full Description

<p>
Stale branches silently accumulate in every repository — creating merge conflicts, confusing teammates, and bloating your Git history. <strong>Branch Health Dashboard</strong> gives you instant visibility into the health of every branch, predicts conflicts before they happen, and automates cleanup — all without leaving your IDE.
</p>

<h3>Key Features</h3>
<ul>
  <li><strong>Branch Age Tracking</strong> — Color-coded health indicators: 🟢 Healthy (≤7 days), 🟡 Aging (7–30 days), 🔴 Stale (>30 days), ⚫ Dead (no recent activity).</li>
  <li><strong>Health Score</strong> — 0–100 composite score based on age, recent activity, and how far behind the base branch each branch has fallen.</li>
  <li><strong>Activity Heatmap</strong> — Commits-per-day visualization with trend indicators so you can spot declining branches early.</li>
  <li><strong>Stale Branch Detection</strong> — Configurable thresholds (7, 14, 30, 60, or 90 days) to match your team's workflow.</li>
  <li><strong>Ahead/Behind Comparison</strong> — Visual comparison of every branch against its base, showing exactly how much divergence exists.</li>
  <li><strong>Dashboard Tool Window</strong> — Sortable, filterable table view of all branches with real-time status updates.</li>
  <li><strong>Stale Branch Notifications</strong> — Balloon alerts when branches cross your configured age thresholds.</li>
  <li><strong>Conflict Prediction</strong> — Detects overlapping file changes across branches and calculates risk levels (LOW / MEDIUM / HIGH / CRITICAL).</li>
  <li><strong>Rebase Advisor</strong> — Recommends REBASE, MERGE, or EITHER based on divergence analysis.</li>
  <li><strong>Cleanup Automation</strong> — Batch-delete merged branches with dry-run mode. Protected branches (main, master, develop) are never touched.</li>
  <li><strong>Orphan Commit Detection</strong> — Finds commits not reachable from any branch, so nothing gets lost.</li>
  <li><strong>Enhanced Notifications</strong> — Conflict risk warnings with affected file counts and actionable suggestions.</li>
</ul>

<h3>Free &amp; Open Source</h3>
<p>Every feature is free for everyone — no license, no tiers. Source code: <a href="https://github.com/JirakJ/branch-health-dashboard">GitHub</a> (Apache-2.0).</p>

<h3>Getting Started</h3>
<ol>
  <li><strong>Install</strong> — Search "Branch Health Dashboard" in <em>Settings → Plugins → Marketplace</em> and click Install.</li>
  <li><strong>Open Dashboard</strong> — Click the <em>Branch Health</em> tab in the bottom tool window to see all your branches at a glance.</li>
  <li><strong>Take Action</strong> — Sort by health score to find problem branches, review conflict predictions, and clean up merged branches with one click.</li>
</ol>

<h3>Compatibility</h3>
<p>Works with <strong>IntelliJ IDEA 2024.3+</strong> and all JetBrains IDEs with Git support (WebStorm, PyCharm, GoLand, Rider, PhpStorm, CLion, RubyMine, Android Studio). Cross-platform: macOS, Linux, Windows.</p>

<h3>Part of Developer Productivity Suite</h3>
<p>Branch Health Dashboard is part of a growing suite of JetBrains plugins designed to supercharge your development workflow. Pair it with <strong>Git-Aware Dev Journal</strong>, <strong>Commit Message Copilot</strong>, <strong>Git Workflow Enforcer</strong>, and <strong>Technical Debt Accountant</strong> for a complete productivity toolkit.</p>

## Tags
git, branches, merge-conflicts, cleanup, developer-tools

## Category
Version Control

## Pricing

Free & open source (Apache-2.0) — every feature is available to everyone, no license needed.

## Screenshots Needed
1. **Dashboard Overview** — Branch Health tool window showing a table of branches with color-coded health indicators, scores, and age columns.
2. **Conflict Prediction** — Expanded branch detail showing predicted merge conflicts with risk levels and affected file list.
3. **Activity Heatmap** — Heatmap visualization showing commits-per-day across multiple branches with trend arrows.
4. **Cleanup Automation** — Batch cleanup dialog in dry-run mode showing merged branches selected for deletion with protected branches grayed out.
5. **Rebase Advisor** — Advisory panel recommending REBASE vs. MERGE with divergence metrics and reasoning.
