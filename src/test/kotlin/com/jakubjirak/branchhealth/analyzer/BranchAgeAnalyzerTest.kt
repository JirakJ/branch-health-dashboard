package com.jakubjirak.branchhealth.analyzer

import com.jakubjirak.branchhealth.model.HealthLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class BranchAgeAnalyzerTest {

    private val analyzer = BranchAgeAnalyzer()

    @Test
    fun `fresh branch is not stale`() {
        val lastCommit = Instant.now().minus(1, ChronoUnit.DAYS)
        assertFalse(analyzer.isStale(lastCommit))
    }

    @Test
    fun `old branch is stale`() {
        val lastCommit = Instant.now().minus(60, ChronoUnit.DAYS)
        assertTrue(analyzer.isStale(lastCommit))
    }

    @Test
    fun `branch at threshold is not stale`() {
        val lastCommit = Instant.now().minus(30, ChronoUnit.DAYS)
        assertFalse(analyzer.isStale(lastCommit))
    }

    @Test
    fun `branch just over threshold is stale`() {
        val lastCommit = Instant.now().minus(31, ChronoUnit.DAYS)
        assertTrue(analyzer.isStale(lastCommit))
    }

    @Test
    fun `custom threshold respected`() {
        val customAnalyzer = BranchAgeAnalyzer(staleThresholdDays = 7)
        val lastCommit = Instant.now().minus(10, ChronoUnit.DAYS)
        assertTrue(customAnalyzer.isStale(lastCommit))
    }

    @Test
    fun `classify fresh branch as healthy`() {
        assertEquals(HealthLevel.HEALTHY, analyzer.classifyByAge(3))
    }

    @Test
    fun `classify week-old branch as healthy`() {
        assertEquals(HealthLevel.HEALTHY, analyzer.classifyByAge(7))
    }

    @Test
    fun `classify two-week branch as aging`() {
        assertEquals(HealthLevel.AGING, analyzer.classifyByAge(10))
    }

    @Test
    fun `classify month-old branch as stale`() {
        assertEquals(HealthLevel.STALE, analyzer.classifyByAge(25))
    }

    @Test
    fun `classify very old branch as dead`() {
        assertEquals(HealthLevel.DEAD, analyzer.classifyByAge(60))
    }

    @Test
    fun `no age penalty for fresh branch`() {
        assertEquals(0, analyzer.calculateAgePenalty(3))
    }

    @Test
    fun `age penalty increases with age`() {
        val penaltyWeekOld = analyzer.calculateAgePenalty(7)
        val penaltyTwoWeeks = analyzer.calculateAgePenalty(14)
        val penaltyMonth = analyzer.calculateAgePenalty(30)

        assertTrue(penaltyWeekOld <= penaltyTwoWeeks)
        assertTrue(penaltyTwoWeeks <= penaltyMonth)
    }

    @Test
    fun `calculate age in days`() {
        val threeDaysAgo = Instant.now().minus(3, ChronoUnit.DAYS)
        assertEquals(3, analyzer.calculateAgeDays(threeDaysAgo))
    }
}
