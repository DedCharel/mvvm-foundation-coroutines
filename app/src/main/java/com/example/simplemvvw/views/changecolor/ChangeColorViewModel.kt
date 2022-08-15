package com.example.simplemvvw.views.changecolor

import androidx.lifecycle.*
import com.example.foundation.model.*
import com.example.foundation.sideeffects.navigator.Navigator
import com.example.simplemvvw.R
import com.example.simplemvvw.model.colors.ColorsRepository
import com.example.simplemvvw.model.colors.NamedColor
import com.example.foundation.sideeffects.resources.Resources
import com.example.foundation.sideeffects.toasts.Toasts
import com.example.foundation.utils.finiteShareIn
import com.example.foundation.views.BaseViewModel
import com.example.foundation.views.ResultFlow

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ChangeColorViewModel(
    screen: ChangeColorFragment.Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
): BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId = savedStateHandle.getStateFlow("currentColorId", screen.currentColorId)
    private val _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)

    // main destination (contains merged values from _availableColors & _currentColorId)

    val viewState: ResultFlow<ViewState> = combine(
        _availableColors,
        _currentColorId,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )


    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = viewState.map{ result ->
        return@map if (result is SuccessResult){
            val currentColor = result.data.colorList.first(){it.selected}
            resources.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            resources.getString(R.string.change_color_screen_title_simple)
        }
    }.asLiveData()

    init {
        load()

    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_instantSaveInProgress.value.isInProgress()) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch{
        try {
            _instantSaveInProgress.value = PercentageProgress.START
            _sampledSaveInProgress.value = PercentageProgress.START
            val currentColorId =_currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)

            val flow = colorsRepository.setCurrentColor(currentColor)
                .finiteShareIn(this)

            val instantJob = async {
                flow.collect { percentage ->
                    _instantSaveInProgress.value = PercentageProgress(percentage)
                }
            }

            val sampledJob = async {
                flow.sample(200).collect { percentage ->
                    _sampledSaveInProgress.value = PercentageProgress(percentage)
                }
            }

            instantJob.await()
            sampledJob.await()

            navigator.goBack(currentColor)
        } catch (e:Exception) {
            if (e !is CancellationException) toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
        }

    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain(){
       load()
    }

    /**
     * [MediatorLiveData] can listen other LiveData instances (even more than 1)
     * and combine their values.
     * Here we listen the list of available colors ([_availableColors] live-data) + current color id
     * ([_currentColorId] live-data), then we use both of these values in order to create a list of
     * [NamedColorListItem], it is a list to be displayed in RecyclerView.
     */
    private fun mergeSources(colors: Result<List<NamedColor>>, currentColorId: Long,
                             instantSaveInProgress: Progress, sampledSaveInProgress: Progress): Result<ViewState> {

        return colors.map { colorsList ->
            ViewState(
                colorList = colorsList.map { NamedColorListItem(it, currentColorId == it.id)},
                showSaveButton = !instantSaveInProgress.isInProgress(),
                showCancelButton = !instantSaveInProgress.isInProgress(),
                showSaveProgressBar = instantSaveInProgress.isInProgress(),

                saveProgressPercentage = instantSaveInProgress.getPercentage(),
                saveProgressPercentageMessage = resources.getString(R.string.percentage_value, sampledSaveInProgress.getPercentage())
            )
        }
    }

    private fun load()  = into(_availableColors){
        return@into colorsRepository.getAvailableColors()
    }



    data class ViewState(
        val colorList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean,

        val saveProgressPercentage: Int,
        val saveProgressPercentageMessage: String
    )
}