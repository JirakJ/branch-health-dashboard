package com.jakubjirak.branchhealth.model

import java.time.Instant

/**
 * Basic information about a Git branch.
 */
data class BranchInfo(
    /** Local name (`feature/x`) or remote-tracking name (`origin/feature/x`). */
    val name: String,
    val lastCommitDate: Instant,
    /** Commits reachable from the branch within the engine's activity window (last 30 days). */
    val commitCount: Int,
    val author: String,
    val isRemote: Boolean,
    val isStale: Boolean,
    val aheadBehind: AheadBehind,
)

/**
 * Ahead/behind counts relative to the default branch.
 */
data class AheadBehind(
    val ahead: Int,
    val behind: Int,
)
