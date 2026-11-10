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
import java.awt.Component
import java.util.Locale
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class BranchHealthToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = BranchHealthPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "Branch Health", false)
        toolWindow.contentManager.addContent(content)
    }
}

class BranchHealthPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val tableModel: DefaultTableModel
    private val table: JBTable
    private val filterComboBox: JComboBox<String>
    private var allResults: List<BranchHealth> = emptyList()

    init {
        val columnNames = arrayOf<Any>("Branch", "Health", "Age (days)", "Activity", "Conflict Risk", "Advice")
        tableModel = object : DefaultTableModel(columnNames, 0) {
            override fun isCellEditable(row: Int, column: Int): Boolean = false
        }

        table = JBTable(tableModel).apply {
            columnModel.getColumn(1).cellRenderer = HealthScoreRenderer()
            autoCreateRowSorter = true
        }

        // Filter toolbar
        filterComboBox = JComboBox(arrayOf("All", "Healthy", "Aging", "Stale", "Dead"))
        filterComboBox.addActionListener { applyFilter() }

        val scanButton = JButton("Scan Branches").apply {
            addActionListener {
                val actionManager = com.intellij.openapi.actionSystem.ActionManager.getInstance()
                val action = actionManager.getAction("com.jakubjirak.branchhealth.ScanBranchesAction")
                if (action != null) {
                    actionManager.tryToExecute(action, null, null, com.intellij.openapi.actionSystem.ActionPlaces.TOOLWINDOW_CONTENT, true)
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
        val selectedFilter = filterComboBox.selectedItem as? String ?: "All"
        val filtered = when (selectedFilter) {
            "Healthy" -> allResults.filter { it.level == HealthLevel.HEALTHY }
            "Aging" -> allResults.filter { it.level == HealthLevel.AGING }
            "Stale" -> allResults.filter { it.level == HealthLevel.STALE }
            "Dead" -> allResults.filter { it.level == HealthLevel.DEAD }
            else -> allResults
        }

        tableModel.rowCount = 0
        filtered.forEach { health ->
            tableModel.addRow(
                arrayOf<Any>(
                    health.branch.name,
                    "${health.level.icon} ${health.healthScore}",
                    health.ageDays.toString(),
                    String.format(Locale.US, "%.2f/day", health.activityRate),
                    health.conflictRisk?.let { "${(it.riskScore * 100).toInt()}%" } ?: "N/A",
                    health.rebaseAdvice?.recommendation?.name ?: "N/A",
                ),
            )
        }
    }
}

private class HealthScoreRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int,
    ): Component {
        val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        horizontalAlignment = CENTER
        return component
    }
}
