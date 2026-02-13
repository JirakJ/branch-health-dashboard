package com.jakubjirak.branchhealth.licensing

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FreemiumGateTest {

    @Test
    fun `free tier has branch limit`() {
        assertEquals(5, FreemiumGate.MAX_FREE_BRANCHES)
    }

    @Test
    fun `feature IDs are defined`() {
        assertEquals("conflict_prediction", FreemiumGate.FEATURE_CONFLICT_PREDICTION)
        assertEquals("rebase_advisor", FreemiumGate.FEATURE_REBASE_ADVISOR)
        assertEquals("cleanup_automation", FreemiumGate.FEATURE_CLEANUP_AUTOMATION)
        assertEquals("unlimited_branches", FreemiumGate.FEATURE_UNLIMITED_BRANCHES)
        assertEquals("notifications", FreemiumGate.FEATURE_NOTIFICATIONS)
    }
}
