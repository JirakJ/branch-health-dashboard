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
            row("Scan interval (minutes):") {
                spinner(5..1440)
                    .bindIntValue(settings::scanIntervalMinutes)
                    .comment("How often the plugin checks repository branches for staleness")
            }
        }

        group("Notifications") {
            row {
                checkBox("Enable notifications")
                    .bindSelected(settings::enableNotifications)
                    .comment("Show notifications when stale or unhealthy branches are detected")
            }
        }

        group("Filtering") {
            row("Ignored branches:") {
                expandableTextField()
                    .bindText(settings::ignoredBranches)
                    .comment("Comma-separated list of branch names to exclude from health checks (e.g. main, develop, master)")
            }
        }
    }
}
