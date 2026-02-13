package com.jakubjirak.branchhealth.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class BranchHealthConfigurable : Configurable {

    private var staleThresholdSpinner: JSpinner? = null
    private var scanIntervalSpinner: JSpinner? = null
    private var ignoredBranchesField: JBTextField? = null
    private var notificationsCheckbox: JBCheckBox? = null
    private var mainPanel: JPanel? = null

    override fun getDisplayName(): String = "Branch Health Dashboard"

    override fun createComponent(): JComponent {
        val settings = BranchHealthSettings.getInstance()

        staleThresholdSpinner = JSpinner(SpinnerNumberModel(settings.staleThresholdDays, 1, 365, 1))
        scanIntervalSpinner = JSpinner(SpinnerNumberModel(settings.scanIntervalMinutes, 1, 1440, 5))

        ignoredBranchesField = JBTextField(settings.ignoredBranches).apply {
            emptyText.text = "main, develop, master"
        }

        notificationsCheckbox = JBCheckBox("Enable notifications", settings.enableNotifications)

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Stale branch threshold (days):"), staleThresholdSpinner!!)
            .addLabeledComponent(JBLabel("Scan interval (minutes):"), scanIntervalSpinner!!)
            .addLabeledComponent(JBLabel("Ignored branches (comma-separated):"), ignoredBranchesField!!)
            .addComponent(notificationsCheckbox!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        return mainPanel!!
    }

    override fun isModified(): Boolean {
        val settings = BranchHealthSettings.getInstance()
        if ((staleThresholdSpinner?.value as? Int) != settings.staleThresholdDays) return true
        if ((scanIntervalSpinner?.value as? Int) != settings.scanIntervalMinutes) return true
        if (ignoredBranchesField?.text != settings.ignoredBranches) return true
        return notificationsCheckbox?.isSelected != settings.enableNotifications
    }

    override fun apply() {
        val settings = BranchHealthSettings.getInstance()
        settings.staleThresholdDays = staleThresholdSpinner?.value as? Int ?: BranchHealthSettings.DEFAULT_STALE_THRESHOLD_DAYS
        settings.scanIntervalMinutes = scanIntervalSpinner?.value as? Int ?: BranchHealthSettings.DEFAULT_SCAN_INTERVAL_MINUTES
        settings.ignoredBranches = ignoredBranchesField?.text ?: BranchHealthSettings.DEFAULT_IGNORED_BRANCHES
        settings.enableNotifications = notificationsCheckbox?.isSelected ?: true
    }

    override fun reset() {
        val settings = BranchHealthSettings.getInstance()
        staleThresholdSpinner?.value = settings.staleThresholdDays
        scanIntervalSpinner?.value = settings.scanIntervalMinutes
        ignoredBranchesField?.text = settings.ignoredBranches
        notificationsCheckbox?.isSelected = settings.enableNotifications
    }

    override fun disposeUIResources() {
        staleThresholdSpinner = null
        scanIntervalSpinner = null
        ignoredBranchesField = null
        notificationsCheckbox = null
        mainPanel = null
    }
}
