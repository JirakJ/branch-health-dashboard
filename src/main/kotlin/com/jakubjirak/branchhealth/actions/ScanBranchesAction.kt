package com.jakubjirak.branchhealth.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.jakubjirak.branchhealth.engine.BranchHealthEngine
import com.jakubjirak.branchhealth.git.BranchRef
import com.jakubjirak.branchhealth.git.GitOutputParsers
import com.jakubjirak.branchhealth.model.AheadBehind
import com.jakubjirak.branchhealth.model.BranchHealth
import com.jakubjirak.branchhealth.model.BranchInfo
import com.jakubjirak.branchhealth.model.HealthLevel
import com.jakubjirak.branchhealth.notifications.BranchHealthNotifier
import com.jakubjirak.branchhealth.settings.BranchHealthSettings
import com.jakubjirak.branchhealth.ui.BranchHealthPanel
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler

/**
 * Scans all local and remote-tracking branches of the project's first Git repository and scores them.
 */
class ScanBranchesAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Scanning Branch Health", true) {
            private var results: List<BranchHealth> = emptyList()

            override fun run(indicator: ProgressIndicator) {
                val repo = GitUtil.getRepositories(project).firstOrNull()
                if (repo == null) {
                    LOG.info("No Git repositories found in project")
                    return
                }
                results = scan(project, repo.root, indicator)
                LOG.info("Branch health scan complete: ${results.size} branches analyzed")
            }

            override fun onSuccess() {
                val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Branch Health")
                (toolWindow?.contentManager?.getContent(0)?.component as? BranchHealthPanel)?.updateResults(results)

                val settings = BranchHealthSettings.getInstance()
                val notifier = BranchHealthNotifier(project)
                notifier.notifyScanComplete(results)
                val stale = results.filter { it.level == HealthLevel.STALE || it.level == HealthLevel.DEAD }
                notifier.notifyStaleBranches(stale, settings.staleThresholdDays)
            }
        })
    }

    private fun scan(project: Project, root: VirtualFile, indicator: ProgressIndicator): List<BranchHealth> {
        val settings = BranchHealthSettings.getInstance()
        val engine = BranchHealthEngine(settings.staleThresholdDays.toLong())
        val git = GitRunner(project, root)

        indicator.text = "Collecting branch information..."
        val allRefs = git.run(GitCommand.FOR_EACH_REF, "--format=${GitOutputParsers.BRANCH_FORMAT}", "refs/heads", "refs/remotes")
            ?.mapNotNull { GitOutputParsers.parseBranchRef(it, emptyList()) }
            ?: return emptyList()
        val base = GitOutputParsers.pickBaseBranch(allRefs)
        val ignored = settings.getIgnoredBranchList()
        val branches = allRefs.filter { it.ref != base?.ref && !GitOutputParsers.isIgnored(it.name, it.isRemote, ignored) }

        return branches.mapIndexed { index, ref ->
            indicator.checkCanceled()
            indicator.text = "Analyzing ${ref.name}..."
            indicator.fraction = index.toDouble() / branches.size.coerceAtLeast(1)
            val baseRef = base?.ref
            val info = toBranchInfo(ref, git, baseRef, engine)
            val changed = baseRef?.let { git.files("$it...${ref.ref}") }.orEmpty()
            val targetChanged = baseRef?.let { git.files("${ref.ref}...$it") }.orEmpty()
            engine.analyzeBranch(info, changed, base?.name ?: "base", targetChanged)
        }
    }

    private fun toBranchInfo(ref: BranchRef, git: GitRunner, baseRef: String?, engine: BranchHealthEngine): BranchInfo {
        val aheadBehind = baseRef
            ?.let { git.run(GitCommand.REV_LIST, "--left-right", "--count", "$it...${ref.ref}") }
            ?.let { GitOutputParsers.parseLeftRightCount(it.joinToString("\n")) }
            ?: AheadBehind(0, 0)
        val recentCommits = git.run(
            GitCommand.REV_LIST, "--count", "--since=${BranchHealthEngine.ACTIVITY_WINDOW_DAYS}.days", ref.ref,
        )?.let { GitOutputParsers.parseCount(it.joinToString("\n")) } ?: 0
        return BranchInfo(
            name = ref.name,
            lastCommitDate = ref.lastCommitDate,
            commitCount = recentCommits,
            author = ref.author,
            isRemote = ref.isRemote,
            isStale = engine.isStale(ref.lastCommitDate),
            aheadBehind = aheadBehind,
        )
    }

    private class GitRunner(private val project: Project, private val root: VirtualFile) {
        fun run(command: GitCommand, vararg params: String): List<String>? {
            val handler = GitLineHandler(project, root, command)
            handler.setSilent(true)
            handler.addParameters(*params)
            val result = Git.getInstance().runCommand(handler)
            if (!result.success()) {
                LOG.debug("git ${command.name()} failed: ${result.errorOutputAsJoinedString}")
                return null
            }
            return result.output
        }

        fun files(range: String): Set<String> =
            run(GitCommand.DIFF, "--name-only", range)?.let(GitOutputParsers::parseFileList).orEmpty()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        private val LOG = Logger.getInstance(ScanBranchesAction::class.java)
    }
}
