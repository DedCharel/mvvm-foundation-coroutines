package com.example.foundation.sideeffects.dialogs

import com.example.foundation.model.tasks.Task
import com.example.foundation.sideeffects.dialogs.plugin.DialogConfig

/**
 * Side-effects interface for managing dialogs from view-model.
 * You need to add [DialogsPlugin] to your activity before using this feature.
 *
 * WARN! Please note, dialogs don't survive after app killing.
 */
interface Dialogs {

    /**
     * Show alert dialog to the user and wait for the user choice.
     */
    fun show(dialogConfig: DialogConfig): Task<Boolean>

}