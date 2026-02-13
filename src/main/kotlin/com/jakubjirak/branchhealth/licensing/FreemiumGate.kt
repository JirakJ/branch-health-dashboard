package com.jakubjirak.branchhealth.licensing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

/**
 * Central gate for freemium feature access.
 * Free tier: basic health metrics, max 5 branches.
 * Paid tier: unlimited branches, conflict prediction, cleanup automation, rebase advisor.
 */
@Service(Service.Level.APP)
class FreemiumGate {

    enum class LicenseStatus {
        FREE, TRIAL, PAID, EXPIRED
    }

    /**
     * Check if a paid feature is available for the current license.
     */
    @Suppress("UNUSED_PARAMETER")
    fun isPaidFeatureAvailable(featureId: String): Boolean {
        val status = getLicenseStatus()
        return status == LicenseStatus.PAID || status == LicenseStatus.TRIAL
    }

    fun getLicenseStatus(): LicenseStatus {
        // TODO: Integrate with JetBrains Marketplace Licensing API
        return LicenseStatus.FREE
    }

    fun getMaxBranches(): Int {
        return when (getLicenseStatus()) {
            LicenseStatus.FREE -> MAX_FREE_BRANCHES
            LicenseStatus.EXPIRED -> MAX_FREE_BRANCHES
            LicenseStatus.TRIAL, LicenseStatus.PAID -> Int.MAX_VALUE
        }
    }

    companion object {
        const val MAX_FREE_BRANCHES = 5

        // Feature IDs
        const val FEATURE_CONFLICT_PREDICTION = "conflict_prediction"
        const val FEATURE_REBASE_ADVISOR = "rebase_advisor"
        const val FEATURE_CLEANUP_AUTOMATION = "cleanup_automation"
        const val FEATURE_UNLIMITED_BRANCHES = "unlimited_branches"
        const val FEATURE_NOTIFICATIONS = "notifications"

        fun getInstance(): FreemiumGate =
            ApplicationManager.getApplication().getService(FreemiumGate::class.java)
    }
}
