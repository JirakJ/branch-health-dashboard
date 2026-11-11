package com.jakubjirak.branchhealth.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "BranchHealthSettings",
    storages = [Storage(value = "branchHealthSettings.xml", roamingType = RoamingType.DEFAULT)],
)
class BranchHealthSettings : PersistentStateComponent<BranchHealthSettings> {

    var staleThresholdDays: Int = DEFAULT_STALE_THRESHOLD_DAYS
    var ignoredBranches: String = DEFAULT_IGNORED_BRANCHES
    var enableNotifications: Boolean = true

    override fun getState(): BranchHealthSettings = this

    override fun loadState(state: BranchHealthSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun getIgnoredBranchList(): List<String> {
        return ignoredBranches.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    companion object {
        const val DEFAULT_STALE_THRESHOLD_DAYS = 30
        const val DEFAULT_IGNORED_BRANCHES = "main, develop, master"

        fun getInstance(): BranchHealthSettings =
            ApplicationManager.getApplication().getService(BranchHealthSettings::class.java)
    }
}
