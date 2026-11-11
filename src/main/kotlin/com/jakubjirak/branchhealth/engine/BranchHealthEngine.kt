package com.jakubjirak.branchhealth.engine

import com.jakubjirak.branchhealth.analyzer.ActivityAnalyzer
import com.jakubjirak.branchhealth.analyzer.BranchAgeAnalyzer
import com.jakubjirak.branchhealth.analyzer.ConflictPredictor
import com.jakubjirak.branchhealth.analyzer.RebaseAdvisor
import com.jakubjirak.branchhealth.model.AheadBehind
import com.jakubjirak.branchhealth.model.BranchHealth
import com.jakubjirak.branchhealth.model.BranchInfo
import com.jakubjirak.branchhealth.model.HealthLevel
import com.intellij.openapi.diagnostic.Logger

/**
 * Orchestrates all analyzers to produce comprehensive branch health assessments.
 * Caches results for efficient lookups.
 */
class BranchHealthEngine(
    private val staleThresholdDays: Long = BranchAgeAnalyzer.DEFAULT_STALE_THRESHOLD_DAYS,
) {

    private val ageAnalyzer = BranchAgeAnalyzer(staleThresholdDays)
    private val activityAnalyzer = ActivityAnalyzer()
    private val conflictPredictor = ConflictPredictor()
    private val rebaseAdvisor = RebaseAdvisor()

    private val cache = java.util.concurrent.ConcurrentHashMap<String, BranchHealth>()

    /**
     * Analyze a single branch and produce a health assessment.
     */
    fun analyzeBranch(
        branch: BranchInfo,
        changedFiles: Set<String> = emptySet(),
        targetBranch: String = "main",
        targetFiles: Set<String> = emptySet(),
    ): BranchHealth {
        val ageDays = ageAnalyzer.calculateAgeDays(branch.lastCommitDate).coerceAtLeast(0)
        val activityRate = activityAnalyzer.calculateActivityRate(branch.commitCount, ACTIVITY_WINDOW_DAYS)
        val issues = mutableListOf<String>()

        // Age penalty
        val agePenalty = ageAnalyzer.calculateAgePenalty(ageDays)
        if (ageAnalyzer.isStale(branch.lastCommitDate)) {
            issues.add("Branch is stale (${ageDays} days since last commit)")
        }

        // Activity score
        val activityScore = activityAnalyzer.calculateActivityScore(activityRate)
        if (activityAnalyzer.classifyActivity(activityRate) == ActivityAnalyzer.ActivityTrend.INACTIVE) {
            issues.add("No recent activity detected")
        }

        // Behind penalty
        val behindPenalty = rebaseAdvisor.calculateBehindPenalty(branch.aheadBehind.behind)
        if (branch.aheadBehind.behind > 0) {
            issues.add("Branch is ${branch.aheadBehind.behind} commits behind $targetBranch")
        }

        // Conflict risk
        val conflictRisk = if (changedFiles.isNotEmpty()) {
            val risk = conflictPredictor.predictConflict(branch.name, changedFiles, targetBranch, targetFiles)
            if (risk != null && risk.riskScore > CONFLICT_ISSUE_THRESHOLD) {
                issues.add("Potential conflicts with ${risk.conflictingBranch} (${risk.filePaths.size} files)")
            }
            risk
        } else {
            null
        }

        val conflictPenalty = conflictRisk?.let { conflictPredictor.calculateConflictPenalty(it.riskScore) } ?: 0

        // Rebase advice
        val rebaseAdvice = rebaseAdvisor.advise(branch.aheadBehind)

        // Calculate overall health score (start at 100, subtract penalties, add bonuses)
        val healthScore = (BASE_SCORE - agePenalty + activityScore - behindPenalty - conflictPenalty)
            .coerceIn(MIN_SCORE, MAX_SCORE)

        val level = classifyHealthScore(healthScore)

        val health = BranchHealth(
            branch = branch,
            healthScore = healthScore,
            level = level,
            issues = issues,
            ageDays = ageDays,
            activityRate = activityRate,
            conflictRisk = conflictRisk,
            rebaseAdvice = rebaseAdvice,
        )

        cache[branch.name] = health
        return health
    }

    /**
     * Analyze multiple branches.
     */
    fun analyzeAll(
        branches: List<BranchInfo>,
        changedFilesMap: Map<String, Set<String>> = emptyMap(),
        targetBranch: String = "main",
        targetFiles: Set<String> = emptySet(),
    ): List<BranchHealth> {
        LOG.info("Analyzing ${branches.size} branches")

        return branches.map { branch ->
            analyzeBranch(
                branch = branch,
                changedFiles = changedFilesMap[branch.name] ?: emptySet(),
                targetBranch = targetBranch,
                targetFiles = targetFiles,
            )
        }
    }

    /** Whether a branch whose last commit is [lastCommitDate] exceeds the stale threshold. */
    fun isStale(lastCommitDate: java.time.Instant): Boolean = ageAnalyzer.isStale(lastCommitDate)

    /**
     * Get cached result for a branch.
     */
    fun getCached(branchName: String): BranchHealth? = cache[branchName]

    /**
     * Clear the cache.
     */
    fun clearCache() {
        cache.clear()
    }

    private fun classifyHealthScore(score: Int): HealthLevel {
        return when {
            score >= HEALTHY_THRESHOLD -> HealthLevel.HEALTHY
            score >= AGING_THRESHOLD -> HealthLevel.AGING
            score >= STALE_THRESHOLD -> HealthLevel.STALE
            else -> HealthLevel.DEAD
        }
    }

    companion object {
        private val LOG = Logger.getInstance(BranchHealthEngine::class.java)

        /** Activity is measured as commits per day over this many trailing days. */
        const val ACTIVITY_WINDOW_DAYS = 30L

        private const val CONFLICT_ISSUE_THRESHOLD = 0.3
        private const val BASE_SCORE = 100
        private const val MIN_SCORE = 0
        private const val MAX_SCORE = 100

        private const val HEALTHY_THRESHOLD = 75
        private const val AGING_THRESHOLD = 50
        private const val STALE_THRESHOLD = 25
    }
}
