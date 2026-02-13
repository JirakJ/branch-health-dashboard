package com.jakubjirak.branchhealth.model

/**
 * Complete health assessment for a Git branch.
 */
data class BranchHealth(
    val branch: BranchInfo,
    val healthScore: Int,
    val level: HealthLevel,
    val issues: List<String>,
    val ageDays: Long,
    val activityRate: Double,
    val conflictRisk: ConflictRisk?,
    val rebaseAdvice: RebaseAdvice?,
)
