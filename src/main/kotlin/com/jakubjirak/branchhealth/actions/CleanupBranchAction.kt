package com.jakubjirak.branchhealth.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.jakubjirak.branchhealth.git.GitOutputParsers
import com.jakubjirak.branchhealth.settings.BranchHealthSettings
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler

/**
 * Deletes local branches that are fully merged into HEAD, after a confirmation that lists them.
 * `main`, `master`, `develop`, the ignored branches from settings, the current branch and branches
 * checked out in other worktrees are never offered. Deletion uses `git branch -d`, which refuses
 * anything that is not merged.
 */
class CleanupBranchAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val root = GitUtil.getRepositories(project).firstOrNull()?.root
        if (root == null) {
            Messages.showInfoMessage(project, "No Git repositories found.", TITLE)
            return
        }

        val merged = ProgressManager.getInstance().runProcessWithProgressSynchronously<List<String>?, RuntimeException>(
            { findMergedBranches(project, root) }, "Finding Merged Branches", true, project,
        ) ?: return

        if (merged.isEmpty()) {
            Messages.showInfoMessage(project, "No merged branches found to clean up.", TITLE)
            return
        }

        val confirmation = Messages.showYesNoDialog(
            project,
            "Delete the following branches that are already merged into HEAD?\n\n${merged.joinToString("\n")}",
            "Confirm Branch Cleanup",
            Messages.getWarningIcon(),
        )
        if (confirmation != Messages.YES) return

        val deleted = ProgressManager.getInstance().runProcessWithProgressSynchronously<Int, RuntimeException>(
            { deleteBranches(project, root, merged) }, "Deleting Branches", false, project,
        )
        Messages.showInfoMessage(project, "Deleted $deleted of ${merged.size} branch(es).", "Branch Cleanup Complete")
    }

    private fun findMergedBranches(project: Project, root: VirtualFile): List<String>? {
        val handler = GitLineHandler(project, root, GitCommand.BRANCH)
        handler.addParameters("--merged", "HEAD")
        val result = Git.getInstance().runCommand(handler)
        if (!result.success()) {
            LOG.warn("Failed to list merged branches: ${result.errorOutputAsJoinedString}")
            return null
        }
        val protected = GitOutputParsers.PROTECTED_BRANCHES + BranchHealthSettings.getInstance().getIgnoredBranchList()
        return GitOutputParsers.parseMergedBranches(result.output, protected)
    }

    private fun deleteBranches(project: Project, root: VirtualFile, branches: List<String>): Int =
        branches.count { branch ->
            val handler = GitLineHandler(project, root, GitCommand.BRANCH)
            handler.addParameters("-d", branch)
            val result = Git.getInstance().runCommand(handler)
            if (!result.success()) LOG.warn("Failed to delete branch $branch: ${result.errorOutputAsJoinedString}")
            result.success()
        }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        private val LOG = Logger.getInstance(CleanupBranchAction::class.java)
        private const val TITLE = "Branch Cleanup"
    }
}
