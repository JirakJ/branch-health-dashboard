package com.jakubjirak.branchhealth.notifications

import com.jakubjirak.branchhealth.model.BranchHealth
import com.jakubjirak.branchhealth.model.HealthLevel
import com.jakubjirak.branchhealth.settings.BranchHealthSettings
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

/**
 * Sends notifications about stale branches, conflict risks, and scan results.
 */
class BranchHealthNotifier(private val project: Project) {

    fun notifyStaleBranches(staleBranches: List<BranchHealth>, thresholdDays: Int) {
        if (staleBranches.isEmpty()) return
        if (!BranchHealthSettings.getInstance().enableNotifications) return

        try {
            val branchNames = staleBranches.joinToString(", ") { it.branch.name }
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Branch Health Dashboard")
                .createNotification(
                    "Stale Branches Detected",
                    "${staleBranches.size} branch(es) have not been updated in over $thresholdDays days: $branchNames",
                    NotificationType.WARNING,
                )
                .notify(project)
        } catch (e: Exception) {
            LOG.warn("Failed to send stale branch notification", e)
        }
    }

    fun notifyConflictRisk(branchHealth: BranchHealth) {
        val conflictRisk = branchHealth.conflictRisk ?: return
        if (!BranchHealthSettings.getInstance().enableNotifications) return

        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Branch Health Dashboard")
                .createNotification(
                    "Conflict Risk Detected",
                    "Branch \"${branchHealth.branch.name}\" has potential merge conflicts with " +
                        "\"${conflictRisk.conflictingBranch}\" (${conflictRisk.filePaths.size} common files).",
                    NotificationType.WARNING,
                )
                .notify(project)
        } catch (e: Exception) {
            LOG.warn("Failed to send conflict risk notification", e)
        }
    }

    fun notifyScanComplete(results: List<BranchHealth>) {
        if (!BranchHealthSettings.getInstance().enableNotifications) return

        val healthy = results.count { it.level == HealthLevel.HEALTHY }
        val aging = results.count { it.level == HealthLevel.AGING }
        val stale = results.count { it.level == HealthLevel.STALE }
        val dead = results.count { it.level == HealthLevel.DEAD }

        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Branch Health Dashboard")
                .createNotification(
                    "Branch Health Scan Complete",
                    "Scanned ${results.size} branches. $healthy healthy, $aging aging, $stale stale, $dead dead.",
                    NotificationType.INFORMATION,
                )
                .notify(project)
        } catch (e: Exception) {
            LOG.warn("Failed to send scan complete notification", e)
        }
    }

    companion object {
        private val LOG = Logger.getInstance(BranchHealthNotifier::class.java)
    }
}
