package com.jakubjirak.branchhealth.analyzer

import com.jakubjirak.branchhealth.model.AheadBehind
import com.jakubjirak.branchhealth.model.RebaseAdvice.Recommendation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RebaseAdvisorAndActivityTest {

    private val advisor = RebaseAdvisor()
    private val activity = ActivityAnalyzer()

    @Test
    fun `rebase advice by divergence`() {
        assertEquals(Recommendation.EITHER, advisor.advise(AheadBehind(ahead = 100, behind = 0)).recommendation)
        assertEquals(Recommendation.REBASE, advisor.advise(AheadBehind(ahead = 10, behind = 20)).recommendation)
        assertEquals(Recommendation.MERGE, advisor.advise(AheadBehind(ahead = 51, behind = 1)).recommendation)
        assertEquals(Recommendation.MERGE, advisor.advise(AheadBehind(ahead = 1, behind = 101)).recommendation)
        assertEquals(Recommendation.EITHER, advisor.advise(AheadBehind(ahead = 30, behind = 30)).recommendation)
    }

    @Test
    fun `behind penalty grows then caps`() {
        assertEquals(0, advisor.calculateBehindPenalty(0))
        assertEquals(0, advisor.calculateBehindPenalty(-3))
        assertEquals(3, advisor.calculateBehindPenalty(10))
        assertEquals(15, advisor.calculateBehindPenalty(50))
        assertEquals(15, advisor.calculateBehindPenalty(500))
    }

    @Test
    fun `activity rate and classification`() {
        assertEquals(0.0, activity.calculateActivityRate(10, 0))
        assertEquals(0.5, activity.calculateActivityRate(15, 30))
        assertEquals(ActivityAnalyzer.ActivityTrend.HIGH, activity.classifyActivity(1.0))
        assertEquals(ActivityAnalyzer.ActivityTrend.MODERATE, activity.classifyActivity(0.3))
        assertEquals(ActivityAnalyzer.ActivityTrend.LOW, activity.classifyActivity(0.05))
        assertEquals(ActivityAnalyzer.ActivityTrend.INACTIVE, activity.classifyActivity(0.0))
    }

    @Test
    fun `activity score per trend`() {
        assertEquals(10, activity.calculateActivityScore(2.0))
        assertEquals(5, activity.calculateActivityScore(0.5))
        assertEquals(0, activity.calculateActivityScore(0.1))
        assertEquals(-10, activity.calculateActivityScore(0.0))
    }
}
