package com.jakubjirak.branchhealth.analyzer

import com.jakubjirak.branchhealth.model.HealthLevel
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Analyzes branch age and detects stale branches based on configurable thresholds.
 */
class BranchAgeAnalyzer(
    private val staleThresholdDays: Long = DEFAULT_STALE_THRESHOLD_DAYS,
) {    /**
     * Calculate the age of a branch in days based on its last commit date.
     */
    fun calculateAgeDays(lastCommitDate: Instant): Long {
        return ChronoUnit.DAYS.between(lastCommitDate, Instant.now())
    }

    /**
     * Determine if a branch is stale based on its last commit date.
     */
    fun isStale(lastCommitDate: Instant): Boolean {
        return calculateAgeDays(lastCommitDate) > staleThresholdDays
    }

    /**
     * Classify the health level of a branch based on its age.
     */
    fun classifyByAge(ageDays: Long): HealthLevel {
        return when {
            ageDays <= HEALTHY_THRESHOLD_DAYS -> HealthLevel.HEALTHY
            ageDays <= AGING_THRESHOLD_DAYS -> HealthLevel.AGING
            ageDays <= staleThresholdDays -> HealthLevel.STALE
            else -> HealthLevel.DEAD
        }
    }

    /**
     * Calculate a health score penalty based on branch age (0 = no penalty, up to 40 penalty points).
     */
    fun calculateAgePenalty(ageDays: Long): Int {
        return when {
            ageDays <= HEALTHY_THRESHOLD_DAYS -> 0
            ageDays <= AGING_THRESHOLD_DAYS -> ((ageDays - HEALTHY_THRESHOLD_DAYS) * MAX_AGING_PENALTY /
                (AGING_THRESHOLD_DAYS - HEALTHY_THRESHOLD_DAYS)).toInt()
            ageDays <= staleThresholdDays -> MAX_AGING_PENALTY + ((ageDays - AGING_THRESHOLD_DAYS) * MAX_STALE_PENALTY /
                (staleThresholdDays - AGING_THRESHOLD_DAYS)).toInt()
            else -> MAX_AGING_PENALTY + MAX_STALE_PENALTY
        }
    }

    companion object {
        const val DEFAULT_STALE_THRESHOLD_DAYS = 30L
        private const val HEALTHY_THRESHOLD_DAYS = 7L
        private const val AGING_THRESHOLD_DAYS = 14L
        private const val MAX_AGING_PENALTY = 15
        private const val MAX_STALE_PENALTY = 25
    }
}
