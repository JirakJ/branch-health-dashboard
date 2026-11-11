package com.jakubjirak.branchhealth.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.ui.dsl.builder.*

class BranchHealthConfigurable : BoundConfigurable("Branch Health Dashboard") {

    override fun createPanel() = panel {
        val settings = BranchHealthSettings.getInstance()

        group("Scanning") {
            row("Stale branch threshold (days):") {
                spinner(1..365)
                    .bindIntValue(settings::staleThresholdDays)
                    .comment("Branches with no commits for this many days are considered stale")
            }
        }

        group("Notifications") {
            row {
                checkBox("Enable notifications")
                    .bindSelected(settings::enableNotifications)
                    .comment("Show a summary and a stale-branch warning after each scan")
            }
        }

        group("Filtering") {
            row("Ignored branches:") {
                expandableTextField()
                    .bindText(settings::ignoredBranches)
                    .comment("Comma-separated branch names excluded from scans and cleanup; matches local and origin/ names")
            }
        }
    }
}
