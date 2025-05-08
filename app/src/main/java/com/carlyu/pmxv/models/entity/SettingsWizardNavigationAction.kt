package com.carlyu.pmxv.models.entity


sealed class SettingsWizardNavigationAction {

    object Next : SettingsWizardNavigationAction()
    object Back : SettingsWizardNavigationAction()

}