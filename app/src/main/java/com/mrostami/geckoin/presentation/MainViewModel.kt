package com.mrostami.geckoin.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.domain.usecases.ThemeConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeConfigUseCase: ThemeConfigUseCase
) : ViewModel() {

    init {
        getThemeMode()
    }

    val fragName: MutableLiveData<String> = MutableLiveData("Home")
    var selectedThemeMode: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private val _appThemeChanged = Channel<Boolean>(Channel.BUFFERED)
    val appThemeChanged = _appThemeChanged.receiveAsFlow()
    fun getThemeMode() {
        viewModelScope.launch {
            themeConfigUseCase.getThemeMode().collectLatest { mode ->
                if (selectedThemeMode != mode) {
                    selectedThemeMode = mode
                    _appThemeChanged.send(true)
                }
            }
        }
    }
}