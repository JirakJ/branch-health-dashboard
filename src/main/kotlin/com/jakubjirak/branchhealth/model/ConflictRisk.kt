package com.jakubjirak.branchhealth.model

/**
 * Merge conflict risk assessment between two branches.
 */
data class ConflictRisk(
    val filePaths: List<String>,
    val riskScore: Double,
    val conflictingBranch: String,
)
