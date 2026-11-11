package com.jakubjirak.branchhealth.settings

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BranchHealthSettingsTest {

    @Test
    fun `defaults`() {
        val s = BranchHealthSettings()
        assertEquals(30, s.staleThresholdDays)
        assertTrue(s.enableNotifications)
        assertEquals(listOf("main", "develop", "master"), s.getIgnoredBranchList())
    }

    @Test
    fun `ignored list trims and drops blanks`() {
        val s = BranchHealthSettings().apply { ignoredBranches = " release , ,hotfix/x," }
        assertEquals(listOf("release", "hotfix/x"), s.getIgnoredBranchList())
        assertEquals(emptyList<String>(), BranchHealthSettings().apply { ignoredBranches = "" }.getIgnoredBranchList())
    }
}
