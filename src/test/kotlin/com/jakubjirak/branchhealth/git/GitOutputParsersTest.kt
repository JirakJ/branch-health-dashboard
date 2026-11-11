package com.jakubjirak.branchhealth.git

import com.jakubjirak.branchhealth.model.AheadBehind
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class GitOutputParsersTest {

    private fun ref(name: String, remote: Boolean) =
        BranchRef(name, if (remote) "refs/remotes/$name" else "refs/heads/$name", remote, Instant.EPOCH, "a")

    @Test
    fun `parses local branch with offset date`() {
        val r = GitOutputParsers.parseBranchRef("refs/heads/feature/x|2024-01-02T03:04:05+01:00|Alice Doe", emptyList())!!
        assertEquals("feature/x", r.name)
        assertEquals("refs/heads/feature/x", r.ref)
        assertFalse(r.isRemote)
        assertEquals(Instant.parse("2024-01-02T02:04:05Z"), r.lastCommitDate)
        assertEquals("Alice Doe", r.author)
    }

    @Test
    fun `parses remote branch and keeps the remote in the name`() {
        val r = GitOutputParsers.parseBranchRef("refs/remotes/origin/fix|2024-01-02T03:04:05Z|Bob", emptyList())!!
        assertEquals("origin/fix", r.name)
        assertTrue(r.isRemote)
    }

    @Test
    fun `author containing the separator is kept intact`() {
        val r = GitOutputParsers.parseBranchRef("refs/heads/a|2024-01-02T03:04:05Z|Team | Bot", emptyList())!!
        assertEquals("Team | Bot", r.author)
    }

    @Test
    fun `skips HEAD refs, tags, malformed lines and bad dates`() {
        assertNull(GitOutputParsers.parseBranchRef("refs/remotes/origin/HEAD|2024-01-02T03:04:05Z|x", emptyList()))
        assertNull(GitOutputParsers.parseBranchRef("refs/tags/v1|2024-01-02T03:04:05Z|x", emptyList()))
        assertNull(GitOutputParsers.parseBranchRef("garbage", emptyList()))
        assertNull(GitOutputParsers.parseBranchRef("refs/heads/a|2024-01-02 03:04:05 +0100|x", emptyList()))
    }

    @Test
    fun `ignored list matches local and remote short names case-insensitively`() {
        val ignored = listOf("Develop")
        assertNull(GitOutputParsers.parseBranchRef("refs/heads/develop|2024-01-02T03:04:05Z|x", ignored))
        assertNull(GitOutputParsers.parseBranchRef("refs/remotes/origin/develop|2024-01-02T03:04:05Z|x", ignored))
        assertTrue(GitOutputParsers.isIgnored("origin/develop", true, listOf("origin/develop")))
        assertFalse(GitOutputParsers.isIgnored("feature/develop", false, ignored))
    }

    @Test
    fun `left-right count is behind then ahead`() {
        assertEquals(AheadBehind(ahead = 5, behind = 3), GitOutputParsers.parseLeftRightCount("3\t5\n"))
        assertNull(GitOutputParsers.parseLeftRightCount(""))
        assertNull(GitOutputParsers.parseLeftRightCount("3"))
        assertNull(GitOutputParsers.parseLeftRightCount("x\ty"))
    }

    @Test
    fun `count and file list parsing`() {
        assertEquals(12, GitOutputParsers.parseCount(" 12\n"))
        assertEquals(0, GitOutputParsers.parseCount("fatal"))
        assertEquals(setOf("a.kt", "b/c.kt"), GitOutputParsers.parseFileList(listOf("a.kt", " b/c.kt ", "", "a.kt")))
    }

    @Test
    fun `merged branches skip current, worktree, detached and protected`() {
        val lines = listOf(
            "* main", "  feature/done", "+ feature/in-worktree", "  (HEAD detached at abc123)",
            "  Develop", "  hotfix", "",
        )
        val result = GitOutputParsers.parseMergedBranches(lines, GitOutputParsers.PROTECTED_BRANCHES + "hotfix")
        assertEquals(listOf("feature/done"), result)
    }

    @Test
    fun `base branch prefers local main, then master, then origin`() {
        assertEquals("main", GitOutputParsers.pickBaseBranch(listOf(ref("master", false), ref("main", false)))?.name)
        assertEquals("master", GitOutputParsers.pickBaseBranch(listOf(ref("master", false), ref("origin/main", true)))?.name)
        assertEquals("origin/main", GitOutputParsers.pickBaseBranch(listOf(ref("feature", false), ref("origin/main", true)))?.name)
        assertNull(GitOutputParsers.pickBaseBranch(listOf(ref("feature", false))))
    }
}
