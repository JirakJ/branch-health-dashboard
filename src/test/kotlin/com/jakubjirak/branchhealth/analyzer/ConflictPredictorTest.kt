package com.jakubjirak.branchhealth.analyzer

import com.jakubjirak.branchhealth.model.ConflictRisk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConflictPredictorTest {

    private val predictor = ConflictPredictor()

    @Test
    fun `no conflict when no common files`() {
        val result = predictor.predictConflict(
            branchName = "feature-a",
            branchFiles = setOf("src/A.kt", "src/B.kt"),
            targetBranch = "main",
            targetFiles = setOf("src/C.kt", "src/D.kt"),
        )
        assertNull(result)
    }

    @Test
    fun `conflict detected with common files`() {
        val result = predictor.predictConflict(
            branchName = "feature-a",
            branchFiles = setOf("src/A.kt", "src/B.kt", "src/Common.kt"),
            targetBranch = "main",
            targetFiles = setOf("src/C.kt", "src/Common.kt"),
        )
        assertNotNull(result)
        assertEquals(1, result!!.filePaths.size)
        assertTrue(result.filePaths.contains("src/Common.kt"))
        assertEquals("main", result.conflictingBranch)
    }

    @Test
    fun `risk score is zero for empty file sets`() {
        val score = predictor.calculateRiskScore(0, 0, 0)
        assertEquals(0.0, score)
    }

    @Test
    fun `risk score is 1 when all files are common`() {
        val score = predictor.calculateRiskScore(5, 5, 5)
        assertEquals(1.0, score)
    }

    @Test
    fun `risk score is proportional to common files`() {
        val lowRisk = predictor.calculateRiskScore(1, 10, 10)
        val highRisk = predictor.calculateRiskScore(8, 10, 10)
        assertTrue(lowRisk < highRisk)
    }

    @Test
    fun `conflict penalty is zero for zero risk`() {
        assertEquals(0, predictor.calculateConflictPenalty(0.0))
    }

    @Test
    fun `conflict penalty scales with risk score`() {
        val lowPenalty = predictor.calculateConflictPenalty(0.2)
        val highPenalty = predictor.calculateConflictPenalty(0.8)
        assertTrue(lowPenalty < highPenalty)
    }

    @Test
    fun `multiple common files increase risk`() {
        val result = predictor.predictConflict(
            branchName = "feature-x",
            branchFiles = setOf("src/A.kt", "src/B.kt", "src/C.kt"),
            targetBranch = "main",
            targetFiles = setOf("src/A.kt", "src/B.kt", "src/D.kt"),
        )
        assertNotNull(result)
        assertEquals(2, result!!.filePaths.size)
    }
}
