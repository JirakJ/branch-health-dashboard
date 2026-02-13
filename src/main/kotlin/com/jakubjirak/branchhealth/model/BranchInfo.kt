package com.jakubjirak.branchhealth.model

import java.time.Instant

/**
 * Basic information about a Git branch.
 */
data class BranchInfo(
    val name: String,
    val lastCommitDate: Instant,
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
