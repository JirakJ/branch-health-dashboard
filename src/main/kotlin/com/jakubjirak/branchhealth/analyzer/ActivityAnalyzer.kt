package com.jakubjirak.branchhealth.analyzer

/**
 * Analyzes commit activity patterns for a branch.
 */
class ActivityAnalyzer {    /**
     * Calculate activity rate as commits per day over a given period.
     */
    fun calculateActivityRate(commitCount: Int, periodDays: Long): Double {
        if (periodDays <= 0) return 0.0
        return commitCount.toDouble() / periodDays
    }

    /**
     * Classify the activity trend based on commit rate.
     */
    fun classifyActivity(activityRate: Double): ActivityTrend {
        return when {
            activityRate >= HIGH_ACTIVITY_THRESHOLD -> ActivityTrend.HIGH
            activityRate >= MODERATE_ACTIVITY_THRESHOLD -> ActivityTrend.MODERATE
            activityRate >= LOW_ACTIVITY_THRESHOLD -> ActivityTrend.LOW
            else -> ActivityTrend.INACTIVE
        }
    }

    /**
     * Calculate a health score bonus/penalty based on activity.
     * Active branches get a bonus, inactive branches get a penalty.
     */
    fun calculateActivityScore(activityRate: Double): Int {
        return when (classifyActivity(activityRate)) {
            ActivityTrend.HIGH -> ACTIVITY_BONUS_HIGH
            ActivityTrend.MODERATE -> ACTIVITY_BONUS_MODERATE
            ActivityTrend.LOW -> 0
            ActivityTrend.INACTIVE -> ACTIVITY_PENALTY_INACTIVE
        }
    }

    enum class ActivityTrend {
        HIGH,
        MODERATE,
        LOW,
        INACTIVE,
    }

    companion object {
        private const val HIGH_ACTIVITY_THRESHOLD = 1.0
        private const val MODERATE_ACTIVITY_THRESHOLD = 0.3
        private const val LOW_ACTIVITY_THRESHOLD = 0.05

        private const val ACTIVITY_BONUS_HIGH = 10
        private const val ACTIVITY_BONUS_MODERATE = 5
        private const val ACTIVITY_PENALTY_INACTIVE = -10
    }
}
