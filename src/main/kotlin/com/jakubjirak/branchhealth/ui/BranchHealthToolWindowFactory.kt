package com.jakubjirak.branchhealth.ui

import com.jakubjirak.branchhealth.model.BranchHealth
import com.jakubjirak.branchhealth.model.HealthLevel
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class BranchHealthToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = BranchHealthPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "Branch Health", false)
        toolWindow.contentManager.addContent(content)
    }
}

/**
 * Pure table-row logic for the Branch Health tool window, kept separate from Swing for testing.
 */
object BranchHealthRows {
    val COLUMNS = arrayOf("Branch", "Status", "Health", "Age (days)", "Commits/day (30d)", "Ahead/Behind", "Conflict Risk", "Advice")
    val COLUMN_CLASSES: Array<Class<*>> = arrayOf(
        String::class.java, String::class.java, Integer::class.java, java.lang.Long::class.java,
        java.lang.Double::class.java, String::class.java, String::class.java, String::class.java,
    )
    val FILTERS = arrayOf("All") + HealthLevel.entries.map { it.displayName }

    fun filter(results: List<BranchHealth>, selected: String?): List<BranchHealth> {
        val level = HealthLevel.entries.firstOrNull { it.displayName == selected } ?: return results
        return results.filter { it.level == level }
    }

    fun toRow(health: BranchHealth): Array<Any> = arrayOf(
        health.branch.name,
        "${health.level.icon} ${health.level.displayName}",
        health.healthScore,
        health.ageDays,
        Math.round(health.activityRate * ROUNDING) / ROUNDING,
        "+${health.branch.aheadBehind.ahead} / -${health.branch.aheadBehind.behind}",
        health.conflictRisk?.let { "${(it.riskScore * PERCENT).toInt()}% (${it.filePaths.size} files)" } ?: "None",
        health.rebaseAdvice?.recommendation?.name ?: "N/A",
    )

    private const val ROUNDING = 100.0
    private const val PERCENT = 100
}

class BranchHealthPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val tableModel: DefaultTableModel
    private val table: JBTable
    private val filterComboBox: JComboBox<String>
    private var allResults: List<BranchHealth> = emptyList()

    init {
        tableModel = object : DefaultTableModel(BranchHealthRows.COLUMNS, 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = false
            override fun getColumnClass(columnIndex: Int): Class<*> = BranchHealthRows.COLUMN_CLASSES[columnIndex]
        }

        table = JBTable(tableModel).apply {
            autoCreateRowSorter = true
            emptyText.text = "No scan results yet. Click Scan Branches."
        }

        filterComboBox = JComboBox(BranchHealthRows.FILTERS)
        filterComboBox.addActionListener { applyFilter() }

        val scanButton = JButton("Scan Branches").apply {
            addActionListener {
                val actionManager = com.intellij.openapi.actionSystem.ActionManager.getInstance()
                val action = actionManager.getAction("com.jakubjirak.branchhealth.ScanBranchesAction")
                if (action != null) {
                    actionManager.tryToExecute(
                        action, null, this@BranchHealthPanel,
                        com.intellij.openapi.actionSystem.ActionPlaces.TOOLWINDOW_CONTENT, true,
                    )
                }
            }
        }

        val toolbar = JPanel().apply {
            add(JBLabel("Filter: "))
            add(filterComboBox)
            add(scanButton)
        }

        add(toolbar, BorderLayout.NORTH)
        add(JBScrollPane(table), BorderLayout.CENTER)
    }

    fun updateResults(results: List<BranchHealth>) {
        allResults = results
        applyFilter()
    }

    private fun applyFilter() {
        tableModel.rowCount = 0
        BranchHealthRows.filter(allResults, filterComboBox.selectedItem as? String)
            .forEach { tableModel.addRow(BranchHealthRows.toRow(it)) }
    }
}
