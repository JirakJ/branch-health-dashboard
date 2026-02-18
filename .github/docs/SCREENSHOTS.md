# Screenshots and Media — Branch Health Dashboard

This document describes the screenshots and video content to produce for the JetBrains Marketplace listing and marketing materials.

---

## Screenshots

### 1. Dashboard Overview
**Filename:** `01-dashboard-overview.png`

The main tool window showing a list of 8–10 branches in a sortable table. Columns visible: Branch Name, Health Score, Age, Last Activity, Status, Behind Count. Several branches display different statuses — two Healthy (green), three Aging (yellow), two Stale (orange), one Dead (red). The table is sorted by health score descending. The IDE sidebar shows the "Branch Health" tool window tab selected.

---

### 2. Health Score Detail
**Filename:** `02-health-score-detail.png`

A detail panel for a single branch (`feature/user-auth`). The health score is displayed prominently as **62/100** with a yellow "Aging" badge. Below it, a breakdown shows each scoring factor: Age −15 pts, Activity −8 pts, Behind count −10 pts, Conflict risk −5 pts. A small bar chart visualizes the score composition. The panel also shows the branch creation date, last commit date, and author.

---

### 3. Activity Heatmap
**Filename:** `03-activity-heatmap.png`

The activity heatmap view for a selected branch. A grid of colored cells represents commits per day over the last 30 days. Darker cells indicate more commits. A trend indicator arrow shows activity is declining. Below the heatmap, a summary line reads: "12 commits in the last 30 days — trending down."

---

### 4. Stale Branch Notification
**Filename:** `04-stale-notification.png`

A balloon notification in the bottom-right corner of the IDE. The notification reads: "Branch `feature/old-search` has been inactive for 34 days and is now Stale. Open Dashboard to review." The notification includes "Open Dashboard" and "Dismiss" action links. Behind the notification, the editor is visible with normal code.

---

### 5. Conflict Prediction (Pro)
**Filename:** `05-conflict-prediction.png`

The conflict prediction panel for a branch (`feature/payments`). A risk badge shows **HIGH**. Below it, a list of 4 conflicting files is displayed with file paths and a short note ("Modified in both branches"). A bar indicates overlap percentage. At the bottom, a recommendation reads: "3 files overlap with `main`. Consider rebasing before this branch falls further behind."

---

### 6. Rebase Advisor (Pro)
**Filename:** `06-rebase-advisor.png`

The rebase advisor panel for a branch (`feature/api-v2`). The recommendation is displayed as a large badge: **REBASE**. Below it, a short explanation reads: "This branch is 12 commits behind `main` with no merge commits. A rebase will produce a clean linear history." Two buttons appear at the bottom: "Rebase Now" and "Dismiss."

---

### 7. Cleanup Automation (Pro)
**Filename:** `07-cleanup-automation.png`

The cleanup dialog showing a dry-run result. A list of 5 branches is displayed, each marked as "Merged" with a checkbox. Branch names include `feature/login-fix`, `bugfix/typo`, and `feature/old-api`. At the top, a note reads: "5 merged branches found. Protected branches (main, master, develop) are excluded." Two buttons: "Delete Selected" and "Cancel." A "Dry Run" toggle is visible and turned on.

---

### 8. Settings Panel
**Filename:** `08-settings-panel.png`

The plugin settings page under **Settings → Tools → Branch Health Dashboard**. Visible options include: Staleness threshold dropdown (set to "30 days"), Notification toggle (enabled), Protected branches list (`main, master, develop`), Health score weighting sliders for Age, Activity, Behind Count, and Conflict Risk. The Pro section is visible at the bottom with a "License Key" field and an "Activate Pro" button.

---

## Video Demo Outline

**Title:** Branch Health Dashboard — Quick Tour
**Duration:** 45–60 seconds
**Format:** Screen recording with captions, no voiceover (or optional voiceover)

| Time        | Scene                          | Description                                                                                          |
|-------------|--------------------------------|------------------------------------------------------------------------------------------------------|
| 0:00–0:05   | Title card                     | Plugin name, tagline, and JetBrains logo.                                                            |
| 0:05–0:15   | Open the dashboard             | Open IntelliJ IDEA with a project. Click the "Branch Health" tab in the sidebar. The dashboard loads with 8 branches showing scores, ages, and statuses. |
| 0:15–0:25   | Sort and filter                | Sort by health score. Filter to show only Stale and Dead branches. Two branches remain visible.       |
| 0:25–0:35   | Health score detail            | Click a Stale branch. The detail panel opens showing the score breakdown: age, activity, behind count. The activity heatmap shows declining commits. |
| 0:35–0:45   | Conflict prediction (Pro)      | Switch to a different branch. The conflict prediction panel shows a HIGH risk with 3 overlapping files. The rebase advisor recommends REBASE. |
| 0:45–0:55   | Cleanup (Pro)                  | Open the cleanup dialog. A dry-run lists 4 merged branches. Select all and click "Delete Selected." A confirmation shows the branches were removed. |
| 0:55–1:00   | Call to action                 | End card: "Install free from the JetBrains Marketplace" with a link and the Pro pricing badge ($6/month). |

**Production notes:**
- Use a realistic sample project with real-looking branch names (e.g., `feature/user-auth`, `bugfix/payment-rounding`, `feature/old-search`).
- Keep the IDE theme consistent (default light or dark theme).
- Use smooth transitions between scenes. No hard cuts.
- Add subtle zoom on key UI elements (health score badge, conflict risk badge, cleanup dialog).
- Captions should be short — one line per scene.
