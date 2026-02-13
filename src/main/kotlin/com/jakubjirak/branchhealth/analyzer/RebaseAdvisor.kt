package com.jakubjirak.branchhealth.analyzer

import com.jakubjirak.branchhealth.model.AheadBehind
import com.jakubjirak.branchhealth.model.RebaseAdvice

/**
 * Analyzes ahead/behind counts and recommends rebase vs merge strategy.
 */
class RebaseAdvisor {

    /**
     * Generate rebase vs merge recommendation based on branch divergence.
     */
    fun advise(aheadBehind: AheadBehind): RebaseAdvice {
        val ahead = aheadBehind.ahead
        val behind = aheadBehind.behind

        return when {
            behind == 0 -> RebaseAdvice(
                recommendation = RebaseAdvice.Recommendation.EITHER,
                reason = "Branch is up to date with the base branch.",
            )
            ahead <= REBASE_AHEAD_THRESHOLD && behind <= REBASE_BEHIND_THRESHOLD -> RebaseAdvice(
                recommendation = RebaseAdvice.Recommendation.REBASE,
                reason = "Small divergence ($ahead ahead, $behind behind). Rebase for a clean history.",
            )
            ahead > MERGE_AHEAD_THRESHOLD || behind > MERGE_BEHIND_THRESHOLD -> RebaseAdvice(
                recommendation = RebaseAdvice.Recommendation.MERGE,
                reason = "Large divergence ($ahead ahead, $behind behind). Merge to preserve history.",
            )
            else -> RebaseAdvice(
                recommendation = RebaseAdvice.Recommendation.EITHER,
                reason = "Moderate divergence ($ahead ahead, $behind behind). Either strategy works.",
            )
        }
    }

    /**
     * Calculate health score penalty based on how far behind the branch is.
     */
    fun calculateBehindPenalty(behind: Int): Int {
        return when {
            behind <= 0 -> 0
            behind <= SMALL_BEHIND -> (behind * PENALTY_PER_SMALL_BEHIND).toInt().coerceAtMost(MAX_BEHIND_PENALTY)
            else -> MAX_BEHIND_PENALTY
        }
    }

    companion object {
        private const val REBASE_AHEAD_THRESHOLD = 10
        private const val REBASE_BEHIND_THRESHOLD = 20
        private const val MERGE_AHEAD_THRESHOLD = 50
        private const val MERGE_BEHIND_THRESHOLD = 100

        private const val SMALL_BEHIND = 50
        private const val PENALTY_PER_SMALL_BEHIND = 0.3
        private const val MAX_BEHIND_PENALTY = 15
    }
}
