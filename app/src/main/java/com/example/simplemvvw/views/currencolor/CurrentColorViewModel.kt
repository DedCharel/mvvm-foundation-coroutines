package com.example.simplemvvw.views.currencolor


import android.Manifest
import com.example.foundation.model.PendingResult
import com.example.foundation.model.SuccessResult
import com.example.foundation.model.takeSuccess
import com.example.simplemvvw.R
import com.example.simplemvvw.model.colors.ColorListener
import com.example.simplemvvw.model.colors.ColorsRepository
import com.example.simplemvvw.model.colors.NamedColor
import com.example.foundation.sideeffects.dialogs.Dialogs
import com.example.foundation.sideeffects.dialogs.plugin.DialogConfig
import com.example.foundation.sideeffects.intents.Intents
import com.example.foundation.sideeffects.navigator.Navigator
import com.example.foundation.sideeffects.permission.Permissions
import com.example.foundation.sideeffects.permission.plugin.PermissionStatus
import com.example.foundation.sideeffects.resources.Resources
import com.example.foundation.sideeffects.toasts.Toasts
import com.example.foundation.views.BaseViewModel
import com.example.foundation.views.LiveResult
import com.example.foundation.views.MutableLiveResult
import com.example.simplemvvw.views.changecolor.ChangeColorFragment
import kotlinx.coroutines.*

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val permissions: Permissions,
    private val intents: Intents,
    private val dialogs: Dialogs,
    private val colorsRepository: ColorsRepository
): BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }

    // --- example of listening results via model layer

    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)

//        viewModelScope.launch {
//            delay(1000)
//
//            val result = withContext(Dispatchers.Default){
//                val part1 = async {
//                    delay(1000)
//                    return@async "Part 1 done"
//                }
//                val part2 = async {
//                    delay(2000)
//                    return@async "Part 2 done"
//                }
//                val part3 = async {
//                    delay(3000)
//                    return@async "Part 3 done"
//                }
//
//                val result1 = part1.await()
//                val result2 = part2.await()
//                val result3 = part3.await()
//                return@withContext "$result1\n$result2\n$result3"
//            }
//            Log.d("SomeTag", "Result: $result")
//        }
    }

    // --- example of listening results directly from the screen

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = resources.getString(R.string.changed_color, result.name)
            toasts.toast(message)
        }
    }

    // ---

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    /**
     * Example of using side-effect plugins
     */
    fun requestPermission() = viewModelScope.launch{
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val hasPermission = permissions.hasPermissions(permission)
        if (hasPermission) {
            dialogs.show(createPermissionAlreadyGrantedDialog())
        } else {
            when (permissions.requestPermission(permission)) {
                PermissionStatus.GRANTED -> {
                    toasts.toast(resources.getString(R.string.permissions_grated))
                }
                PermissionStatus.DENIED -> {
                    toasts.toast(resources.getString(R.string.permissions_denied))
                }
                PermissionStatus.DENIED_FOREVER -> {
                    if (dialogs.show(createAskForLaunchingAppSettingsDialog())) {
                        intents.openAppSettings()
                    }
                }
            }
        }
    }

    fun tryAgain(){
        load()
    }

    private fun load() = into(_currentColor){
        return@into colorsRepository.getCurrentColor()
    }

    private fun createPermissionAlreadyGrantedDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.permissions_already_granted),
        positiveButton = resources.getString(R.string.action_ok)
    )

    private fun createAskForLaunchingAppSettingsDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.open_app_settings_message),
        positiveButton = resources.getString(R.string.action_open),
        negativeButton = resources.getString(R.string.action_cancel)
    )
}