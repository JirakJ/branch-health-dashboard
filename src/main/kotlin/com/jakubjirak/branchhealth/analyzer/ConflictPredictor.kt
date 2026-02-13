package com.jakubjirak.branchhealth.analyzer

import com.jakubjirak.branchhealth.model.ConflictRisk

/**
 * Predicts merge conflict probability by finding common changed files across branches.
 */
class ConflictPredictor {

    /**
     * Estimate conflict risk between two branches based on their changed file sets.
     */
    @Suppress("UNUSED_PARAMETER")
    fun predictConflict(
        branchName: String,
        branchFiles: Set<String>,
        targetBranch: String,
        targetFiles: Set<String>,
    ): ConflictRisk? {
        val commonFiles = branchFiles.intersect(targetFiles)
        if (commonFiles.isEmpty()) return null

        val riskScore = calculateRiskScore(commonFiles.size, branchFiles.size, targetFiles.size)

        return ConflictRisk(
            filePaths = commonFiles.toList(),
            riskScore = riskScore,
            conflictingBranch = targetBranch,
        )
    }

    /**
     * Calculate risk score (0.0 - 1.0) based on the ratio of common files.
     */
    fun calculateRiskScore(commonFileCount: Int, branchFileCount: Int, targetFileCount: Int): Double {
        if (branchFileCount == 0 && targetFileCount == 0) return 0.0
        val totalUniqueFiles = branchFileCount + targetFileCount - commonFileCount
        if (totalUniqueFiles == 0) return 0.0
        return (commonFileCount.toDouble() / totalUniqueFiles).coerceIn(0.0, 1.0)
    }

    /**
     * Calculate health score penalty based on conflict risk (0 to 20 penalty points).
     */
    fun calculateConflictPenalty(riskScore: Double): Int {
        return (riskScore * MAX_CONFLICT_PENALTY).toInt()
    }

    companion object {
        private const val MAX_CONFLICT_PENALTY = 20
    }
}
