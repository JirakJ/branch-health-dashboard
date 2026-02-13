package com.jakubjirak.branchhealth.actions

import com.jakubjirak.branchhealth.engine.BranchHealthEngine
import com.jakubjirak.branchhealth.model.AheadBehind
import com.jakubjirak.branchhealth.model.BranchInfo
import com.jakubjirak.branchhealth.notifications.BranchHealthNotifier
import com.jakubjirak.branchhealth.settings.BranchHealthSettings
import com.jakubjirak.branchhealth.ui.BranchHealthPanel
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.wm.ToolWindowManager
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import java.time.Instant

class ScanBranchesAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Scanning Branch Health", true) {
            override fun run(indicator: ProgressIndicator) {
                try {
                    indicator.text = "Collecting branch information..."
                    indicator.fraction = 0.1

                    val repositories = GitUtil.getRepositories(project)
                    if (repositories.isEmpty()) {
                        LOG.info("No Git repositories found in project")
                        return
                    }

                    val repo = repositories.first()
                    val git = Git.getInstance()
                    val settings = BranchHealthSettings.getInstance()
                    val ignoredBranches = settings.getIgnoredBranchList()
                    val engine = BranchHealthEngine(settings.staleThresholdDays.toLong())

                    // Collect branch information using git commands
                    val handler = GitLineHandler(project, repo.root, GitCommand.BRANCH)
                    handler.addParameters("-a", "--format=%(refname:short)|%(committerdate:iso)|%(subject)|%(authorname)")
                    val result = git.runCommand(handler)

                    if (!result.success()) {
                        LOG.warn("Failed to list branches: ${result.errorOutputAsJoinedString}")
                        return
                    }

                    indicator.text = "Analyzing branches..."
                    indicator.fraction = 0.4

                    val branches = result.output.mapNotNull { line ->
                        parseBranchLine(line, ignoredBranches)
                    }

                    indicator.text = "Calculating health scores..."
                    indicator.fraction = 0.7

                    val results = engine.analyzeAll(branches)

                    indicator.text = "Updating UI..."
                    indicator.fraction = 0.9

                    // Update tool window
                    com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Branch Health")
                        val content = toolWindow?.contentManager?.getContent(0)
                        val panel = content?.component as? BranchHealthPanel
                        panel?.updateResults(results)
                    }

                    // Send notifications
                    val notifier = BranchHealthNotifier(project)
                    notifier.notifyScanComplete(results)

                    val staleBranches = results.filter {
                        it.level == com.jakubjirak.branchhealth.model.HealthLevel.STALE ||
                            it.level == com.jakubjirak.branchhealth.model.HealthLevel.DEAD
                    }
                    if (staleBranches.isNotEmpty()) {
                        notifier.notifyStaleBranches(staleBranches, settings.staleThresholdDays)
                    }

                    LOG.info("Branch health scan complete: ${results.size} branches analyzed")
                } catch (e: Exception) {
                    LOG.warn("Branch health scan failed", e)
                }
            }
        })
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        private val LOG = Logger.getInstance(ScanBranchesAction::class.java)

        internal fun parseBranchLine(line: String, ignoredBranches: List<String>): BranchInfo? {
            val parts = line.split("|")
            if (parts.size < 4) return null

            val name = parts[0].trim().removePrefix("remotes/origin/")
            if (name.isEmpty() || name == "HEAD") return null
            if (ignoredBranches.any { name.equals(it, ignoreCase = true) }) return null

            val dateStr = parts[1].trim()
            val lastCommitDate = try {
                Instant.parse(dateStr.replace(" ", "T").replace(Regex("\\+(..)(..)$"), "+$1:$2"))
            } catch (_: Exception) {
                Instant.now()
            }

            val author = parts[3].trim()
            val isRemote = line.trimStart().startsWith("remotes/")

            return BranchInfo(
                name = name,
                lastCommitDate = lastCommitDate,
                commitCount = 0,
                author = author,
                isRemote = isRemote,
                isStale = false,
                aheadBehind = AheadBehind(0, 0),
            )
        }
    }
}
