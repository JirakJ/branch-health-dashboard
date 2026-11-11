package com.jakubjirak.branchhealth.engine

import com.jakubjirak.branchhealth.model.AheadBehind
import com.jakubjirak.branchhealth.model.BranchInfo
import com.jakubjirak.branchhealth.model.HealthLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class BranchHealthEngineTest {

    private val engine = BranchHealthEngine()

    private fun createBranch(
        name: String = "feature/test",
        daysAgo: Long = 1,
        commitCount: Int = 10,
        ahead: Int = 0,
        behind: Int = 0,
    ): BranchInfo {
        return BranchInfo(
            name = name,
            lastCommitDate = Instant.now().minus(daysAgo, ChronoUnit.DAYS),
            commitCount = commitCount,
            author = "test-author",
            isRemote = false,
            isStale = daysAgo > 30,
            aheadBehind = AheadBehind(ahead, behind),
        )
    }

    @Test
    fun `fresh active branch gets high health score`() {
        val branch = createBranch(daysAgo = 1, commitCount = 10)
        val health = engine.analyzeBranch(branch)

        assertTrue(health.healthScore >= 75)
        assertEquals(HealthLevel.HEALTHY, health.level)
    }

    @Test
    fun `stale branch gets low health score`() {
        val branch = createBranch(daysAgo = 60, commitCount = 1)
        val health = engine.analyzeBranch(branch)

        assertTrue(health.healthScore < 75)
        assertTrue(health.issues.isNotEmpty())
    }

    @Test
    fun `dead branch has lowest health`() {
        val branch = createBranch(daysAgo = 120, commitCount = 0)
        val health = engine.analyzeBranch(branch)

        assertTrue(health.healthScore <= 50)
    }

    @Test
    fun `behind branch gets penalty`() {
        val upToDate = createBranch(daysAgo = 5, commitCount = 2, behind = 0)
        val behind = createBranch(daysAgo = 5, commitCount = 2, behind = 50)

        val healthUpToDate = engine.analyzeBranch(upToDate)
        val healthBehind = engine.analyzeBranch(behind)

        assertTrue(healthUpToDate.healthScore > healthBehind.healthScore)
    }

    @Test
    fun `issues reported for stale branch`() {
        val branch = createBranch(daysAgo = 60, commitCount = 0)
        val health = engine.analyzeBranch(branch)

        assertTrue(health.issues.any { it.contains("stale") })
    }

    @Test
    fun `issues reported for behind branch`() {
        val branch = createBranch(daysAgo = 1, commitCount = 5, behind = 20)
        val health = engine.analyzeBranch(branch)

        assertTrue(health.issues.any { it.contains("behind") })
    }

    @Test
    fun `health score is clamped between 0 and 100`() {
        val veryOld = createBranch(daysAgo = 365, commitCount = 0, behind = 200)
        val health = engine.analyzeBranch(veryOld)

        assertTrue(health.healthScore in 0..100)
    }

    @Test
    fun `cache stores results`() {
        val branch = createBranch(name = "feature/cached")
        engine.analyzeBranch(branch)

        val cached = engine.getCached("feature/cached")
        assertTrue(cached != null)
        assertEquals("feature/cached", cached!!.branch.name)
    }

    @Test
    fun `clear cache removes results`() {
        val branch = createBranch(name = "feature/temp")
        engine.analyzeBranch(branch)
        engine.clearCache()

        val cached = engine.getCached("feature/temp")
        assertTrue(cached == null)
    }

    @Test
    fun `age days calculated correctly`() {
        val branch = createBranch(daysAgo = 15)
        val health = engine.analyzeBranch(branch)

        assertEquals(15, health.ageDays)
    }
}

class BranchHealthEngineConflictTest {

    private val engine = BranchHealthEngine()

    private fun branch() = BranchInfo(
        name = "feature/x",
        lastCommitDate = Instant.now(),
        commitCount = 30,
        author = "a",
        isRemote = false,
        isStale = false,
        aheadBehind = AheadBehind(0, 0),
    )

    @Test
    fun `overlapping changes produce conflict risk, penalty and issue`() {
        val health = engine.analyzeBranch(branch(), setOf("a.kt", "b.kt"), "main", setOf("a.kt", "b.kt"))
        assertEquals(1.0, health.conflictRisk!!.riskScore)
        assertEquals("main", health.conflictRisk!!.conflictingBranch)
        // 100 + 10 (30 commits / 30 days = HIGH) - 20 conflict = 90 -> clamped 90
        assertEquals(90, health.healthScore)
        assertTrue(health.issues.any { it.contains("conflicts with main") })
    }

    @Test
    fun `disjoint changes have no conflict risk`() {
        val health = engine.analyzeBranch(branch(), setOf("a.kt"), "main", setOf("b.kt"))
        assertEquals(null, health.conflictRisk)
    }

    @Test
    fun `activity is measured over the fixed window`() {
        val health = engine.analyzeBranch(branch().copy(commitCount = 15))
        assertEquals(0.5, health.activityRate)
    }

    @Test
    fun `future commit dates do not produce negative age`() {
        val health = engine.analyzeBranch(branch().copy(lastCommitDate = Instant.now().plus(3, ChronoUnit.DAYS)))
        assertEquals(0, health.ageDays)
    }

    @Test
    fun `isStale delegates to the configured threshold`() {
        val strict = BranchHealthEngine(staleThresholdDays = 5)
        assertTrue(strict.isStale(Instant.now().minus(6, ChronoUnit.DAYS)))
        assertTrue(!strict.isStale(Instant.now().minus(4, ChronoUnit.DAYS)))
    }
}
