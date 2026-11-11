package com.jakubjirak.branchhealth.ui

import com.jakubjirak.branchhealth.model.AheadBehind
import com.jakubjirak.branchhealth.model.BranchHealth
import com.jakubjirak.branchhealth.model.BranchInfo
import com.jakubjirak.branchhealth.model.ConflictRisk
import com.jakubjirak.branchhealth.model.HealthLevel
import com.jakubjirak.branchhealth.model.RebaseAdvice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class BranchHealthRowsTest {

    private fun health(name: String, level: HealthLevel, risk: ConflictRisk? = null) = BranchHealth(
        branch = BranchInfo(name, Instant.EPOCH, 3, "a", false, false, AheadBehind(ahead = 4, behind = 2)),
        healthScore = 80,
        level = level,
        issues = emptyList(),
        ageDays = 12,
        activityRate = 0.3333,
        conflictRisk = risk,
        rebaseAdvice = RebaseAdvice(RebaseAdvice.Recommendation.REBASE, "r"),
    )

    @Test
    fun `columns and classes line up`() {
        assertEquals(BranchHealthRows.COLUMNS.size, BranchHealthRows.COLUMN_CLASSES.size)
        val row = BranchHealthRows.toRow(health("f", HealthLevel.HEALTHY))
        assertEquals(BranchHealthRows.COLUMNS.size, row.size)
        row.forEachIndexed { i, v -> assertEquals(BranchHealthRows.COLUMN_CLASSES[i], v.javaClass, "column $i") }
    }

    @Test
    fun `row values are typed for sorting and formatted for reading`() {
        val row = BranchHealthRows.toRow(health("f", HealthLevel.AGING, ConflictRisk(listOf("a", "b"), 0.25, "main")))
        assertEquals("f", row[0])
        assertEquals("🟡 Aging", row[1])
        assertEquals(80, row[2])
        assertEquals(12L, row[3])
        assertEquals(0.33, row[4])
        assertEquals("+4 / -2", row[5])
        assertEquals("25% (2 files)", row[6])
        assertEquals("REBASE", row[7])
    }

    @Test
    fun `no conflict risk reads None`() {
        assertEquals("None", BranchHealthRows.toRow(health("f", HealthLevel.HEALTHY))[6])
    }

    @Test
    fun `filter by status display name`() {
        val all = listOf(health("a", HealthLevel.HEALTHY), health("b", HealthLevel.DEAD))
        assertEquals(all, BranchHealthRows.filter(all, "All"))
        assertEquals(all, BranchHealthRows.filter(all, null))
        assertEquals(listOf("b"), BranchHealthRows.filter(all, "Dead").map { it.branch.name })
        assertEquals(listOf("All", "Healthy", "Aging", "Stale", "Dead"), BranchHealthRows.FILTERS.toList())
    }
}
