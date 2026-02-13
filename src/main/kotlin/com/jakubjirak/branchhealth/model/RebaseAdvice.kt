package com.jakubjirak.branchhealth.model

/**
 * Rebase vs merge recommendation for a branch.
 */
data class RebaseAdvice(
    val recommendation: Recommendation,
    val reason: String,
) {
    enum class Recommendation {
        REBASE,
        MERGE,
        EITHER,
    }
}
