package com.jakubjirak.branchhealth.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler

class CleanupBranchAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val mergedBranches = findMergedBranches(project) ?: return

        if (mergedBranches.isEmpty()) {
            Messages.showInfoMessage(project, "No stale branches found to clean up.", "Branch Cleanup")
            return
        }

        val branchList = mergedBranches.joinToString("\n")
        val confirmation = Messages.showYesNoDialog(
            project,
            "Are you sure you want to delete the following stale branches?\n\n$branchList",
            "Confirm Branch Cleanup",
            Messages.getWarningIcon(),
        )

        if (confirmation != Messages.YES) return

        val deletedCount = deleteBranches(project, mergedBranches)
        Messages.showInfoMessage(project, "Successfully deleted $deletedCount branch(es).", "Branch Cleanup Complete")
    }

    private fun findMergedBranches(project: Project): List<String>? {
        val repositories = GitUtil.getRepositories(project)
        if (repositories.isEmpty()) {
            Messages.showInfoMessage(project, "No Git repositories found.", "Branch Cleanup")
            return null
        }

        val repo = repositories.first()
        val handler = GitLineHandler(project, repo.root, GitCommand.BRANCH)
        handler.addParameters("--merged", "HEAD")
        val result = Git.getInstance().runCommand(handler)

        if (!result.success()) {
            LOG.warn("Failed to list merged branches: ${result.errorOutputAsJoinedString}")
            return null
        }

        return result.output
            .map { it.trim() }
            .filter { !it.startsWith("*") && it != "main" && it != "master" && it != "develop" }
    }

    private fun deleteBranches(project: Project, branches: List<String>): Int {
        val repositories = GitUtil.getRepositories(project)
        if (repositories.isEmpty()) return 0
        val repo = repositories.first()
        val git = Git.getInstance()
        var deletedCount = 0

        for (branch in branches) {
            try {
                val deleteHandler = GitLineHandler(project, repo.root, GitCommand.BRANCH)
                deleteHandler.addParameters("-d", branch)
                val deleteResult = git.runCommand(deleteHandler)
                if (deleteResult.success()) {
                    deletedCount++
                    LOG.info("Deleted branch: $branch")
                } else {
                    LOG.warn("Failed to delete branch $branch: ${deleteResult.errorOutputAsJoinedString}")
                }
            } catch (ex: Exception) {
                LOG.warn("Error deleting branch $branch", ex)
            }
        }
        return deletedCount
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        private val LOG = Logger.getInstance(CleanupBranchAction::class.java)
    }
}
