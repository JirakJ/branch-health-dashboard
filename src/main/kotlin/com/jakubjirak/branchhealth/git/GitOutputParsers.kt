package com.jakubjirak.branchhealth.git

import com.jakubjirak.branchhealth.model.AheadBehind
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

/** A branch ref as listed by `git branch --all --format=...`. */
data class BranchRef(
    /** Display name: `feature/x` for local branches, `origin/feature/x` for remote-tracking ones. */
    val name: String,
    /** Fully qualified ref, e.g. `refs/heads/feature/x`; used as the git revision. */
    val ref: String,
    val isRemote: Boolean,
    val lastCommitDate: Instant,
    val author: String,
)

/**
 * Pure parsers for the git command output used by the plugin. Kept free of IDE APIs so they can be unit-tested.
 */
object GitOutputParsers {

    /** `git branch --format` understood by [parseBranchRef]. */
    const val BRANCH_FORMAT = "%(refname)|%(committerdate:iso-strict)|%(authorname)"

    /** Branches that are never offered for deletion, regardless of settings. */
    val PROTECTED_BRANCHES = setOf("main", "master", "develop")

    private const val LOCAL_PREFIX = "refs/heads/"
    private const val REMOTE_PREFIX = "refs/remotes/"
    private const val FIELD_COUNT = 3

    /**
     * Parses one line of `git branch --all --format=BRANCH_FORMAT`.
     * Returns `null` for symbolic `HEAD` refs, ignored branches, and malformed lines or dates.
     */
    fun parseBranchRef(line: String, ignoredBranches: List<String>): BranchRef? {
        val parts = line.trim().split("|", limit = FIELD_COUNT)
        if (parts.size < FIELD_COUNT) return null
        val ref = parts[0].trim()
        val (name, isRemote) = splitRef(ref) ?: return null
        val skip = name.isEmpty() || name == "HEAD" || name.endsWith("/HEAD") || isIgnored(name, isRemote, ignoredBranches)
        val date = if (skip) null else parseDate(parts[1].trim())
        return date?.let { BranchRef(name, ref, isRemote, it, parts[2].trim()) }
    }

    private fun splitRef(ref: String): Pair<String, Boolean>? = when {
        ref.startsWith(LOCAL_PREFIX) -> ref.removePrefix(LOCAL_PREFIX) to false
        ref.startsWith(REMOTE_PREFIX) -> ref.removePrefix(REMOTE_PREFIX) to true
        else -> null
    }

    /** A branch is ignored when its name, or for remote branches its name without the remote, matches the list. */
    fun isIgnored(name: String, isRemote: Boolean, ignoredBranches: List<String>): Boolean {
        val shortName = if (isRemote) name.substringAfter('/') else name
        return ignoredBranches.any { it.equals(name, ignoreCase = true) || it.equals(shortName, ignoreCase = true) }
    }

    /** Parses ISO-8601 dates with offset (`%(committerdate:iso-strict)`), `null` if malformed. */
    fun parseDate(raw: String): Instant? = try {
        OffsetDateTime.parse(raw).toInstant()
    } catch (_: DateTimeParseException) {
        null
    }

    /**
     * Parses `git rev-list --left-right --count <base>...<branch>`: "<behind>\t<ahead>".
     */
    fun parseLeftRightCount(output: String): AheadBehind? {
        val numbers = output.trim().split(Regex("\\s+"))
        if (numbers.size != 2) return null
        val behind = numbers[0].toIntOrNull() ?: return null
        val ahead = numbers[1].toIntOrNull() ?: return null
        return AheadBehind(ahead = ahead, behind = behind)
    }

    /** Parses `git rev-list --count ...` output, `0` when unparseable. */
    fun parseCount(output: String): Int = output.trim().toIntOrNull() ?: 0

    /** Parses `git diff --name-only` output into a set of paths. */
    fun parseFileList(lines: List<String>): Set<String> = lines.map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    /**
     * Parses `git branch --merged HEAD` output into deletable branch names: skips the current branch (`*`),
     * branches checked out in other worktrees (`+`), detached-HEAD lines and [protected] names.
     */
    fun parseMergedBranches(lines: List<String>, protected: Set<String>): List<String> =
        lines.map { it.trimEnd() }
            .filter { it.isNotBlank() && !it.startsWith("*") && !it.startsWith("+") }
            .map { it.trim() }
            .filter { !it.startsWith("(") }
            .filter { name -> protected.none { it.equals(name, ignoreCase = true) } }

    /**
     * Picks the base branch to compare against: local `main`, `master`, `develop` (in that order),
     * otherwise the same names on `origin`. `null` when none exists.
     */
    fun pickBaseBranch(refs: List<BranchRef>): BranchRef? {
        val candidates = listOf("main", "master", "develop")
        return candidates.firstNotNullOfOrNull { c -> refs.firstOrNull { !it.isRemote && it.name == c } }
            ?: candidates.firstNotNullOfOrNull { c -> refs.firstOrNull { it.isRemote && it.name == "origin/$c" } }
    }
}
